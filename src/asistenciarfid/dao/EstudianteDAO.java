package asistenciarfid.dao;

import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.EstadoMateria;
import asistenciarfid.modelo.Perfil;
import asistenciarfid.modelo.RegistroAcademico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class EstudianteDAO {

    private static final String SELECT_BASE
            = "SELECT id, usuario_id, codigo_rfid, nombre, apellido, correo, carrera_id, nivel "
            + "FROM estudiante ";

    // Convierte la fila actual del ResultSet en un Estudiante
    private static Estudiante armarEstudiante(ResultSet rs) throws SQLException {
        Estudiante e = new Estudiante();
        e.setId(rs.getInt("id"));
        int usuarioId = rs.getInt("usuario_id");
        if (rs.wasNull()) {
            e.setUsuarioId(null);
        } else {
            e.setUsuarioId(usuarioId);
        }
        e.setCodigoRfid(rs.getString("codigo_rfid"));
        e.setNombre(rs.getString("nombre"));
        e.setApellido(rs.getString("apellido"));
        e.setCorreo(rs.getString("correo"));
        e.setCarreraId(rs.getInt("carrera_id"));
        e.setNivel(rs.getInt("nivel"));
        return e;
    }

    // Cuenta filas con una consulta que tiene un solo "?"
    private static int contar(Connection cc, String sql, String valor) throws SQLException {
        PreparedStatement ps = cc.prepareStatement(sql);
        ps.setString(1, valor);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    private static void cerrar(Connection cc) {
        try {
            if (cc != null) {
                cc.close();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    // Busca un estudiante con una condición que tiene un solo "?". null si no existe
    private static Estudiante buscarUno(String condicion, Object valor) {
        Estudiante est = null;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return null;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(SELECT_BASE + "WHERE " + condicion);
            ps.setObject(1, valor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                est = armarEstudiante(rs);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            cerrar(cc);
        }
        return est;
    }

    // null si no existe
    public static Estudiante buscarPorCodigo(String codigoRfid) {
        return buscarUno("codigo_rfid = ?", codigoRfid);
    }

    public static Estudiante buscarPorUsuarioId(int usuarioId) {
        return buscarUno("usuario_id = ?", usuarioId);
    }

    public static Estudiante buscarPorId(int id) {
        return buscarUno("id = ?", id);
    }

    public static boolean existeCodigo(String codigoRfid) {
        boolean existe = false;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            existe = contar(cc, "SELECT COUNT(*) FROM estudiante WHERE codigo_rfid = ?", codigoRfid) > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            cerrar(cc);
        }
        return existe;
    }

    // carreraId null = todas las carreras
    public static List<Estudiante> listar(Integer carreraId) {
        List<Estudiante> lista = new ArrayList<>();
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps;
            if (carreraId == null) {
                ps = cc.prepareStatement(SELECT_BASE + "ORDER BY apellido, nombre");
            } else {
                ps = cc.prepareStatement(SELECT_BASE + "WHERE carrera_id = ? ORDER BY apellido, nombre");
                ps.setInt(1, carreraId);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarEstudiante(rs));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            cerrar(cc);
        }
        return lista;
    }

    // Guarda el usuario, el estudiante y las materias, con consultas simples una tras otra.
    // Si un paso falla, borra lo que ya se había guardado y devuelve false.
    public static boolean insertarConUsuario(Estudiante e, String username, String passwordPlano,List<RegistroAcademico> materias) {
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            // Antes de guardar nada: el usuario y el código RFID no deben existir ya
            if (contar(cc, "SELECT COUNT(*) FROM usuario WHERE username = ?", username) > 0
                    || contar(cc, "SELECT COUNT(*) FROM estudiante WHERE codigo_rfid = ?",e.getCodigoRfid()) > 0) {
                return false;
            }
            boolean ok = insertarUsuario(cc, e, username, passwordPlano) && insertarEstudiante(cc, e) && insertarMaterias(cc, e, materias);
            if (!ok) {
                borrarInsertados(cc, e.getCodigoRfid(), username);
            }
            return ok;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
            borrarInsertados(cc, e.getCodigoRfid(), username);
            return false;
        } finally {
            cerrar(cc);
        }
    }

    // Paso 1: el usuario. Luego se busca su id para usarlo en el estudiante
    private static boolean insertarUsuario(Connection cc, Estudiante e, String username,String passwordPlano) throws SQLException {
        PreparedStatement ps = cc.prepareStatement("INSERT INTO usuario (username, clave, nombre, perfil, activo) VALUES (?, ?, ?, ?, 1)");
        ps.setString(1, username);
        ps.setString(2, passwordPlano);
        ps.setString(3, e.getNombre() + " " + e.getApellido());
        ps.setString(4, Perfil.ESTUDIANTE);
        if (ps.executeUpdate() == 0) {
            return false;
        }
        ps = cc.prepareStatement("SELECT id FROM usuario WHERE username = ?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            return false;
        }
        e.setUsuarioId(rs.getInt("id"));
        return true;
    }

    // Paso 2: el estudiante. Luego se busca su id para usarlo en sus materias
    private static boolean insertarEstudiante(Connection cc, Estudiante e) throws SQLException {
        PreparedStatement ps = cc.prepareStatement("INSERT INTO estudiante (usuario_id, codigo_rfid, nombre, apellido, correo, carrera_id, nivel) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps.setInt(1, e.getUsuarioId());
        ps.setString(2, e.getCodigoRfid());
        ps.setString(3, e.getNombre());
        ps.setString(4, e.getApellido());
        ps.setString(5, e.getCorreo());
        ps.setInt(6, e.getCarreraId());
        ps.setInt(7, e.getNivel());
        if (ps.executeUpdate() == 0) {
            return false;
        }
        ps = cc.prepareStatement("SELECT id FROM estudiante WHERE codigo_rfid = ?");
        ps.setString(1, e.getCodigoRfid());
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            return false;
        }
        e.setId(rs.getInt("id"));
        return true;
    }

    // Paso 3: una fila de registro_academico por cada materia de la lista
    private static boolean insertarMaterias(Connection cc, Estudiante e,List<RegistroAcademico> materias) throws SQLException {
        PreparedStatement ps = cc.prepareStatement("INSERT INTO registro_academico (estudiante_id, asignatura_id, estado) VALUES (?, ?, ?)");
        for (RegistroAcademico r : materias) {
            ps.setInt(1, e.getId());
            ps.setInt(2, r.getAsignaturaId());
            ps.setString(3, r.getEstado());
            if (ps.executeUpdate() == 0) {
                return false;
            }
        }
        return true;
    }

    // Deshace el alta: borra el estudiante (sus materias se borran solas por ON DELETE CASCADE)
    // y luego el usuario. Se busca por código y usuario, que ya se verificó que eran nuevos.
    private static void borrarInsertados(Connection cc, String codigoRfid, String username) {
        try {
            PreparedStatement ps = cc.prepareStatement("DELETE FROM estudiante WHERE codigo_rfid = ?");
            ps.setString(1, codigoRfid);
            ps.executeUpdate();
            ps = cc.prepareStatement("DELETE FROM usuario WHERE username = ?");
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    // Modifica los datos del estudiante (y el nombre de su usuario). No cambia la clave ni las materias
    public static boolean actualizar(Estudiante e) {
        boolean ok = false;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "UPDATE estudiante SET codigo_rfid = ?, nombre = ?, apellido = ?, correo = ?, "
                    + "carrera_id = ?, nivel = ? WHERE id = ?");
            ps.setString(1, e.getCodigoRfid());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getApellido());
            ps.setString(4, e.getCorreo());
            ps.setInt(5, e.getCarreraId());
            ps.setInt(6, e.getNivel());
            ps.setInt(7, e.getId());
            ps.executeUpdate();   
            if (e.getUsuarioId() != null) {
                ps = cc.prepareStatement("UPDATE usuario SET nombre = ? WHERE id = ?");
                ps.setString(1, e.getNombre() + " " + e.getApellido());
                ps.setInt(2, e.getUsuarioId());
                ps.executeUpdate();
            }
            ok = true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            cerrar(cc);
        }
        return ok;
    }

    // true si el estudiante tiene al menos una matrícula (en cualquier periodo).
    // Ante un error de base de datos devuelve true, para no permitir el cambio a ciegas.
    public static boolean tieneMatriculas(int estudianteId) {
        boolean tiene = true;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return true;
        }
        try {
            PreparedStatement ps = cc.prepareStatement("SELECT COUNT(*) FROM matricula WHERE estudiante_id = ?");
            ps.setInt(1, estudianteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tiene = rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            cerrar(cc);
        }
        return tiene;
    }

    // Cambia la carrera de un estudiante que aún no tiene matrículas y rehace su récord
    // académico con las materias de la nueva carrera (las anteriores ya no le corresponden).
    // Todo ocurre en una transacción: si algo falla, no se cambia nada.
    public static boolean cambiarCarrera(int estudianteId, int carreraId) {
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            cc.setAutoCommit(false);
            // Se vuelve a comprobar aquí, por si lo matricularon mientras el formulario estaba abierto
            if (contarPorId(cc, "SELECT COUNT(*) FROM matricula WHERE estudiante_id = ?", estudianteId) > 0) {
                cc.rollback();
                return false;
            }
            PreparedStatement ps = cc.prepareStatement("SELECT nivel FROM estudiante WHERE id = ?");
            ps.setInt(1, estudianteId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                cc.rollback();
                return false;
            }
            int nivelEstudiante = rs.getInt("nivel");

            ps = cc.prepareStatement("UPDATE estudiante SET carrera_id = ? WHERE id = ?");
            ps.setInt(1, carreraId);
            ps.setInt(2, estudianteId);
            ps.executeUpdate();

            ps = cc.prepareStatement("DELETE FROM registro_academico WHERE estudiante_id = ?");
            ps.setInt(1, estudianteId);
            ps.executeUpdate();

            ps = cc.prepareStatement("SELECT id, nivel FROM asignatura WHERE carrera_id = ?");
            ps.setInt(1, carreraId);
            rs = ps.executeQuery();
            PreparedStatement insertar = cc.prepareStatement(
                    "INSERT INTO registro_academico (estudiante_id, asignatura_id, estado) VALUES (?, ?, ?)");
            while (rs.next()) {
                insertar.setInt(1, estudianteId);
                insertar.setInt(2, rs.getInt("id"));
                insertar.setString(3, rs.getInt("nivel") < nivelEstudiante
                        ? EstadoMateria.APROBADA : EstadoMateria.AUN_NO_CURSA);
                insertar.executeUpdate();
            }
            cc.commit();
            return true;
        } catch (SQLException ex) {
            try {
                cc.rollback();
            } catch (SQLException ex2) {
                // sin más que hacer: se muestra el error original
            }
            JOptionPane.showMessageDialog(null, ex);
            return false;
        } finally {
            try {
                cc.setAutoCommit(true);
            } catch (SQLException ex) {
                // la conexión se cierra enseguida
            }
            cerrar(cc);
        }
    }

    // Igual que contar, pero con un id entero
    private static int contarPorId(Connection cc, String sql, int valor) throws SQLException {
        PreparedStatement ps = cc.prepareStatement(sql);
        ps.setInt(1, valor);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    // También elimina su usuario
    public static boolean eliminar(int id) {
        Estudiante est = buscarPorId(id);
        if (est == null) {
            return false;
        }
        boolean ok = false;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            PreparedStatement ps = cc.prepareStatement("DELETE FROM estudiante WHERE id = ?");
            ps.setInt(1, id);
            ok = ps.executeUpdate() > 0;
            if (ok && est.getUsuarioId() != null) {
                ps = cc.prepareStatement("DELETE FROM usuario WHERE id = ?");
                ps.setInt(1, est.getUsuarioId());
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            cerrar(cc);
        }
        return ok;
    }
}