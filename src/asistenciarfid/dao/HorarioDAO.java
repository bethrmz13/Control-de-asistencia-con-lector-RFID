package asistenciarfid.dao;

import asistenciarfid.modelo.Horario;
import java.util.ArrayList;
import java.util.List;

public class HorarioDAO {

    // PENDIENTE (Damaris): reemplazar el cuerpo de cada método con JDBC.
    // No cambiar los nombres ni los parámetros.

    public static List<Horario> listarPorGrupo(int grupoId) {
        return new ArrayList<>();
    }

    // Horarios de los grupos donde el estudiante está matriculado, del periodo activo
    public static List<Horario> listarDeEstudiante(int estudianteId) {
        return new ArrayList<>();
    }

    // Lo mismo, pero solo de un día de la semana (1 = lunes ... 7 = domingo)
    public static List<Horario> listarDeEstudianteEnDia(int estudianteId, int diaSemana) {
        return new ArrayList<>();
    }

    public static boolean insertar(Horario h) {
        return false;
    }

    public static boolean actualizar(Horario h) {
        return false;
    }

    public static boolean eliminar(int id) {
        return false;
    }
}
