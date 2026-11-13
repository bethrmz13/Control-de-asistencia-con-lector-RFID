package asistenciarfid.dao;

import asistenciarfid.modelo.Asignatura;
import java.util.ArrayList;
import java.util.List;

public class AsignaturaDAO {

    // PENDIENTE (Brayan): reemplazar el cuerpo de cada método con JDBC.
    // No cambiar los nombres ni los parámetros.

    // carreraId null = todas las carreras
    public static List<Asignatura> listar(Integer carreraId) {
        return new ArrayList<>();
    }

    public static Asignatura buscarPorId(int id) {
        return null;
    }

    public static boolean insertar(Asignatura a) {
        return false;
    }

    public static boolean actualizar(Asignatura a) {
        return false;
    }

    public static boolean eliminar(int id) {
        return false;
    }
}
