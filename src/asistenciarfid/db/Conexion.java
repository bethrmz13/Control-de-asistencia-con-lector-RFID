package asistenciarfid.db;

import java.sql.*;
import javax.swing.JOptionPane;

public class Conexion {

    public Connection conectar() {
        Connection conect = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conect = DriverManager.getConnection(
                    "jdbc:mysql://localhost/asistencia_rfid", "root", "");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return conect;
    }
}
