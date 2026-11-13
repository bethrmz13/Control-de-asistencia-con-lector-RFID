package asistenciarfid.dao;

import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.RegistroAcademico;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    // PENDIENTE (Evelyn): reemplazar el cuerpo de cada método con JDBC.
    // No cambiar los nombres ni los parámetros.

    // null si no existe
    public static Estudiante buscarPorCodigo(String codigoRfid) {
        return null;
    }

    // null si no existe
    public static Estudiante buscarPorUsuarioId(int usuarioId) {
        return null;
    }

    // null si no existe
    public static Estudiante buscarPorId(int id) {
        return null;
    }

    public static boolean existeCodigo(String codigoRfid) {
        return false;
    }

    // carreraId null = todas las carreras
    public static List<Estudiante> listar(Integer carreraId) {
        return new ArrayList<>();
    }

    // Guarda, en este orden y con consultas simples: el usuario (perfil ESTUDIANTE), el estudiante
    // y las filas de su tabla de materias. Si un paso falla, borra lo que ya había guardado y
    // devuelve false.
    public static boolean insertarConUsuario(Estudiante e, String username, String passwordPlano,
                                             List<RegistroAcademico> materias) {
        return false;
    }

    public static boolean actualizar(Estudiante e) {
        return false;
    }

    // También elimina su usuario
    public static boolean eliminar(int id) {
        return false;
    }
}
