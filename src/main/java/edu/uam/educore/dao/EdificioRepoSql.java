package edu.uam.educore.dao;

import edu.uam.educore.db.Conexion;
import edu.uam.educore.db.ConfiguracionBD;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.infraestructura.Edificio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EdificioRepoSql extends Repositorio<Edificio> {

    private final ConfiguracionBD config;

    public EdificioRepoSql(ConfiguracionBD config) {
        this.config = config;
    }

    private Connection abrir() throws Exception {
        return Conexion.getConnection(
                config.url(),
                config.usuario(),
                config.contrasena());
    }

    @Override
    public void guardar(Edificio edificio) throws Exception {

        String sql
                = "INSERT INTO edificio (codigo, nombre) VALUES (?, ?)";

        try (Connection con = abrir(); PreparedStatement ps
                = con.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, edificio.getCodigo());
            ps.setString(2, edificio.getNombre());

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    edificio.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Edificio edificio) throws Exception {

        String sql
                = "UPDATE edificio SET codigo=?, nombre=? WHERE id=?";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, edificio.getCodigo());
            ps.setString(2, edificio.getNombre());
            ps.setInt(3, edificio.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {

        try (Connection con = abrir(); PreparedStatement ps
                = con.prepareStatement(
                        "DELETE FROM edificio WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Edificio> buscarPorId(int id) throws Exception {

        String sql
                = "SELECT id, codigo, nombre "
                + "FROM edificio WHERE id=?";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapearEdificio(rs));
                }

                return Optional.empty();
            }
        }
    }

    @Override
    public List<Edificio> buscarTodos() throws Exception {

        List<Edificio> lista = new ArrayList<>();

        String sql
                = "SELECT id, codigo, nombre "
                + "FROM edificio ORDER BY id";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearEdificio(rs));
            }
        }

        return lista;
    }

    private Edificio mapearEdificio(ResultSet rs) throws Exception {

        int id = rs.getInt("id");
        String codigo = rs.getString("codigo");
        String nombre = rs.getString("nombre");

        Edificio edificio
                = new Edificio(id, codigo, nombre);

        cargarAulas(edificio);

        return edificio;
    }

    private void cargarAulas(Edificio edificio) throws Exception {

        String sql
                = "SELECT id, numero, capacidad, tipo "
                + "FROM aula "
                + "WHERE edificio_id=? "
                + "ORDER BY id";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, edificio.getId());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Aula aula
                            = new Aula(
                                    rs.getInt("id"),
                                    rs.getString("numero"),
                                    rs.getInt("capacidad"),
                                    rs.getString("tipo"),
                                    edificio);

                    edificio.agregarAula(aula);
                }
            }
        }

    }

    public void guardarAula(Aula aula) throws Exception {

        String sql
                = "INSERT INTO aula (numero, capacidad, tipo, edificio_id) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, aula.getNumero());
            ps.setInt(2, aula.getCapacidad());
            ps.setString(3, aula.getTipo());
            ps.setInt(4, aula.getEdificio().getId());

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    aula.setId(claves.getInt(1));
                }
            }
        }
    }

    public void eliminarAula(int aulaId) throws Exception {

        String sql = "DELETE FROM aula WHERE id=?";

        try (Connection con = abrir(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, aulaId);
            ps.executeUpdate();
        }
    }
}
