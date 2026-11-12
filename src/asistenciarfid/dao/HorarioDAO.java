/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

/**
 *
 * @author USER
 */

import asistenciarfid.modelo.Horario;
import java.util.List;

public interface HorarioDAO {

    List<Horario> listarPorGrupo(int grupoId);

    // Horarios de los grupos donde el estudiante esta matriculado, del periodo activo
    List<Horario> listarDeEstudiante(int estudianteId);

    // Lo mismo, pero solo de un dia de la semana (1 = lunes ... 7 = domingo)
    List<Horario> listarDeEstudianteEnDia(int estudianteId, int diaSemana);

    boolean insertar(Horario h);

    boolean actualizar(Horario h);

    boolean eliminar(int id);
}
