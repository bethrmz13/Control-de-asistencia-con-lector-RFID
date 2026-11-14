package asistenciarfid.controlador;

import asistenciarfid.dao.AsignaturaDAO;
import asistenciarfid.dao.EstudianteDAO;
import asistenciarfid.dao.GrupoDAO;
import asistenciarfid.dao.MatriculaDAO;
import asistenciarfid.dao.PeriodoDAO;
import asistenciarfid.dao.RegistroAcademicoDAO;
import asistenciarfid.modelo.Asignatura;
import asistenciarfid.modelo.EstadoMateria;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.Grupo;
import asistenciarfid.modelo.Matricula;
import asistenciarfid.modelo.Periodo;
import asistenciarfid.util.Parametros;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CierrePeriodoControlador {

    // Matrículas del periodo activo que todavía no tienen nota
    public static List<Matricula> notasFaltantes() {
        return MatriculaDAO.listarSinNota();
    }

    // Cierra el periodo activo. Devuelve los estudiantes "por revisar" (los que
    // reprobaron algo), o null si no se pudo cerrar (faltan notas o hubo un error).
    public static List<Estudiante> cerrarPeriodo() {
        try {
            // Primero: si falta alguna nota no se cambia nada
            if (!notasFaltantes().isEmpty()) {
                return null;
            }
            Periodo periodo = PeriodoDAO.obtenerActivo();
            if (periodo == null) {
                JOptionPane.showMessageDialog(null, "No hay un periodo activo");
                return null;
            }

            // Segundo: cada matrícula pasa su materia a APROBADA o REPROBADA.
            // Se anotan los estudiantes y cuáles reprobaron algo (dos listas de ids).
            List<Integer> estudiantes = new ArrayList<>();
            List<Integer> conReprobadas = new ArrayList<>();
            List<Grupo> grupos = GrupoDAO.listar(null, null);
            for (Grupo g : grupos) {
                List<Matricula> matriculas = MatriculaDAO.listarPorGrupo(g.getId());
                for (Matricula m : matriculas) {
                    String estado;
                    if (m.getNota() >= Parametros.NOTA_APROBACION) {
                        estado = EstadoMateria.APROBADA;
                    } else {
                        estado = EstadoMateria.REPROBADA;
                        if (!conReprobadas.contains(m.getEstudianteId())) {
                            conReprobadas.add(m.getEstudianteId());
                        }
                    }
                    RegistroAcademicoDAO.actualizarEstado(m.getEstudianteId(), g.getAsignaturaId(), estado);
                    if (!estudiantes.contains(m.getEstudianteId())) {
                        estudiantes.add(m.getEstudianteId());
                    }
                }
            }

            // Tercero: sube de nivel quien aprobó todo; los demás quedan "por revisar"
            List<Estudiante> porRevisar = new ArrayList<>();
            for (Integer estudianteId : estudiantes) {
                Estudiante est = EstudianteDAO.buscarPorId(estudianteId);
                if (est == null) {
                    continue;
                }
                if (conReprobadas.contains(estudianteId)) {
                    porRevisar.add(est);
                } else {
                    int nivelMaximo = nivelMasAlto(est.getCarreraId());
                    if (est.getNivel() < nivelMaximo) {
                        est.setNivel(est.getNivel() + 1);
                        EstudianteDAO.actualizar(est);
                    }
                }
            }

            // Al final: el periodo queda inactivo y con la matrícula cerrada
            periodo.setActivo(false);
            periodo.setMatriculaAbierta(false);
            PeriodoDAO.actualizar(periodo);
            return porRevisar;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cerrar el periodo: " + ex
                    + "\nRevise los niveles de los estudiantes en la ventana de estudiantes.");
            return null;
        }
    }

    // Nivel más alto de las asignaturas de una carrera
    private static int nivelMasAlto(int carreraId) {
        int maximo = 0;
        List<Asignatura> asignaturas = AsignaturaDAO.listar(carreraId);
        for (Asignatura a : asignaturas) {
            if (a.getNivel() > maximo) {
                maximo = a.getNivel();
            }
        }
        return maximo;
    }
}
