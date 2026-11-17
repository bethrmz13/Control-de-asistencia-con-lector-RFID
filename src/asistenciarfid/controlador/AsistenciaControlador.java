package asistenciarfid.controlador;

import asistenciarfid.dao.AsistenciaDAO;
import asistenciarfid.dao.EstudianteDAO;
import asistenciarfid.dao.HorarioDAO;
import asistenciarfid.modelo.Estado;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.Horario;
import asistenciarfid.modelo.ResultadoRegistro;
import asistenciarfid.util.BaseDatos;
import asistenciarfid.util.Fechas;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AsistenciaControlador {

    
    public static ResultadoRegistro registrarPorCodigo(String codigo, Date ahora) {
        
        if (!BaseDatos.disponible()) {
            return new ResultadoRegistro(Estado.ERROR_GUARDADO, null, null,
                    "Sin conexión a la base de datos");
        }

        
        Estudiante est = null;
        if (codigo != null) {
            est = EstudianteDAO.buscarPorCodigo(codigo.trim());
        }
        if (est == null) {
            return new ResultadoRegistro(Estado.NO_ENCONTRADO, null, null,
                    "Tarjeta no registrada");
        }

        
        List<Horario> horarios = HorarioDAO.listarDeEstudianteEnDia(est.getId(), Fechas.diaSemana(ahora));
        if (horarios.isEmpty()) {
            return new ResultadoRegistro(Estado.SIN_CLASES_HOY, est, null,
                    est.getNombre() + " no tiene clases hoy");
        }

        
        int minutosAhora = Fechas.minutosDelDia(ahora);
        Horario elegido = null;
        for (Horario h : horarios) {
            int inicio = Fechas.minutosDelDia(h.getHoraInicio());
            int fin = Fechas.minutosDelDia(h.getHoraFin());
            if (minutosAhora >= inicio && minutosAhora < fin) {
                elegido = h;
                break;
            }
        }
        if (elegido == null) {
            return new ResultadoRegistro(Estado.FUERA_DE_HORARIO, est, null,
                    "Fuera del horario de registro");
        }

        
        java.sql.Date fecha = new java.sql.Date(ahora.getTime());
        java.sql.Time hora = new java.sql.Time(ahora.getTime());
        if (AsistenciaDAO.existe(est.getId(), elegido.getId(), fecha)) {
            return new ResultadoRegistro(Estado.YA_REGISTRADO, est, elegido,
                    "Ya registró asistencia en " + elegido.getAsignaturaNombre());
        }

        
        boolean ok = AsistenciaDAO.registrar(est.getId(), elegido.getId(), fecha, hora);
        if (!ok) {
            
            if (AsistenciaDAO.existe(est.getId(), elegido.getId(), fecha)) {
                return new ResultadoRegistro(Estado.YA_REGISTRADO, est, elegido,
                        "Ya registró asistencia en " + elegido.getAsignaturaNombre());
            }
            return new ResultadoRegistro(Estado.ERROR_GUARDADO, est, elegido,
                    "No se pudo guardar la asistencia. Intente de nuevo");
        }
        String horaTexto = new SimpleDateFormat("HH:mm").format(ahora);
        return new ResultadoRegistro(Estado.REGISTRADO, est, elegido,
                "Asistencia registrada en " + elegido.getAsignaturaNombre() + " a las " + horaTexto);
    }
}
