package asistenciarfid.dao;

import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Grupo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class GrupoDAO {

    // Consulta base: trae el grupo con el nombre y nivel de la asignatura y el nombre del docente.
    // El LEFT JOIN con usuario es porque docente_id puede ser NULL.
    private static final String SQL_BASE
            = "SELECT g.id, g.periodo_id, g.asignatura_id, g.paralelo, g.docente_id, "
            + "a.nombre AS asignatura_nombre, a.nivel AS asignatura_nivel, "
            + "u.nombre AS docente_nombre "
            + "FROM grupo g "
            + "JOIN asignatura a ON a.id = g.asignatura_id "
            + "LEFT JOIN usuario u ON u.id = g.docente_id ";

    // Convierte la fila actual del ResultSet en un Grupo
    private static Grupo armarGrupo(ResultSet rs) throws SQLException {
        Grupo g = new Grupo();
        g.setId(rs.getInt("id"));
        g.setPeriodoId(rs.getInt("periodo_id"));
        g.setAsignaturaId(rs.getInt("asignatura_id"));
        g.setParalelo(rs.getString("paralelo"));
        int docente = rs.getInt("docente_id");
        if (rs.wasNull()) {
            g.setDocenteId(null);
        } else {
            g.setDocenteId(docente);
        }
        g.setAsignaturaNombre(rs.getString("asignatura_nombre"));
        g.setAsignaturaNivel(rs.getInt("asignatura_nivel"));
        g.setDocenteNombre(rs.getString("docente_nombre"));
        return g;
    }

    // Grupos del periodo ACTIVO. Si carreraId o docenteId son null, no se filtra por ellos.
    public static List<Grupo> listar(Integer carreraId, Integer docenteId) {
        List<Grupo> lista = new ArrayList<>();
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return lista;
        }
        try {
            String sql = SQL_BASE
                    + "JOIN periodo p ON p.id = g.periodo_id "
                    + "WHERE p.activo = 1 ";
            if (carreraId != null) {
                sql = sql + "AND a.carrera_id = ? ";
            }
            if (docenteId != null) {
                sql = sql + "AND g.docente_id = ? ";
            }
            sql = sql + "ORDER BY a.nivel, a.nombre, g.paralelo";
            PreparedStatement ps = cc.prepareStatement(sql);
            int posicion = 1;
            if (carreraId != null) {
                ps.setInt(posicion, carreraId);
                posicion++;
            }
            if (docenteId != null) {
                ps.setInt(posicion, docenteId);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarGrupo(rs));
            }
            cc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }

    // Del periodo activo
    public static List<Grupo> listarPorAsignatura(int asignaturaId) {
        List<Grupo> lista = new ArrayList<>();
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return lista;
        }
        try {
            String sql = SQL_BASE
                    + "JOIN periodo p ON p.id = g.periodo_id "
                    + "WHERE p.activo = 1 AND g.asignatura_id = ? "
                    + "ORDER BY g.paralelo";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setInt(1, asignaturaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarGrupo(rs));
            }
            cc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }

    public static Grupo buscarPorId(int id) {
        Grupo g = null;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return null;
        }
        try {
            String sql = SQL_BASE + "WHERE g.id = ?";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                g = armarGrupo(rs);
            }
            cc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return g;
    }

    // El periodo_id sale del periodo ACTIVO (el SELECT devuelve 0 filas si no hay uno activo)
    public static boolean insertar(Grupo g) {
        boolean ok = false;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            String sql = "INSERT INTO grupo (periodo_id, asignatura_id, paralelo, docente_id) "
                    + "SELECT id, ?, ?, ? FROM periodo WHERE activo = 1";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setInt(1, g.getAsignaturaId());
            ps.setString(2, g.getParalelo());
            if (g.getDocenteId() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, g.getDocenteId());
            }
            ok = ps.executeUpdate() > 0;
            cc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ok;
    }

    public static boolean actualizar(Grupo g) {
        boolean ok = false;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            String sql = "UPDATE grupo SET asignatura_id=?, paralelo=?, docente_id=? WHERE id=?";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setInt(1, g.getAsignaturaId());
            ps.setString(2, g.getParalelo());
            if (g.getDocenteId() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, g.getDocenteId());
            }
            ps.setInt(4, g.getId());
            ok = ps.executeUpdate() > 0;
            cc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ok;
    }

    public static boolean eliminar(int id) {
        boolean ok = false;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            String sql = "DELETE FROM grupo WHERE id=?";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setInt(1, id);
            ok = ps.executeUpdate() > 0;
            cc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ok;
    }
}