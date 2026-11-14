package asistenciarfid.dao;

import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Horario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class HorarioDAO {

    // Consulta base: cada horario trae el nombre de la asignatura y el paralelo (con JOIN)
    private static final String SELECT_BASE
            = "SELECT h.id, h.grupo_id, h.dia_semana, h.hora_inicio, h.hora_fin, h.aula, "
            + "a.nombre AS asignatura, g.paralelo "
            + "FROM horario h "
            + "JOIN grupo g ON g.id = h.grupo_id "
            + "JOIN asignatura a ON a.id = g.asignatura_id ";

    // Arma un Horario con la fila actual del ResultSet
    private static Horario armarHorario(ResultSet rs) throws Exception {
        Horario h = new Horario();
        h.setId(rs.getInt("id"));
        h.setGrupoId(rs.getInt("grupo_id"));
        h.setDiaSemana(rs.getInt("dia_semana"));
        h.setHoraInicio(rs.getTime("hora_inicio"));
        h.setHoraFin(rs.getTime("hora_fin"));
        h.setAula(rs.getString("aula"));
        h.setAsignaturaNombre(rs.getString("asignatura"));
        h.setParalelo(rs.getString("paralelo"));
        return h;
    }

    public static List<Horario> listarPorGrupo(int grupoId) {
        List<Horario> lista = new ArrayList<>();
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(SELECT_BASE
                    + "WHERE h.grupo_id = ? ORDER BY h.dia_semana, h.hora_inicio");
            ps.setInt(1, grupoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarHorario(rs));
            }
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }

    // Horarios de los grupos donde el estudiante está matriculado, del periodo activo
    public static List<Horario> listarDeEstudiante(int estudianteId) {
        List<Horario> lista = new ArrayList<>();
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(SELECT_BASE
                    + "JOIN matricula m ON m.grupo_id = g.id "
                    + "JOIN periodo p ON p.id = g.periodo_id "
                    + "WHERE m.estudiante_id = ? AND p.activo = 1 "
                    + "ORDER BY h.dia_semana, h.hora_inicio");
            ps.setInt(1, estudianteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarHorario(rs));
            }
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }

    // Lo mismo, pero solo de un día de la semana (1 = lunes ... 7 = domingo)
    public static List<Horario> listarDeEstudianteEnDia(int estudianteId, int diaSemana) {
        List<Horario> lista = new ArrayList<>();
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(SELECT_BASE
                    + "JOIN matricula m ON m.grupo_id = g.id "
                    + "JOIN periodo p ON p.id = g.periodo_id "
                    + "WHERE m.estudiante_id = ? AND p.activo = 1 AND h.dia_semana = ? "
                    + "ORDER BY h.hora_inicio");
            ps.setInt(1, estudianteId);
            ps.setInt(2, diaSemana);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarHorario(rs));
            }
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }

    public static boolean insertar(Horario h) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "INSERT INTO horario (grupo_id, dia_semana, hora_inicio, hora_fin, aula) "
                    + "VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, h.getGrupoId());
            ps.setInt(2, h.getDiaSemana());
            ps.setTime(3, h.getHoraInicio());
            ps.setTime(4, h.getHoraFin());
            ps.setString(5, h.getAula());
            ok = ps.executeUpdate() > 0;
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ok;
    }

    public static boolean actualizar(Horario h) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "UPDATE horario SET dia_semana = ?, hora_inicio = ?, hora_fin = ?, aula = ? "
                    + "WHERE id = ?");
            ps.setInt(1, h.getDiaSemana());
            ps.setTime(2, h.getHoraInicio());
            ps.setTime(3, h.getHoraFin());
            ps.setString(4, h.getAula());
            ps.setInt(5, h.getId());
            ok = ps.executeUpdate() > 0;
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
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
            PreparedStatement ps = cc.prepareStatement("DELETE FROM horario WHERE id = ?");
            ps.setInt(1, id);
            ok = ps.executeUpdate() > 0;
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ok;
    }
}
