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

    // Consulta base para obtener la información completa de un horario.
    private static final String SELECT_BASE
            = "SELECT h.id, h.grupo_id, h.dia_semana, h.hora_inicio, h.hora_fin, h.aula, "
            + "a.nombre AS asignatura, g.paralelo "
            + "FROM horario h "
            + "JOIN grupo g ON g.id = h.grupo_id "
            + "JOIN asignatura a ON a.id = g.asignatura_id ";

    // Convierte una fila del ResultSet en un objeto Horario.
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

    // ============================================================
    // LISTAR HORARIOS DE UN GRUPO
    // ============================================================

    public static List<Horario> listarPorGrupo(int grupoId) {
        List<Horario> lista = new ArrayList<>();

        Conexion cn = new Conexion();
        Connection cc = cn.conectar();

        if (cc == null) {
            return lista;
        }

        try {
            PreparedStatement ps = cc.prepareStatement(
                    SELECT_BASE
                    + "WHERE h.grupo_id = ? "
                    + "ORDER BY h.dia_semana, h.hora_inicio");

            ps.setInt(1, grupoId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(armarHorario(rs));
            }

            rs.close();
            ps.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return lista;
    }

    // ============================================================
    // HORARIOS DE UN ESTUDIANTE
    // ============================================================

    public static List<Horario> listarDeEstudiante(int estudianteId) {
        List<Horario> lista = new ArrayList<>();

        Conexion cn = new Conexion();
        Connection cc = cn.conectar();

        if (cc == null) {
            return lista;
        }

        try {
            PreparedStatement ps = cc.prepareStatement(
                    SELECT_BASE
                    + "JOIN matricula m ON m.grupo_id = g.id "
                    + "JOIN periodo p ON p.id = g.periodo_id "
                    + "WHERE m.estudiante_id = ? "
                    + "AND p.activo = 1 "
                    + "ORDER BY h.dia_semana, h.hora_inicio");

            ps.setInt(1, estudianteId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(armarHorario(rs));
            }

            rs.close();
            ps.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return lista;
    }

    // ============================================================
    // HORARIOS DE UN ESTUDIANTE EN UN DÍA
    // ============================================================

    public static List<Horario> listarDeEstudianteEnDia(
            int estudianteId,
            int diaSemana) {

        List<Horario> lista = new ArrayList<>();

        Conexion cn = new Conexion();
        Connection cc = cn.conectar();

        if (cc == null) {
            return lista;
        }

        try {
            PreparedStatement ps = cc.prepareStatement(
                    SELECT_BASE
                    + "JOIN matricula m ON m.grupo_id = g.id "
                    + "JOIN periodo p ON p.id = g.periodo_id "
                    + "WHERE m.estudiante_id = ? "
                    + "AND p.activo = 1 "
                    + "AND h.dia_semana = ? "
                    + "ORDER BY h.hora_inicio");

            ps.setInt(1, estudianteId);
            ps.setInt(2, diaSemana);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(armarHorario(rs));
            }

            rs.close();
            ps.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return lista;
    }

    // ============================================================
    // HORARIOS DE UN DOCENTE
    // ============================================================

    public static List<Horario> listarDeDocente(int docenteId) {
        List<Horario> lista = new ArrayList<>();

        Conexion cn = new Conexion();
        Connection cc = cn.conectar();

        if (cc == null) {
            return lista;
        }

        try {
            PreparedStatement ps = cc.prepareStatement(
                    SELECT_BASE
                    + "JOIN periodo p ON p.id = g.periodo_id "
                    + "WHERE g.docente_id = ? "
                    + "AND p.activo = 1 "
                    + "ORDER BY h.dia_semana, h.hora_inicio");

            ps.setInt(1, docenteId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(armarHorario(rs));
            }

            rs.close();
            ps.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return lista;
    }

    // ============================================================
    // BUSCAR CHOQUES DE HORARIO
    // ============================================================
    //
    // Comprueba tres posibles conflictos:
    //
    // 1. El mismo grupo ya tiene otro horario que se cruza.
    // 2. El docente ya tiene otra clase a esa hora.
    // 3. El aula ya está ocupada a esa hora.
    //
    // horarioIdExcluido:
    //   0 = horario nuevo
    //   >0 = horario que se está modificando
    //
    // Solo se consideran horarios pertenecientes al período activo.
    //

    public static String buscarChoque(
            int grupoId,
            Integer docenteId,
            String aula,
            int dia,
            java.sql.Time inicio,
            java.sql.Time fin,
            int horarioIdExcluido) {

        Conexion cn = new Conexion();
        Connection cc = cn.conectar();

        if (cc == null) {
            return null;
        }

        String choque = null;

        try {

            String sql
                    = SELECT_BASE
                    + "JOIN periodo p ON p.id = g.periodo_id "
                    + "WHERE p.activo = 1 "
                    + "AND h.dia_semana = ? "
                    + "AND h.id <> ? "
                    + "AND h.hora_inicio < ? "
                    + "AND ? < h.hora_fin "
                    + "AND ("
                    + "h.grupo_id = ? "
                    + "OR g.docente_id = ? "
                    + "OR (? IS NOT NULL AND h.aula = ?)"
                    + ") "
                    + "ORDER BY h.hora_inicio "
                    + "LIMIT 1";

            PreparedStatement ps = cc.prepareStatement(sql);

            ps.setInt(1, dia);
            ps.setInt(2, horarioIdExcluido);
            ps.setTime(3, fin);
            ps.setTime(4, inicio);
            ps.setInt(5, grupoId);

            if (docenteId == null) {
                ps.setNull(6, java.sql.Types.INTEGER);
            } else {
                ps.setInt(6, docenteId);
            }

            if (aula == null || aula.trim().isEmpty()) {
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setNull(8, java.sql.Types.VARCHAR);
            } else {
                ps.setString(7, aula);
                ps.setString(8, aula);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Horario otro = armarHorario(rs);

                String motivo;

                if (otro.getGrupoId() == grupoId) {

                    motivo = "este mismo grupo ya tiene otro horario";

                } else if (docenteId != null
                        && docenteDelGrupo(cc, otro.getGrupoId())
                        == docenteId.intValue()) {

                    motivo = "el docente ya tiene otra clase";

                } else {

                    motivo = "el aula " + otro.getAula()
                            + " ya está ocupada";
                }

                choque = "Choque: "
                        + motivo
                        + " en "
                        + otro.getAsignaturaNombre()
                        + " "
                        + otro.getParalelo()
                        + " ("
                        + otro.getNombreDia()
                        + " "
                        + otro.getHoraInicio().toString().substring(0, 5)
                        + " a "
                        + otro.getHoraFin().toString().substring(0, 5)
                        + ")";
            }

            rs.close();
            ps.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return choque;
    }

    // Obtiene el docente asignado a un grupo.
    private static int docenteDelGrupo(
            Connection cc,
            int grupoId) throws Exception {

        PreparedStatement ps = cc.prepareStatement(
                "SELECT docente_id FROM grupo WHERE id = ?");

        ps.setInt(1, grupoId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            int docente = rs.getInt(1);

            if (rs.wasNull()) {
                rs.close();
                ps.close();
                return -1;
            }

            rs.close();
            ps.close();

            return docente;
        }

        rs.close();
        ps.close();

        return -1;
    }

    // ============================================================
    // COMPROBAR ESTUDIANTES MATRICULADOS
    // ============================================================
    //
    // Se utiliza cuando se modifica el horario de un grupo que
    // ya tiene estudiantes matriculados.
    //
    // Evita que al cambiar el horario se cree un choque en el
    // horario de uno de esos estudiantes.
    //

    public static String buscarChoqueConMatriculados(Horario n) {

        String resultado = null;

        Conexion cn = new Conexion();
        Connection cc = cn.conectar();

        if (cc == null) {
            return null;
        }

        try {

            PreparedStatement ps = cc.prepareStatement(
                    "SELECT CONCAT(e.apellido, ' ', e.nombre) AS estudiante, "
                    + "a.nombre AS asignatura "
                    + "FROM matricula m1 "
                    + "JOIN estudiante e ON e.id = m1.estudiante_id "
                    + "JOIN matricula m2 "
                    + "ON m2.estudiante_id = m1.estudiante_id "
                    + "AND m2.grupo_id <> m1.grupo_id "
                    + "JOIN grupo g2 ON g2.id = m2.grupo_id "
                    + "JOIN periodo p ON p.id = g2.periodo_id "
                    + "AND p.activo = 1 "
                    + "JOIN asignatura a ON a.id = g2.asignatura_id "
                    + "JOIN horario h2 ON h2.grupo_id = g2.id "
                    + "WHERE m1.grupo_id = ? "
                    + "AND h2.dia_semana = ? "
                    + "AND h2.hora_inicio < ? "
                    + "AND ? < h2.hora_fin "
                    + "LIMIT 1");

            ps.setInt(1, n.getGrupoId());
            ps.setInt(2, n.getDiaSemana());
            ps.setTime(3, n.getHoraFin());
            ps.setTime(4, n.getHoraInicio());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                resultado = rs.getString("estudiante")
                        + " (choca con "
                        + rs.getString("asignatura")
                        + ")";
            }

            rs.close();
            ps.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return resultado;
    }

    // ============================================================
    // INSERTAR
    // ============================================================

    public static boolean insertar(Horario h) {

        boolean ok = false;

        Conexion cn = new Conexion();
        Connection cc = cn.conectar();

        if (cc == null) {
            return false;
        }

        try {

            PreparedStatement ps = cc.prepareStatement(
                    "INSERT INTO horario "
                    + "(grupo_id, dia_semana, hora_inicio, hora_fin, aula) "
                    + "VALUES (?, ?, ?, ?, ?)");

            ps.setInt(1, h.getGrupoId());
            ps.setInt(2, h.getDiaSemana());
            ps.setTime(3, h.getHoraInicio());
            ps.setTime(4, h.getHoraFin());
            ps.setString(5, h.getAula());

            ok = ps.executeUpdate() > 0;

            ps.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return ok;
    }

    // ============================================================
    // ACTUALIZAR
    // ============================================================

    public static boolean actualizar(Horario h) {

        boolean ok = false;

        Conexion cn = new Conexion();
        Connection cc = cn.conectar();

        if (cc == null) {
            return false;
        }

        try {

            PreparedStatement ps = cc.prepareStatement(
                    "UPDATE horario "
                    + "SET dia_semana = ?, "
                    + "hora_inicio = ?, "
                    + "hora_fin = ?, "
                    + "aula = ? "
                    + "WHERE id = ?");

            ps.setInt(1, h.getDiaSemana());
            ps.setTime(2, h.getHoraInicio());
            ps.setTime(3, h.getHoraFin());
            ps.setString(4, h.getAula());
            ps.setInt(5, h.getId());

            ok = ps.executeUpdate() > 0;

            ps.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return ok;
    }

    // ============================================================
    // ELIMINAR
    // ============================================================

    public static boolean eliminar(int id) {

        boolean ok = false;

        Conexion cn = new Conexion();
        Connection cc = cn.conectar();

        if (cc == null) {
            return false;
        }

        try {

            PreparedStatement ps = cc.prepareStatement(
                    "DELETE FROM horario WHERE id = ?");

            ps.setInt(1, id);

            ok = ps.executeUpdate() > 0;

            ps.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        return ok;
    }
}

