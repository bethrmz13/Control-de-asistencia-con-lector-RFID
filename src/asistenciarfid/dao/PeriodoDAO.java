package asistenciarfid.dao;

import asistenciarfid.modelo.Periodo;
import java.util.ArrayList;
import java.util.List;

public class PeriodoDAO {

    // PENDIENTE (Brayan): reemplazar el cuerpo de cada método con JDBC.
    // No cambiar los nombres ni los parámetros.

    // null si no hay periodo activo
    public static Periodo obtenerActivo() {
        return null;
    }

    public static List<Periodo> listar() {
        return new ArrayList<>();
    }

    public static boolean insertar(Periodo p) {
        return false;
    }

    public static boolean actualizar(Periodo p) {
        return false;
    }

    // Deja solo este periodo con activo = 1 y los demás con 0
    public static boolean activar(int id) {
        return false;
    }

    public static boolean cambiarMatricula(int id, boolean abierta) {
        return false;
    }
}
