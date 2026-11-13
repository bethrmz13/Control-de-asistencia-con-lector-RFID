package asistenciarfid.dao;

import asistenciarfid.modelo.Matricula;
import java.util.ArrayList;
import java.util.List;

public class MatriculaDAO {

    // PENDIENTE (Damaris): reemplazar el cuerpo de cada método con JDBC.
    // No cambiar los nombres ni los parámetros.

    public static boolean matricular(int estudianteId, int grupoId) {
        return false;
    }

    public static boolean anular(int estudianteId, int grupoId) {
        return false;
    }

    // Del periodo activo
    public static List<Matricula> listarPorEstudiante(int estudianteId) {
        return new ArrayList<>();
    }

    // Con el nombre del estudiante y su nota
    public static List<Matricula> listarPorGrupo(int grupoId) {
        return new ArrayList<>();
    }

    public static int contarEnPeriodoActivo(int estudianteId) {
        return 0;
    }

    // true si ya está matriculado en algún paralelo de esa asignatura (periodo activo)
    public static boolean existeEnAsignatura(int estudianteId, int asignaturaId) {
        return false;
    }

    // nota null = borrar la nota
    public static boolean actualizarNota(int estudianteId, int grupoId, Double nota) {
        return false;
    }

    // Matrículas del periodo activo que todavía no tienen nota
    public static List<Matricula> listarSinNota() {
        return new ArrayList<>();
    }
}
