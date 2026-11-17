/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.controlador;

import asistenciarfid.dao.HorarioDAO;
import asistenciarfid.modelo.Horario;
import java.util.List;

public class HorarioControlador {

    // Devuelve null si el horario se puede guardar, o el mensaje del primer problema encontrado.
    // Se llama antes de insertar o actualizar un horario.
    public static String validarChoques(Horario nuevo) {
        // 1. Que no se cruce con otro horario del mismo grupo
        List<Horario> delGrupo = HorarioDAO.listarPorGrupo(nuevo.getGrupoId());
        for (Horario h : delGrupo) {
            if (h.getId() == nuevo.getId()) {
                continue;   // es el mismo horario que se está modificando
            }
            if (seCruzan(nuevo, h)) {
                return "Se cruza con otro horario de este grupo (" + h.getNombreDia() + " "
                        + h.getHoraInicio().toString().substring(0, 5) + " a "
                        + h.getHoraFin().toString().substring(0, 5) + ")";
            }
        }

        // 2. Que no deje con choque a un estudiante que ya está matriculado en el grupo
        String choque = HorarioDAO.buscarChoqueConMatriculados(nuevo);
        if (choque != null) {
            return "Este horario dejaría con choque a un estudiante matriculado:\n" + choque;
        }
        return null;
    }

    // Dos horarios se cruzan si son el mismo día y cada uno empieza antes de que termine el otro
    private static boolean seCruzan(Horario a, Horario b) {
        return a.getDiaSemana() == b.getDiaSemana()
                && a.getHoraInicio().before(b.getHoraFin())
                && b.getHoraInicio().before(a.getHoraFin());
    }
}