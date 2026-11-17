package asistenciarfid.dao;

import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UsuarioDAO {

    
    private static Usuario armarUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setNombre(rs.getString("nombre"));
        u.setPerfil(rs.getString("perfil"));
        int carrera = rs.getInt("carrera_id");
        if (rs.wasNull()) {
            u.setCarreraId(null);
        } else {
            u.setCarreraId(carrera);
        }
        u.setActivo(rs.getInt("activo") == 1);
        return u;
    }

    
    public static Usuario validar(String username, String passwordPlano) {
        Usuario u = null;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return null;
        }
        try {
            String sql = "SELECT * FROM usuario WHERE username=? AND clave=? AND activo=1";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, passwordPlano);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u = armarUsuario(rs);
            }
            cc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return u;
    }

    public static List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return lista;
        }
        try {
            
            String sql = "SELECT u.id, u.username, u.nombre, u.perfil, u.activo, "
                    + "CASE WHEN u.perfil = 'ESTUDIANTE' THEN e.carrera_id ELSE u.carrera_id END AS carrera_id "
                    + "FROM usuario u LEFT JOIN estudiante e ON e.usuario_id = u.id "
                    + "ORDER BY u.nombre";
            PreparedStatement ps = cc.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarUsuario(rs));
            }
            cc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }

    
    public static List<Usuario> listarPorPerfil(String perfil) {
        List<Usuario> lista = new ArrayList<>();
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return lista;
        }
        try {
            String sql = "SELECT * FROM usuario WHERE perfil=? AND activo=1 ORDER BY nombre";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setString(1, perfil);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(armarUsuario(rs));
            }
            cc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return lista;
    }

    public static boolean insertar(Usuario u, String passwordPlano) {
        boolean ok = false;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            String sql = "INSERT INTO usuario (username, clave, nombre, perfil, carrera_id, activo) "
                    + "VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, passwordPlano);
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getPerfil());
            if (u.getCarreraId() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, u.getCarreraId());
            }
            ps.setInt(6, u.isActivo() ? 1 : 0);
            ok = ps.executeUpdate() > 0;
            cc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ok;
    }

    
    public static boolean actualizar(Usuario u, String passwordPlano) {
        boolean ok = false;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return false;
        }
        try {
            boolean cambiaClave = passwordPlano != null && !passwordPlano.isEmpty();
            String sql;
            if (cambiaClave) {
                sql = "UPDATE usuario SET username=?, nombre=?, perfil=?, carrera_id=?, activo=?, clave=? WHERE id=?";
            } else {
                sql = "UPDATE usuario SET username=?, nombre=?, perfil=?, carrera_id=?, activo=? WHERE id=?";
            }
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getPerfil());
            if (u.getCarreraId() == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, u.getCarreraId());
            }
            ps.setInt(5, u.isActivo() ? 1 : 0);
            if (cambiaClave) {
                ps.setString(6, passwordPlano);
                ps.setInt(7, u.getId());
            } else {
                ps.setInt(6, u.getId());
            }
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
            String sql = "DELETE FROM usuario WHERE id=?";
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
