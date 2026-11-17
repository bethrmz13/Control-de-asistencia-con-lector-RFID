package asistenciarfid.util;

import asistenciarfid.db.Conexion;
import java.sql.Connection;

// Ayuda para los controladores: los DAO devuelven lista vacía o null cuando no hay conexión,
// y eso se confunde con "no hay datos". Antes de una operación importante se comprueba esto.
public class BaseDatos {

    // true si se pudo abrir y cerrar una conexión con MySQL
    // (si no, Conexion.conectar() ya muestra el aviso con el error)
    public static boolean disponible() {
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return false;
        }
        try {
            cc.close();
        } catch (Exception ex) {
            // no importa: la conexión funcionó
        }
        return true;
    }
}
