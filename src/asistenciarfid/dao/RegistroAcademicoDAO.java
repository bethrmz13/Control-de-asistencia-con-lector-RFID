package asistenciarfid.dao;

import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.EstadoMateria;
import asistenciarfid.modelo.RegistroAcademico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class RegistroAcademicoDAO {

    // Convierte la fila actual del ResultSet en un RegistroAcademico
    private static RegistroAcademico armarRegistro(ResultSet rs) throws SQLException {
        RegistroAcademico r = new RegistroAcademico();
        r.setEstudianteId(rs.getInt("estudiante_id"));
        r.setAsignaturaId(rs.getInt("asignatura_id"));
        r.setEstado(rs.getString("estado"));
        r.setAsignaturaNombre(rs.getString("nombre"));
        r.setAsignaturaNivel(rs.getInt("nivel"));
        return r;
    }

    // Cierra la conexión sin que un error al cerrar tumbe el programa
    private static void cerrar(Connection cc) {
        try {
            if (cc != null) {
                cc.close();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    // Materias del estudiante (su récord académico), ordenadas por nivel
    public static List<RegistroAcademico> listarPorEstudiante(int estudianteId) {
        List<RegistroAcademico> lista = new ArrayList<>();
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT r.estudiante_id, r.asignatura_id, r.estado, a.nombre, a.nivel "
                    + "FROM registro_academico r "
                    + "JOIN asignatura a ON a.id = r.asignatura_id "
                    + "WHERE r.estudiante_id = ? ORDER BY a.nivel, a.nombre");
            ps.setInt(1, estudianteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarRegistro(rs));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            cerrar(cc);
        }
        return lista;
    }

  
    public static List<RegistroAcademico> generarParaCarrera(int carreraId, int nivelInicio) {
        List<RegistroAcademico> lista = new ArrayList<>();
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT id, nombre, nivel FROM asignatura "
                    + "WHERE carrera_id = ? ORDER BY nivel, nombre");
            ps.setInt(1, carreraId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RegistroAcademico r = new RegistroAcademico();
                int nivel = rs.getInt("nivel");
                r.setAsignaturaId(rs.getInt("id"));
                r.setAsignaturaNombre(rs.getString("nombre"));
                r.setAsignaturaNivel(nivel);
                if (nivel < nivelInicio) {
                    r.setEstado(EstadoMateria.APROBADA);
                } else {
                    r.setEstado(EstadoMateria.AUN_NO_CURSA);
                }
                lista.add(r);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            cerrar(cc);
        }
        return lista;
    }

    // null si no existe la fila
    public static String estadoDe(int estudianteId, int asignaturaId) {
        String estado = null;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return null;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT estado FROM registro_academico "
                    + "WHERE estudiante_id = ? AND asignatura_id = ?");
            ps.setInt(1, estudianteId);
            ps.setInt(2, asignaturaId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                estado = rs.getString("estado");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            cerrar(cc);
        }
        return estado;
    }

    public static boolean actualizarEstado(int estudianteId, int asignaturaId, String estado) {
        boolean ok = false;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "UPDATE registro_academico SET estado = ? "
                    + "WHERE estudiante_id = ? AND asignatura_id = ?");
            ps.setString(1, estado);
            ps.setInt(2, estudianteId);
            ps.setInt(3, asignaturaId);
            ps.executeUpdate();
            ok = true;   
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            cerrar(cc);
        }
        return ok;
    }
}