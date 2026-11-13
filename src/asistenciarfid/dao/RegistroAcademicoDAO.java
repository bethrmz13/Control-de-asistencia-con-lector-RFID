package asistenciarfid.dao;

import asistenciarfid.modelo.RegistroAcademico;
import java.util.ArrayList;
import java.util.List;

public class RegistroAcademicoDAO {

    // PENDIENTE (Evelyn): reemplazar el cuerpo de cada método con JDBC.
    // No cambiar los nombres ni los parámetros.

    public static List<RegistroAcademico> listarPorEstudiante(int estudianteId) {
        return new ArrayList<>();
    }

    // NO guarda nada: arma la lista de materias de la carrera. Nivel menor al de inicio
    // = APROBADA, las demás = AUN_NO_CURSA. Sirve para mostrarla en FrmEstudiantes.
    public static List<RegistroAcademico> generarParaCarrera(int carreraId, int nivelInicio) {
        return new ArrayList<>();
    }

    // null si no existe
    public static String estadoDe(int estudianteId, int asignaturaId) {
        return null;
    }

    public static boolean actualizarEstado(int estudianteId, int asignaturaId, String estado) {
        return false;
    }
}
