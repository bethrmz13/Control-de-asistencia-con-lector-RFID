package asistenciarfid.util;

import asistenciarfid.db.Conexion;
import java.sql.Connection;

public class BaseDatos {

    public static boolean disponible() {
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            cc.close();
        } catch (Exception ex) {
        }
        return true;
    }
}
