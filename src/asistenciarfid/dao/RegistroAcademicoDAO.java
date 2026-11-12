/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

/**
 *
 * @author USER
 */

import asistenciarfid.modelo.RegistroAcademico;
import java.util.List;

public interface RegistroAcademicoDAO {

    List<RegistroAcademico> listarPorEstudiante(int estudianteId);

    // NO guarda nada: arma la lista de materias de la carrera. Nivel menor al de inicio
    // = APROBADA, las demas = AUN_NO_CURSA. Sirve para mostrarla en FrmEstudiantes.
    List<RegistroAcademico> generarParaCarrera(int carreraId, int nivelInicio);

    String estadoDe(int estudianteId, int asignaturaId);   // null si no existe

    boolean actualizarEstado(int estudianteId, int asignaturaId, String estado);
}
