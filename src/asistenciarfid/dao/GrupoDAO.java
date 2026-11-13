package asistenciarfid.dao;

import asistenciarfid.modelo.Grupo;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    // PENDIENTE (Victoria): reemplazar el cuerpo de cada método con JDBC.
    // No cambiar los nombres ni los parámetros.

    // Grupos del periodo ACTIVO. Si carreraId o docenteId son null, no se filtra por ellos.
    public static List<Grupo> listar(Integer carreraId, Integer docenteId) {
        return new ArrayList<>();
    }

    // Del periodo activo
    public static List<Grupo> listarPorAsignatura(int asignaturaId) {
        return new ArrayList<>();
    }

    public static Grupo buscarPorId(int id) {
        return null;
    }

    public static boolean insertar(Grupo g) {
        return false;
    }

    public static boolean actualizar(Grupo g) {
        return false;
    }

    public static boolean eliminar(int id) {
        return false;
    }
}
