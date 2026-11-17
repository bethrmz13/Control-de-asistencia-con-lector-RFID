package asistenciarfid.dao;

import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Asistencia;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class AsistenciaDAO {

    public static boolean existe(int estudianteId, int horarioId, Date fecha) {
        boolean existe = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT COUNT(*) FROM asistencia "
                    + "WHERE estudiante_id = ? AND horario_id = ? AND fecha = ?");
            ps.setInt(1, estudianteId);
            ps.setInt(2, horarioId);
            ps.setDate(3, fecha);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return existe;
    }

    public static boolean registrar(int estudianteId, int horarioId, Date fecha, Time hora) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "INSERT INTO asistencia (estudiante_id, horario_id, fecha, hora) "
                    + "VALUES (?, ?, ?, ?)");
            ps.setInt(1, estudianteId);
            ps.setInt(2, horarioId);
            ps.setDate(3, fecha);
            ps.setTime(4, hora);
            ok = ps.executeUpdate() > 0;
            cc.close();
        } catch (java.sql.SQLIntegrityConstraintViolationException ex) {

            ok = false;
            if (ex.getErrorCode() != 1062) {
                JOptionPane.showMessageDialog(null, ex);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ok;
    }

    public static List<Asistencia> listarPorHorarioYFecha(int horarioId, Date fecha) {
        List<Asistencia> lista = new ArrayList<>();
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT a.id, a.estudiante_id, a.horario_id, a.fecha, a.hora, "
                    + "CONCAT(e.apellido, ' ', e.nombre) AS estudiante "
                    + "FROM asistencia a JOIN estudiante e ON e.id = a.estudiante_id "
                    + "WHERE a.horario_id = ? AND a.fecha = ? "
                    + "ORDER BY a.hora");
            ps.setInt(1, horarioId);
            ps.setDate(2, fecha);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setId(rs.getInt("id"));
                a.setEstudianteId(rs.getInt("estudiante_id"));
                a.setHorarioId(rs.getInt("horario_id"));
                a.setFecha(rs.getDate("fecha"));
                a.setHora(rs.getTime("hora"));
                a.setEstudianteNombre(rs.getString("estudiante"));
                lista.add(a);
            }
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }


    public static int contarPresentes(int estudianteId, int grupoId, Date desde, Date hasta) {
        int total = 0;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return 0;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT COUNT(*) FROM asistencia a "
                    + "JOIN horario h ON h.id = a.horario_id "
                    + "WHERE a.estudiante_id = ? AND h.grupo_id = ? "
                    + "AND a.fecha BETWEEN ? AND ?");
            ps.setInt(1, estudianteId);
            ps.setInt(2, grupoId);
            ps.setDate(3, desde);
            ps.setDate(4, hasta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return total;
    }
}
