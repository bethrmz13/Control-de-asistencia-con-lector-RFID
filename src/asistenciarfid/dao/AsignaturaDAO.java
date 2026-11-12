/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

/**
 *
 * @author USER
 */

import asistenciarfid.modelo.Asignatura;
import java.util.List;

public interface AsignaturaDAO {

    List<Asignatura> listar(Integer carreraId);        // null = todas las carreras

    Asignatura buscarPorId(int id);

    boolean insertar(Asignatura a);

    boolean actualizar(Asignatura a);

    boolean eliminar(int id);
}
