/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.dao;

/**
 *
 * @author USER
 */

import asistenciarfid.modelo.Asistencia;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface AsistenciaDAO {

    boolean existe(int estudianteId, int horarioId, Date fecha);

    boolean registrar(int estudianteId, int horarioId, Date fecha, Time hora);

    List<Asistencia> listarPorHorarioYFecha(int horarioId, Date fecha);

    // Cuenta las asistencias del estudiante en los horarios de ese grupo, entre dos fechas
    int contarPresentes(int estudianteId, int grupoId, Date desde, Date hasta);
}
