package asistenciarfid.dao;

import asistenciarfid.modelo.Asistencia;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {

    // PENDIENTE (Damaris): reemplazar el cuerpo de cada método con JDBC.
    // No cambiar los nombres ni los parámetros.

    public static boolean existe(int estudianteId, int horarioId, Date fecha) {
        return false;
    }

    public static boolean registrar(int estudianteId, int horarioId, Date fecha, Time hora) {
        return false;
    }

    public static List<Asistencia> listarPorHorarioYFecha(int horarioId, Date fecha) {
        return new ArrayList<>();
    }

    // Asistencias del estudiante en los horarios de ese grupo, entre dos fechas
    public static int contarPresentes(int estudianteId, int grupoId, Date desde, Date hasta) {
        return 0;
    }
}
