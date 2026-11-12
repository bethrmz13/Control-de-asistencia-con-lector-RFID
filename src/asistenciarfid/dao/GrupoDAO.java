/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

/**
 *
 * @author USER
 */

import asistenciarfid.modelo.Grupo;
import java.util.List;

public interface GrupoDAO {

    // Grupos del periodo ACTIVO. Los filtros en null no se aplican.
    List<Grupo> listar(Integer carreraId, Integer docenteId);

    List<Grupo> listarPorAsignatura(int asignaturaId); // del periodo activo

    Grupo buscarPorId(int id);

    boolean insertar(Grupo g);

    boolean actualizar(Grupo g);

    boolean eliminar(int id);
}
