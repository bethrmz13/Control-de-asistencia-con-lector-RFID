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
import asistenciarfid.util.BaseDatos;
import asistenciarfid.util.Parametros;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CierrePeriodoControlador {

    
    public static List<Matricula> notasFaltantes() {
        return MatriculaDAO.listarSinNota();
    }

    
    public static List<Estudiante> cerrarPeriodo() {
        
        if (!BaseDatos.disponible()) {
            JOptionPane.showMessageDialog(null, "No se pudo cerrar el periodo: no hay conexión con la base de datos");
            return null;
        }

        
        List<Integer> estudiantesEstado = new ArrayList<>();
        List<Integer> asignaturasEstado = new ArrayList<>();
        List<String> estadosAntes = new ArrayList<>();
        List<Integer> estudiantesNivel = new ArrayList<>();
        List<Integer> nivelesAntes = new ArrayList<>();

        try {
            
            if (!notasFaltantes().isEmpty()) {
                return null;
            }
            Periodo periodo = PeriodoDAO.obtenerActivo();
            if (periodo == null) {
                JOptionPane.showMessageDialog(null, "No hay un periodo activo");
                return null;
            }

        
            List<Integer> estudiantes = new ArrayList<>();
            List<Integer> conReprobadas = new ArrayList<>();
            List<Grupo> grupos = GrupoDAO.listar(null, null);
            for (Grupo g : grupos) {
                List<Matricula> matriculas = MatriculaDAO.listarPorGrupo(g.getId());
                for (Matricula m : matriculas) {
                    String quien = m.getEstudianteNombre() + " en " + g.getAsignaturaNombre();
                    if (m.getNota() == null) {
                        return fallar("falta la nota de " + quien, estudiantesEstado, asignaturasEstado,
                                estadosAntes, estudiantesNivel, nivelesAntes);
                    }
                   
                    String antes = RegistroAcademicoDAO.estadoDe(m.getEstudianteId(), g.getAsignaturaId());
                    if (antes == null) {
                        return fallar(quien + " no está en el registro académico del estudiante",
                                estudiantesEstado, asignaturasEstado, estadosAntes, estudiantesNivel, nivelesAntes);
                    }
                    String estado;
                    if (m.getNota() >= Parametros.NOTA_APROBACION) {
                        estado = EstadoMateria.APROBADA;
                    } else {
                        estado = EstadoMateria.REPROBADA;
                        if (!conReprobadas.contains(m.getEstudianteId())) {
                            conReprobadas.add(m.getEstudianteId());
                        }
                    }
                    if (!RegistroAcademicoDAO.actualizarEstado(m.getEstudianteId(), g.getAsignaturaId(), estado)) {
                        return fallar("no se pudo guardar el estado de " + quien, estudiantesEstado,
                                asignaturasEstado, estadosAntes, estudiantesNivel, nivelesAntes);
                    }
                   
                    estudiantesEstado.add(m.getEstudianteId());
                    asignaturasEstado.add(g.getAsignaturaId());
                    estadosAntes.add(antes);
                    if (!estudiantes.contains(m.getEstudianteId())) {
                        estudiantes.add(m.getEstudianteId());
                    }
                }
            }

            List<Estudiante> porRevisar = new ArrayList<>();
            for (Integer estudianteId : estudiantes) {
                Estudiante est = EstudianteDAO.buscarPorId(estudianteId);
                if (est == null) {
                    return fallar("no se encontró al estudiante con id " + estudianteId, estudiantesEstado,
                            asignaturasEstado, estadosAntes, estudiantesNivel, nivelesAntes);
                }
                if (conReprobadas.contains(estudianteId)) {
                    porRevisar.add(est);
                } else {
                    int nivelMaximo = nivelMasAlto(est.getCarreraId());
                    if (est.getNivel() < nivelMaximo) {
                        int nivelAntes = est.getNivel();
                        est.setNivel(nivelAntes + 1);
                        if (!EstudianteDAO.actualizar(est)) {
                            return fallar("no se pudo subir de nivel a " + est.getNombre() + " " + est.getApellido(),
                                    estudiantesEstado, asignaturasEstado, estadosAntes, estudiantesNivel, nivelesAntes);
                        }
                        estudiantesNivel.add(estudianteId);
                        nivelesAntes.add(nivelAntes);
                    }
                }
            }

            periodo.setActivo(false);
            periodo.setMatriculaAbierta(false);
            if (!PeriodoDAO.actualizar(periodo)) {
                return fallar("no se pudo cerrar el periodo", estudiantesEstado, asignaturasEstado,
                        estadosAntes, estudiantesNivel, nivelesAntes);
            }
            return porRevisar;
        } catch (Exception ex) {
            return fallar("ocurrió un error (" + ex + ")", estudiantesEstado, asignaturasEstado,
                    estadosAntes, estudiantesNivel, nivelesAntes);
        }
    }

    private static List<Estudiante> fallar(String motivo, List<Integer> estudiantesEstado,
            List<Integer> asignaturasEstado, List<String> estadosAntes,
            List<Integer> estudiantesNivel, List<Integer> nivelesAntes) {
        int noDevueltos = 0;
        for (int i = nivelesAntes.size() - 1; i >= 0; i--) {
            try {
                Estudiante est = EstudianteDAO.buscarPorId(estudiantesNivel.get(i));
                est.setNivel(nivelesAntes.get(i));
                if (!EstudianteDAO.actualizar(est)) {
                    noDevueltos++;
                }
            } catch (Exception ex) {
                noDevueltos++;
            }
        }
        for (int i = estadosAntes.size() - 1; i >= 0; i--) {
            try {
                if (!RegistroAcademicoDAO.actualizarEstado(estudiantesEstado.get(i),
                        asignaturasEstado.get(i), estadosAntes.get(i))) {
                    noDevueltos++;
                }
            } catch (Exception ex) {
                noDevueltos++;
            }
        }
        String mensaje = "No se pudo cerrar el periodo: " + motivo + ".";
        if (noDevueltos == 0) {
            mensaje += "\nSe devolvieron los cambios y el periodo sigue activo. Intente de nuevo.";
        } else {
            mensaje += "\nNo se pudieron devolver " + noDevueltos + " cambios. Revise los niveles y los estados de "
                    + "las materias de los estudiantes antes de volver a intentar.";
        }
        JOptionPane.showMessageDialog(null, mensaje);
        return null;
    }

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
