package asistenciarfid.dao;

import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Matricula;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class MatriculaDAO {

    // Consulta base: cada matrícula trae el estudiante ("Apellido Nombre"), la asignatura,
    // el paralelo y la nota (null si está vacía)
    private static final String SELECT_BASE
            = "SELECT m.estudiante_id, m.grupo_id, m.nota, "
            + "CONCAT(e.apellido, ' ', e.nombre) AS estudiante, "
            + "a.nombre AS asignatura, g.paralelo "
            + "FROM matricula m "
            + "JOIN estudiante e ON e.id = m.estudiante_id "
            + "JOIN grupo g ON g.id = m.grupo_id "
            + "JOIN asignatura a ON a.id = g.asignatura_id "
            + "JOIN periodo p ON p.id = g.periodo_id ";

    // Arma una Matricula con la fila actual del ResultSet
    private static Matricula armarMatricula(ResultSet rs) throws Exception {
        Matricula m = new Matricula();
        m.setEstudianteId(rs.getInt("estudiante_id"));
        m.setGrupoId(rs.getInt("grupo_id"));
        double nota = rs.getDouble("nota");
        if (rs.wasNull()) {
            m.setNota(null);
        } else {
            m.setNota(nota);
        }
        m.setEstudianteNombre(rs.getString("estudiante"));
        m.setAsignaturaNombre(rs.getString("asignatura"));
        m.setParalelo(rs.getString("paralelo"));
        return m;
    }

    public static boolean matricular(int estudianteId, int grupoId) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "INSERT INTO matricula (estudiante_id, grupo_id) VALUES (?, ?)");
            ps.setInt(1, estudianteId);
            ps.setInt(2, grupoId);
            ok = ps.executeUpdate() > 0;
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ok;
    }

    public static boolean anular(int estudianteId, int grupoId) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "DELETE FROM matricula WHERE estudiante_id = ? AND grupo_id = ?");
            ps.setInt(1, estudianteId);
            ps.setInt(2, grupoId);
            ok = ps.executeUpdate() > 0;
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ok;
    }

    // Del periodo activo
    public static List<Matricula> listarPorEstudiante(int estudianteId) {
        List<Matricula> lista = new ArrayList<>();
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(SELECT_BASE
                    + "WHERE m.estudiante_id = ? AND p.activo = 1 ORDER BY a.nombre");
            ps.setInt(1, estudianteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarMatricula(rs));
            }
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }

    // Con el nombre del estudiante y su nota
    public static List<Matricula> listarPorGrupo(int grupoId) {
        List<Matricula> lista = new ArrayList<>();
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(SELECT_BASE
                    + "WHERE m.grupo_id = ? ORDER BY e.apellido, e.nombre");
            ps.setInt(1, grupoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarMatricula(rs));
            }
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }

    public static int contarEnPeriodoActivo(int estudianteId) {
        int total = 0;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return 0;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT COUNT(*) FROM matricula m "
                    + "JOIN grupo g ON g.id = m.grupo_id "
                    + "JOIN periodo p ON p.id = g.periodo_id "
                    + "WHERE m.estudiante_id = ? AND p.activo = 1");
            ps.setInt(1, estudianteId);
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

    // true si ya está matriculado en algún paralelo de esa asignatura (periodo activo)
    public static boolean existeEnAsignatura(int estudianteId, int asignaturaId) {
        boolean existe = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "SELECT COUNT(*) FROM matricula m "
                    + "JOIN grupo g ON g.id = m.grupo_id "
                    + "JOIN periodo p ON p.id = g.periodo_id "
                    + "WHERE m.estudiante_id = ? AND g.asignatura_id = ? AND p.activo = 1");
            ps.setInt(1, estudianteId);
            ps.setInt(2, asignaturaId);
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

    // nota null = borrar la nota
    public static boolean actualizarNota(int estudianteId, int grupoId, Double nota) {
        boolean ok = false;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "UPDATE matricula SET nota = ? WHERE estudiante_id = ? AND grupo_id = ?");
            if (nota == null) {
                ps.setNull(1, Types.DECIMAL);
            } else {
                ps.setDouble(1, nota);
            }
            ps.setInt(2, estudianteId);
            ps.setInt(3, grupoId);
            ps.executeUpdate();
            ok = true;   // si la nota no cambia, MySQL cuenta 0 filas: no es un error
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ok;
    }

    // Matrículas del periodo activo que todavía no tienen nota
    public static List<Matricula> listarSinNota() {
        List<Matricula> lista = new ArrayList<>();
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(SELECT_BASE
                    + "WHERE p.activo = 1 AND m.nota IS NULL ORDER BY a.nombre, e.apellido");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarMatricula(rs));
            }
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }
}
