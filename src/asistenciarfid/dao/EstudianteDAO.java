/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

/**
 *
 * @author USER
 */

import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.RegistroAcademico;
import java.util.List;

public interface EstudianteDAO {

    Estudiante buscarPorCodigo(String codigoRfid);     // null si no existe

    Estudiante buscarPorUsuarioId(int usuarioId);      // null si no existe

    boolean existeCodigo(String codigoRfid);

    // Si carreraId es null no filtra por carrera
    List<Estudiante> listar(Integer carreraId);

    // En UNA transaccion: crea el usuario (perfil ESTUDIANTE), el estudiante y su tabla de materias
    boolean insertarConUsuario(Estudiante e, String username, String passwordPlano,
                               List<RegistroAcademico> materias);

    boolean actualizar(Estudiante e);

    boolean eliminar(int id);                          // tambien elimina su usuario
}