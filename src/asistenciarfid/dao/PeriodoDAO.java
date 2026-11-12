/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

/**
 *
 * @author USER
 */

import asistenciarfid.modelo.Periodo;
import java.util.List;

public interface PeriodoDAO {

    Periodo obtenerActivo();                       // null si no hay periodo activo

    List<Periodo> listar();

    boolean insertar(Periodo p);

    boolean actualizar(Periodo p);

    boolean activar(int id);                       // deja solo este periodo con activo = 1

    boolean cambiarMatricula(int id, boolean abierta);
}