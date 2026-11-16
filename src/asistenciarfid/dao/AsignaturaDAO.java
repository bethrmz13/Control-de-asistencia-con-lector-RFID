package asistenciarfid.dao;

import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Asignatura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class AsignaturaDAO {

    // carreraId null = todas las carreras
    public static List<Asignatura> listar(Integer carreraId) {
        List<Asignatura> lista = new ArrayList<>();
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return lista;
        }
        try {
            String sql = "SELECT id, carrera_id, nombre, nivel FROM asignatura ";
            if (carreraId != null) {
                sql = sql + "WHERE carrera_id = ? ";
            }
            sql = sql + "ORDER BY nivel, nombre";
            PreparedStatement ps = cc.prepareStatement(sql);
            if (carreraId != null) {
                ps.setInt(1, carreraId);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Asignatura a = new Asignatura();
                a.setId(rs.getInt("id"));
                a.setCarreraId(rs.getInt("carrera_id"));
                a.setNombre(rs.getString("nombre"));
                a.setNivel(rs.getInt("nivel"));
                lista.add(a);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            try {
                cc.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        return lista;
    }

    public static Asignatura buscarPorId(int id) {
        Asignatura asignatura = null;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return null;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT id, carrera_id, nombre, nivel FROM asignatura WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                asignatura = new Asignatura();
                asignatura.setId(rs.getInt("id"));
                asignatura.setCarreraId(rs.getInt("carrera_id"));
                asignatura.setNombre(rs.getString("nombre"));
                asignatura.setNivel(rs.getInt("nivel"));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            try {
                cc.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        return asignatura;
    }

    public static boolean insertar(Asignatura a) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "INSERT INTO asignatura (carrera_id, nombre, nivel) VALUES (?, ?, ?)");
            ps.setInt(1, a.getCarreraId());
            ps.setString(2, a.getNombre());
            ps.setInt(3, a.getNivel());
            ok = ps.executeUpdate() > 0;
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            try {
                cc.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        return ok;
    }

    public static boolean actualizar(Asignatura a) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "UPDATE asignatura SET carrera_id = ?, nombre = ?, nivel = ? WHERE id = ?");
            ps.setInt(1, a.getCarreraId());
            ps.setString(2, a.getNombre());
            ps.setInt(3, a.getNivel());
            ps.setInt(4, a.getId());
            ok = ps.executeUpdate() > 0;
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            try {
                cc.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        return ok;
    }

    public static boolean eliminar(int id) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "DELETE FROM asignatura WHERE id = ?");
            ps.setInt(1, id);
            ok = ps.executeUpdate() > 0;
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            try {
                cc.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        return ok;
    }
}
