/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package asistenciarfid.controlador;

import asistenciarfid.dao.GrupoDAO;
import asistenciarfid.dao.HorarioDAO;
import asistenciarfid.modelo.Grupo;
import asistenciarfid.modelo.Horario;

public class HorarioControlador {

    /**
     * Valida si un horario puede ser creado o modificado.
     *
     * Devuelve:
     *   null = el horario es válido.
     *   texto = existe algún conflicto.
     */
    public static String validarChoques(Horario nuevo) {

        if (nuevo == null) {
            return "El horario no puede ser nulo";
        }

        // --------------------------------------------------------
        // 1. Validar datos básicos
        // --------------------------------------------------------

        if (nuevo.getHoraInicio() == null
                || nuevo.getHoraFin() == null) {

            return "Debe indicar la hora de inicio y la hora de fin";
        }

        if (!nuevo.getHoraInicio().before(nuevo.getHoraFin())) {

            return "La hora de inicio debe ser anterior a la hora de fin";
        }

        if (nuevo.getDiaSemana() < 1
                || nuevo.getDiaSemana() > 7) {

            return "El día de la semana no es válido";
        }

        // --------------------------------------------------------
        // 2. Buscar el grupo
        // --------------------------------------------------------

        Grupo grupo = GrupoDAO.buscarPorId(nuevo.getGrupoId());

        if (grupo == null) {
            return "El grupo no existe";
        }

        // --------------------------------------------------------
        // 3. Obtener el docente del grupo
        // --------------------------------------------------------

        Integer docenteId = grupo.getDocenteId();

        // --------------------------------------------------------
        // 4. Validar choque de:
        //
        //    - mismo grupo
        //    - mismo docente
        //    - misma aula
        //
        // El DAO ignora el propio horario cuando se está
        // modificando.
        // --------------------------------------------------------

        String choque = HorarioDAO.buscarChoque(
                nuevo.getGrupoId(),
                docenteId,
                nuevo.getAula(),
                nuevo.getDiaSemana(),
                nuevo.getHoraInicio(),
                nuevo.getHoraFin(),
                nuevo.getId()
        );

        if (choque != null) {
            return choque;
        }

        // --------------------------------------------------------
        // 5. Si el grupo ya tiene estudiantes matriculados,
        //    comprobar que este cambio no les cree un choque
        //    con otra materia.
        // --------------------------------------------------------

        String choqueEstudiante
                = HorarioDAO.buscarChoqueConMatriculados(nuevo);

        if (choqueEstudiante != null) {

            return "Este horario dejaría con choque a un estudiante "
                    + "matriculado:\n"
                    + choqueEstudiante;
        }

        // --------------------------------------------------------
        // 6. Todo correcto
        // --------------------------------------------------------

        return null;
    }
}
