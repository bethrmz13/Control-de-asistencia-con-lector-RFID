package asistenciarfid.dao;

import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Periodo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class PeriodoDAO {

    // null si no hay periodo activo
    public static Periodo obtenerActivo() {
        Periodo periodo = null;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return null;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT id, nombre, fecha_inicio, fecha_fin, activo, matricula_abierta FROM periodo WHERE activo = 1");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                periodo = new Periodo();
                periodo.setId(rs.getInt("id"));
                periodo.setNombre(rs.getString("nombre"));
                periodo.setFechaInicio(rs.getDate("fecha_inicio"));
                periodo.setFechaFin(rs.getDate("fecha_fin"));
                periodo.setActivo(rs.getInt("activo") == 1);
                periodo.setMatriculaAbierta(rs.getInt("matricula_abierta") == 1);
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
        return periodo;
    }

    public static List<Periodo> listar() {
        List<Periodo> lista = new ArrayList<>();
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT id, nombre, fecha_inicio, fecha_fin, activo, matricula_abierta FROM periodo ORDER BY fecha_inicio DESC, id DESC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Periodo p = new Periodo();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setFechaInicio(rs.getDate("fecha_inicio"));
                p.setFechaFin(rs.getDate("fecha_fin"));
                p.setActivo(rs.getInt("activo") == 1);
                p.setMatriculaAbierta(rs.getInt("matricula_abierta") == 1);
                lista.add(p);
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

    public static boolean insertar(Periodo p) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "INSERT INTO periodo (nombre, fecha_inicio, fecha_fin, activo, matricula_abierta) "
                    + "VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, p.getNombre());
            ps.setDate(2, p.getFechaInicio());
            ps.setDate(3, p.getFechaFin());
            ps.setInt(4, p.isActivo() ? 1 : 0);
            ps.setInt(5, p.isMatriculaAbierta() ? 1 : 0);
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

    public static boolean actualizar(Periodo p) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "UPDATE periodo SET nombre = ?, fecha_inicio = ?, fecha_fin = ?, "
                    + "activo = ?, matricula_abierta = ? WHERE id = ?");
            ps.setString(1, p.getNombre());
            ps.setDate(2, p.getFechaInicio());
            ps.setDate(3, p.getFechaFin());
            ps.setInt(4, p.isActivo() ? 1 : 0);
            ps.setInt(5, p.isMatriculaAbierta() ? 1 : 0);
            ps.setInt(6, p.getId());
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

    // Deja solo este periodo con activo = 1 y los demás con 0
    public static boolean activar(int id) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement("SELECT id FROM periodo WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            boolean existe = rs.next();
            rs.close();
            ps.close();
            if (!existe) {
                return false;
            }

            // Se comprueba la existencia antes de desactivar los periodos.
            ps = cc.prepareStatement("UPDATE periodo SET activo = 0");
            ps.executeUpdate();
            ps.close();

            ps = cc.prepareStatement("UPDATE periodo SET activo = 1 WHERE id = ?");
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

    public static boolean cambiarMatricula(int id, boolean abierta) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "UPDATE periodo SET matricula_abierta = ? WHERE id = ?");
            ps.setInt(1, abierta ? 1 : 0);
            ps.setInt(2, id);
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
