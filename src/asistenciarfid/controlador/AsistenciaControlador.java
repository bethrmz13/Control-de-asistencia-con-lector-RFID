package asistenciarfid.controlador;

import asistenciarfid.modelo.Estado;
import asistenciarfid.modelo.ResultadoRegistro;
import java.util.Date;

public class AsistenciaControlador {

    // La pantalla pasa new Date() (la hora actual). Las pruebas pasan una hora
    // inventada creada con Fechas.crear(...).
    public static ResultadoRegistro registrarPorCodigo(String codigo, Date ahora) {
        // PENDIENTE (Damaris): reemplazar por la lógica real
        return new ResultadoRegistro(Estado.NO_ENCONTRADO, null, null, "Pendiente de implementar");
    }
}
