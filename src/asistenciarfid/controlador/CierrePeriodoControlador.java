package asistenciarfid.controlador;

import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.Matricula;
import java.util.ArrayList;
import java.util.List;

public class CierrePeriodoControlador {

    // Matrículas del periodo activo que todavía no tienen nota
    public static List<Matricula> notasFaltantes() {
        // PENDIENTE (Damaris)
        return new ArrayList<>();
    }

    // Cierra el periodo activo. Devuelve los estudiantes "por revisar" (los que
    // reprobaron algo), o null si no se pudo cerrar (faltan notas o hubo un error).
    public static List<Estudiante> cerrarPeriodo() {
        // PENDIENTE (Damaris)
        return null;
    }
}
