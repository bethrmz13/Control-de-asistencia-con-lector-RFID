/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

import asistenciarfid.modelo.Carrera;
import java.util.List;

public interface CarreraDAO {

    List<Carrera> listar();

    Carrera buscarPorId(int id);
}
