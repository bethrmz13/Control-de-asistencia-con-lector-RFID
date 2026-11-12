/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

/**
 *
 * @author USER
 */

import asistenciarfid.modelo.Usuario;
import java.util.List;

public interface UsuarioDAO {

    // Devuelve el usuario (sin contrasena) o null si los datos son incorrectos
    Usuario validar(String username, String passwordPlano);

    List<Usuario> listar();

    List<Usuario> listarPorPerfil(String perfil);  // ej. todos los DOCENTE

    boolean insertar(Usuario u, String passwordPlano);

    // Si passwordPlano es null o vacio, no cambia la contrasena
    boolean actualizar(Usuario u, String passwordPlano);

    boolean eliminar(int id);
}
