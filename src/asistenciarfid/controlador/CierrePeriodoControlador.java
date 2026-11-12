/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.controlador;


import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.Matricula;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author USER
 */
public class CierrePeriodoControlador {
    // Matriculas del periodo activo que todavia no tienen nota
    public List<Matricula> notasFaltantes() {
        // PENDIENTE (Damaris)
        return new ArrayList<>();
    }

    // Cierra el periodo activo. Devuelve los estudiantes "por revisar" (los que
    // reprobaron algo), o null si no se pudo cerrar (faltan notas o hubo un error).
    public List<Estudiante> cerrarPeriodo() {
        // PENDIENTE (Damaris)
        return null;
    }
}
