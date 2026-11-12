/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.controlador;

/**
 *
 * @author USER
 */

import asistenciarfid.modelo.Estado;
import asistenciarfid.modelo.ResultadoRegistro;
import java.util.Date;

public class AsistenciaControlador {

    // La pantalla pasa new Date() (la hora actual). Las pruebas pasan una hora
    // inventada creada con Fechas.crear(...).
    public ResultadoRegistro registrarPorCodigo(String codigo, Date ahora) {
        // PENDIENTE (Damaris): reemplazar por la logica real
        return new ResultadoRegistro(Estado.NO_ENCONTRADO, null, null, "Pendiente de implementar");
    }
}