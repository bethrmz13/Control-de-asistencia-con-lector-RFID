/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

/**
 *
 * @author USER
 */

import asistenciarfid.modelo.Matricula;
import java.util.List;

public interface MatriculaDAO {

    boolean matricular(int estudianteId, int grupoId);

    boolean anular(int estudianteId, int grupoId);

    List<Matricula> listarPorEstudiante(int estudianteId);   // del periodo activo

    List<Matricula> listarPorGrupo(int grupoId);             // con nombre y nota

    int contarEnPeriodoActivo(int estudianteId);

    // true si ya esta matriculado en algun paralelo de esa asignatura (periodo activo)
    boolean existeEnAsignatura(int estudianteId, int asignaturaId);

    boolean actualizarNota(int estudianteId, int grupoId, Double nota);   // nota null = borrar

    List<Matricula> listarSinNota();                         // del periodo activo
}
