/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

/**
 *
 * @author USER
 */

import java.sql.Date;
import java.util.List;

public interface FeriadoDAO {

    List<Date> listarEntre(Date desde, Date hasta);
}