package asistenciarfid.dao;

import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Carrera;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CarreraDAO {

    public static List<Carrera> listar() {
        List<Carrera> lista = new ArrayList<>();
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return lista;
        }
        try {
            PreparedStatement ps = cc.prepareStatement("SELECT id, nombre FROM carrera ORDER BY nombre");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Carrera c = new Carrera();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                lista.add(c);
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

    public static Carrera buscarPorId(int id) {
        Carrera carrera = null;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return null;
        }
        try {
            PreparedStatement ps = cc.prepareStatement("SELECT id, nombre FROM carrera WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                carrera = new Carrera();
                carrera.setId(rs.getInt("id"));
                carrera.setNombre(rs.getString("nombre"));
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
        return carrera;
    }
}
