package asistenciarfid.controlador;

import asistenciarfid.dao.AsignaturaDAO;
import asistenciarfid.dao.EstudianteDAO;
import asistenciarfid.dao.GrupoDAO;
import asistenciarfid.dao.HorarioDAO;
import asistenciarfid.dao.MatriculaDAO;
import asistenciarfid.dao.PeriodoDAO;
import asistenciarfid.dao.RegistroAcademicoDAO;
import asistenciarfid.modelo.Asignatura;
import asistenciarfid.modelo.EstadoMateria;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.Grupo;
import asistenciarfid.modelo.Horario;
import asistenciarfid.modelo.Periodo;
import asistenciarfid.util.BaseDatos;
import asistenciarfid.util.Parametros;
import java.util.ArrayList;
import java.util.List;

public class MatriculaControlador {

    public static String matricular(int estudianteId, int grupoId) {
        if (!BaseDatos.disponible()) {
            return "No hay conexión con la base de datos";
        }

        Periodo periodo = PeriodoDAO.obtenerActivo();
        if (periodo == null) {
            return "No hay un periodo activo";
        }
        if (!periodo.isMatriculaAbierta()) {
            return "Las matrículas están cerradas";
        }

        Grupo grupo = GrupoDAO.buscarPorId(grupoId);
        if (grupo == null) {
            return "El grupo no existe";
        }
        if (grupo.getPeriodoId() != periodo.getId()) {
            return "El grupo no es del periodo activo";
        }

        Estudiante est = EstudianteDAO.buscarPorId(estudianteId);
        if (est == null) {
            return "El estudiante no existe";
        }
        Asignatura asig = AsignaturaDAO.buscarPorId(grupo.getAsignaturaId());
        if (asig == null) {
            return "La asignatura no existe";
        }
        String errorMateria = revisarMateria(est, asig);
        if (errorMateria != null) {
            return errorMateria;
        }

        if (MatriculaDAO.existeEnAsignatura(estudianteId, asig.getId())) {
            return "Ya está matriculado en " + asig.getNombre() + " en este periodo";
        }

        if (MatriculaDAO.contarEnPeriodoActivo(estudianteId) >= Parametros.MAX_MATERIAS) {
            return "Ya tiene el máximo de " + Parametros.MAX_MATERIAS + " materias";
        }

        List<Horario> nuevos = HorarioDAO.listarPorGrupo(grupoId);
        List<Horario> actuales = HorarioDAO.listarDeEstudiante(estudianteId);
        for (Horario n : nuevos) {
            for (Horario a : actuales) {

                if (n.getDiaSemana() == a.getDiaSemana()
                        && n.getHoraInicio().before(a.getHoraFin())
                        && a.getHoraInicio().before(n.getHoraFin())) {
                    return "Choca con " + a.getAsignaturaNombre() + " (" + a.getNombreDia() + " "
                            + a.getHoraInicio().toString().substring(0, 5) + " a "
                            + a.getHoraFin().toString().substring(0, 5) + ")";
                }
            }
        }

        if (!MatriculaDAO.matricular(estudianteId, grupoId)) {
            return "No se pudo guardar la matrícula";
        }
        return null;
    }

    private static String revisarMateria(Estudiante est, Asignatura asig) {
        if (asig.getCarreraId() != est.getCarreraId()) {
            return "La materia no es de su carrera";
        }
        if (asig.getNivel() > est.getNivel()) {
            return "La materia es de un nivel superior al suyo";
        }
        String estado = RegistroAcademicoDAO.estadoDe(est.getId(), asig.getId());
        if (EstadoMateria.APROBADA.equals(estado)) {
            return "La materia ya está aprobada";
        }
        if (!EstadoMateria.AUN_NO_CURSA.equals(estado) && !EstadoMateria.REPROBADA.equals(estado)) {
            return "La materia no está en su registro académico";
        }
        return null;
    }

    public static List<Grupo> gruposDisponibles(Estudiante estudiante) {
        List<Grupo> disponibles = new ArrayList<>();
        Periodo periodo = PeriodoDAO.obtenerActivo();
        if (periodo == null || !periodo.isMatriculaAbierta()) {
            return disponibles;
        }
        List<Grupo> grupos = GrupoDAO.listar(estudiante.getCarreraId(), null);
        for (Grupo g : grupos) {
            Asignatura asig = AsignaturaDAO.buscarPorId(g.getAsignaturaId());
            if (asig == null) {
                continue;
            }
            if (revisarMateria(estudiante, asig) != null) {
                continue;
            }
            if (MatriculaDAO.existeEnAsignatura(estudiante.getId(), asig.getId())) {
                continue;
            }
            disponibles.add(g);
        }
        return disponibles;
    }
}
