package edu.uam.educore.dao;

import edu.uam.educore.db.Conexion;
import edu.uam.educore.db.ConfiguracionBD;
import edu.uam.educore.model.academico.Seccion;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.personas.Empleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import edu.uam.educore.enums.TipoPersonal;
import edu.uam.educore.model.personas.Estudiante;
import edu.uam.educore.model.personas.EstudianteBecado;
import edu.uam.educore.model.personas.EstudianteRegular;

public class SeccionRepoSql extends Repositorio<Seccion> {

    private final ConfiguracionBD config;

    public SeccionRepoSql(ConfiguracionBD config) {
        this.config = config;
    }

    private Connection abrir() throws Exception {
        return Conexion.getConnection(
                config.url(),
                config.usuario(),
                config.contrasena());
    }

    @Override
    public void guardar(Seccion seccion) throws Exception {

        String sql
                = "INSERT INTO seccion "
                + "(codigo, nombre, aula_id, docente_id) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, seccion.getCodigo());
            ps.setString(2, seccion.getNombre());
            ps.setInt(3, seccion.getAula().getId());
            ps.setInt(4, seccion.getDocente().getId());

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    seccion.setId(claves.getInt(1));
                }
            }
        }
    }

    public void guardarMatricula(
            int seccionId,
            int estudianteId) throws Exception {

        String sql
                = "INSERT INTO matricula "
                + "(estudiante_id, seccion_id) "
                + "VALUES (?, ?)";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estudianteId);
            ps.setInt(2, seccionId);

            ps.executeUpdate();
        }
    }

    public void eliminarMatricula(
            int seccionId,
            int estudianteId) throws Exception {

        String sql
                = "DELETE FROM matricula "
                + "WHERE seccion_id=? "
                + "AND estudiante_id=?";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seccionId);
            ps.setInt(2, estudianteId);

            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Seccion seccion) throws Exception {

        String sql
                = "UPDATE seccion "
                + "SET codigo=?, nombre=?, aula_id=?, docente_id=? "
                + "WHERE id=?";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, seccion.getCodigo());
            ps.setString(2, seccion.getNombre());
            ps.setInt(3, seccion.getAula().getId());
            ps.setInt(4, seccion.getDocente().getId());
            ps.setInt(5, seccion.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(
                "DELETE FROM seccion WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Seccion> buscarPorId(int id) throws Exception {

        String sql
                = "SELECT id, codigo, nombre, aula_id, docente_id "
                + "FROM seccion WHERE id=?";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapearSeccion(rs));
                }

                return Optional.empty();
            }
        }
    }

    @Override
    public List<Seccion> buscarTodos() throws Exception {

        List<Seccion> lista = new ArrayList<>();

        String sql
                = "SELECT id, codigo, nombre, aula_id, docente_id "
                + "FROM seccion ORDER BY id";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearSeccion(rs));
            }
        }

        return lista;
    }

    private Seccion mapearSeccion(ResultSet rs) throws Exception {

        int id = rs.getInt("id");
        String codigo = rs.getString("codigo");
        String nombre = rs.getString("nombre");
        int aulaId = rs.getInt("aula_id");
        int docenteId = rs.getInt("docente_id");

        Aula aula = buscarAula(aulaId);
        Empleado docente = buscarEmpleado(docenteId);

        if (aula == null) {
            throw new IllegalArgumentException(
                    "No existe aula con ID " + aulaId + ".");
        }

        if (docente == null) {
            throw new IllegalArgumentException(
                    "No existe empleado con ID " + docenteId + ".");
        }

        Seccion seccion = new Seccion(
                id,
                codigo,
                nombre,
                aula,
                docente);

        cargarEstudiantes(seccion);

        return seccion;
    }

    private void cargarEstudiantes(Seccion seccion) throws Exception {

        String sql
                = "SELECT e.id, e.tipo, e.nombre, e.apellidos, "
                + "e.email, e.carnet, e.porcentaje_beca "
                + "FROM matricula m "
                + "INNER JOIN estudiante e "
                + "ON e.id = m.estudiante_id "
                + "WHERE m.seccion_id=? "
                + "ORDER BY e.id";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seccion.getId());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Estudiante estudiante = crearEstudiante(rs);

                    if (estudiante != null) {
                        seccion.agregarEstudiante(estudiante);
                    }
                }
            }
        }
    }

    private Estudiante crearEstudiante(ResultSet rs) throws Exception {

        int id = rs.getInt("id");
        String tipo = rs.getString("tipo");
        String nombre = rs.getString("nombre");
        String apellidos = rs.getString("apellidos");
        String email = rs.getString("email");
        String carnet = rs.getString("carnet");

        if ("BECADO".equalsIgnoreCase(tipo)) {

            double porcentajeBeca
                    = rs.getDouble("porcentaje_beca");

            return new EstudianteBecado(
                    id,
                    nombre,
                    apellidos,
                    email,
                    carnet,
                    porcentajeBeca);
        }

        return new EstudianteRegular(
                id,
                nombre,
                apellidos,
                email,
                carnet);
    }

    private Aula buscarAula(int aulaId) throws Exception {

        String sql
                = "SELECT id, numero, capacidad, tipo, edificio_id "
                + "FROM aula WHERE id=?";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, aulaId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Aula(
                            rs.getInt("id"),
                            rs.getString("numero"),
                            rs.getInt("capacidad"),
                            rs.getString("tipo"),
                            null);
                }
            }
        }

        return null;
    }

    private Empleado buscarEmpleado(int empleadoId) throws Exception {

        String sql
                = "SELECT id, nombre, apellidos, email, salario, "
                + "fecha_ingreso, tipo "
                + "FROM empleado WHERE id=?";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, empleadoId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Empleado(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellidos"),
                            rs.getString("email"),
                            rs.getDouble("salario"),
                            rs.getDate("fecha_ingreso").toLocalDate(),
                            TipoPersonal.valueOf(rs.getString("tipo")));
                }
            }
        }

        return null;
    }
}
