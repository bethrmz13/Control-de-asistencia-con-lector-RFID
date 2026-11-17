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


    public static String validarChoques(Horario nuevo) {

        if (nuevo == null) {
            return "El horario no puede ser nulo";
        }

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

        Grupo grupo = GrupoDAO.buscarPorId(nuevo.getGrupoId());

        if (grupo == null) {
            return "El grupo no existe";
        }

        Integer docenteId = grupo.getDocenteId();


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


        String choqueEstudiante
                = HorarioDAO.buscarChoqueConMatriculados(nuevo);

        if (choqueEstudiante != null) {

            return "Este horario dejaría con choque a un estudiante "
                    + "matriculado:\n"
                    + choqueEstudiante;
        }

        return null;
    }
}
