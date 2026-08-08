package edu.uam.educore.socket;

import edu.uam.educore.db.ConfiguracionBD;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import edu.uam.educore.db.Conexion;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * Servidor de Matrícula. Recibe por socket la orden MATRICULAR &lt;archivo&gt;,
 * lee ese CSV del directorio de entrada (una matrícula por renglón:
 * carnet,codigoSeccion) y matricula todo el lote en UNA transacción: si un
 * renglón falla, revierte el lote completo.
 */
public class ServidorMatricula {

    private final ConfiguracionBD config;
    private final Path entradaDir;

    public ServidorMatricula(ConfiguracionBD config, String entradaDir) {
        this.config = config;
        this.entradaDir = Path.of(entradaDir);
    }

    public static void main(String[] args) throws Exception {

        ConfiguracionBD config
                = ConfiguracionBD.desdeArchivo(".env");

        java.util.Properties props = new java.util.Properties();

        try (java.io.InputStream in
                = new java.io.FileInputStream(".env")) {
            props.load(in);
        }

        String entrada = props.getProperty("ENTRADA_DIR");
        int puerto = Integer.parseInt(
                props.getProperty("MATRICULA_PORT"));

        new ServidorMatricula(config, entrada).escuchar(puerto);
    }

    public void escuchar(int puerto) throws IOException {
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Matricula escuchando en " + puerto);
            while (true) {
                try (Socket cliente = servidor.accept(); BufferedReader in
                        = new BufferedReader(
                                new InputStreamReader(cliente.getInputStream(), StandardCharsets.UTF_8)); PrintWriter out
                        = new PrintWriter(cliente.getOutputStream(), true, StandardCharsets.UTF_8)) {
                    atender(in, out);
                } catch (IOException e) {
                    System.err.println("Error atendiendo cliente: " + e.getMessage());
                }
            }
        }
    }

    private void atender(BufferedReader in, PrintWriter out) throws IOException {
        String linea = in.readLine();
        if (linea == null || !linea.startsWith("MATRICULAR ")) {
            out.println("400 comando invalido");
            return;
        }
        String archivo = linea.substring("MATRICULAR ".length()).trim();
        try {
            int k = procesarLote(archivo);
            out.println("201 " + k);
        } catch (Exception e) {
            out.println("400 " + e.getMessage());
        }
    }

    /**
     * TODO(estudiante · T4/T5): matricular el lote en UNA transacción.
     *
     * <p>
     * Pasos:
     *
     * <ol>
     * <li>Leer el CSV de entrada (entradaDir.resolve(archivo)) con un
     * BufferedReader; cada renglón es "carnet,codigoSeccion".
     * <li>Abrir conexión y con.setAutoCommit(false).
     * <li>Por cada renglón: buscar el estudiante por carnet (si no existe, es
     * un error), buscar la sección por código y su cupo (aula.capacidad),
     * validar cupo y duplicado, e insertar en matricula.
     * <li>Si todo pasa: con.commit() y devolver la cantidad. Si algo falla:
     * con.rollback() y relanzar.
     * </ol>
     *
     * <p>
     * Cómo distinguir los cuatro casos de error (carnet inexistente, sección
     * inexistente, cupo lleno, matrícula duplicada) queda a su criterio de
     * diseño — no hay una jerarquía de excepciones provista. Ver "Puntos extra"
     * en el enunciado si quieren diseñar la suya.
     *
     * <p>
     * Referencia del patrón JDBC: EstudianteRepoSql.
     */
    private record SeccionInfo(
            int id,
            int capacidad) {

    }

    private int procesarLote(String archivo) throws Exception {

        Path ruta = entradaDir.resolve(archivo);

        if (!Files.exists(ruta)) {
            throw new IllegalArgumentException(
                    "No existe el archivo: " + archivo);
        }

        List<String> lineas = Files.readAllLines(
                ruta,
                StandardCharsets.UTF_8);

        if (lineas.isEmpty()) {
            throw new IllegalArgumentException(
                    "El archivo CSV está vacío.");
        }

        try (Connection con = Conexion.getConnection(
                config.url(),
                config.usuario(),
                config.contrasena())) {

            con.setAutoCommit(false);

            try {

                int procesadas = 0;

                for (String linea : lineas) {

                    linea = linea.trim();

                    if (linea.isEmpty()) {
                        continue;
                    }

                    String[] partes = linea.split(",");

                    if (partes.length != 2) {
                        throw new IllegalArgumentException(
                                "Formato inválido. Se esperaba: carnet,codigoSeccion");
                    }

                    String carnet = partes[0].trim();
                    String codigoSeccion = partes[1].trim();

                    if (carnet.isEmpty() || codigoSeccion.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Carnet y código de sección son obligatorios.");
                    }

                    // 1. Buscar estudiante por carnet
                    int estudianteId = buscarEstudiante(
                            con,
                            carnet);

                    // 2. Buscar sección y obtener su capacidad
                    SeccionInfo seccion = buscarSeccion(
                            con,
                            codigoSeccion);

                    // 3. Verificar que no esté ya matriculado
                    if (yaMatriculado(
                            con,
                            estudianteId,
                            seccion.id())) {

                        throw new IllegalArgumentException(
                                "El estudiante con carnet "
                                + carnet
                                + " ya está matriculado en la sección "
                                + codigoSeccion
                                + ".");
                    }

                    // 4. Verificar cupo
                    int matriculados = contarMatriculados(
                            con,
                            seccion.id());

                    if (matriculados >= seccion.capacidad()) {

                        throw new IllegalArgumentException(
                                "La sección "
                                + codigoSeccion
                                + " no tiene cupo disponible.");
                    }

                    // 5. Insertar matrícula
                    insertarMatricula(
                            con,
                            estudianteId,
                            seccion.id());

                    procesadas++;
                }

                con.commit();

                return procesadas;

            } catch (Exception e) {

                con.rollback();

                throw e;
            }
        }
    }

    private int buscarEstudiante(
            Connection con,
            String carnet) throws Exception {

        String sql
                = "SELECT id "
                + "FROM estudiante "
                + "WHERE carnet=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, carnet);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        throw new IllegalArgumentException(
                "No existe estudiante con carnet " + carnet + ".");
    }

    private SeccionInfo buscarSeccion(
            Connection con,
            String codigoSeccion) throws Exception {

        String sql
                = "SELECT s.id, a.capacidad "
                + "FROM seccion s "
                + "INNER JOIN aula a ON a.id = s.aula_id "
                + "WHERE s.codigo=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoSeccion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new SeccionInfo(
                            rs.getInt("id"),
                            rs.getInt("capacidad"));
                }
            }
        }

        throw new IllegalArgumentException(
                "No existe sección con código "
                + codigoSeccion + ".");
    }

    private boolean yaMatriculado(
            Connection con,
            int estudianteId,
            int seccionId) throws Exception {

        String sql
                = "SELECT COUNT(*) "
                + "FROM matricula "
                + "WHERE estudiante_id=? "
                + "AND seccion_id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estudianteId);
            ps.setInt(2, seccionId);

            try (ResultSet rs = ps.executeQuery()) {

                rs.next();

                return rs.getInt(1) > 0;
            }
        }
    }

    private int contarMatriculados(
            Connection con,
            int seccionId) throws Exception {

        String sql
                = "SELECT COUNT(*) "
                + "FROM matricula "
                + "WHERE seccion_id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seccionId);

            try (ResultSet rs = ps.executeQuery()) {

                rs.next();

                return rs.getInt(1);
            }
        }
    }

    private void insertarMatricula(
            Connection con,
            int estudianteId,
            int seccionId) throws Exception {

        String sql
                = "INSERT INTO matricula "
                + "(estudiante_id, seccion_id) "
                + "VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estudianteId);
            ps.setInt(2, seccionId);

            ps.executeUpdate();
        }
    }

}
