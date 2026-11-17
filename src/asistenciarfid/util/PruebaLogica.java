package asistenciarfid.util;

import asistenciarfid.controlador.AsistenciaControlador;
import asistenciarfid.controlador.MatriculaControlador;
import asistenciarfid.dao.AsistenciaDAO;
import asistenciarfid.dao.EstudianteDAO;
import asistenciarfid.dao.MatriculaDAO;
import asistenciarfid.dao.PeriodoDAO;
import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.Grupo;
import asistenciarfid.modelo.Periodo;
import asistenciarfid.modelo.ResultadoRegistro;
import asistenciarfid.dao.GrupoDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;


public class PruebaLogica {

    static int correctos = 0;
    static int fallas = 0;

    public static void main(String[] args) {
        limpiarAsistencias();
        probarAsistencia();
        probarMatricula();
        probarArreglos();
        System.out.println();
        System.out.println("Resultado: " + correctos + " OK, " + fallas + " FALLA");
    }


    static void comparar(String caso, Object esperado, Object obtenido) {
        boolean ok = esperado == null ? obtenido == null : esperado.equals(obtenido);
        if (ok) {
            correctos++;
            System.out.println("OK     " + caso + " -> " + obtenido);
        } else {
            fallas++;
            System.out.println("FALLA  " + caso + " -> esperado " + esperado + " pero fue " + obtenido);
        }
    }


    static void limpiarAsistencias() {
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return;
        }
        try {
            PreparedStatement ps = cc.prepareStatement(
                    "DELETE FROM asistencia WHERE fecha IN ('2026-11-09', '2026-11-10')");
            ps.executeUpdate();
            cc.close();
        } catch (Exception ex) {
            System.out.println("No se pudo limpiar: " + ex);
        }
    }

    static String estadoAsistencia(String codigo, Date hora) {
        ResultadoRegistro r = AsistenciaControlador.registrarPorCodigo(codigo, hora);
        return r.getEstado();
    }

    static void probarAsistencia() {
        System.out.println("=== ASISTENCIA (lunes 2026-11-09) ===");
        String maria = "0001234567";
        String luis = "0007654321";
        comparar("María 08:10", "REGISTRADO", estadoAsistencia(maria, Fechas.crear(2026, 11, 9, 8, 10)));
        comparar("María 08:12", "YA_REGISTRADO", estadoAsistencia(maria, Fechas.crear(2026, 11, 9, 8, 12)));
        comparar("María 15:55", "FUERA_DE_HORARIO", estadoAsistencia(maria, Fechas.crear(2026, 11, 9, 15, 55)));
        comparar("María 16:15", "REGISTRADO", estadoAsistencia(maria, Fechas.crear(2026, 11, 9, 16, 15)));
        comparar("María 18:05", "REGISTRADO", estadoAsistencia(maria, Fechas.crear(2026, 11, 9, 18, 5)));
        comparar("Luis 16:16 (dentro de la clase)", "REGISTRADO", estadoAsistencia(luis, Fechas.crear(2026, 11, 9, 16, 16)));
        comparar("Luis 08:05", "FUERA_DE_HORARIO", estadoAsistencia(luis, Fechas.crear(2026, 11, 9, 8, 5)));
        comparar("María 20:00 (la clase ya terminó)", "FUERA_DE_HORARIO", estadoAsistencia(maria, Fechas.crear(2026, 11, 9, 20, 0)));
        comparar("Código 9999999999", "NO_ENCONTRADO", estadoAsistencia("9999999999", Fechas.crear(2026, 11, 9, 8, 10)));
        comparar("María martes", "SIN_CLASES_HOY", estadoAsistencia(maria, Fechas.crear(2026, 11, 10, 8, 10)));
    }


    static int grupoDe(String asignatura) {
        List<Grupo> grupos = GrupoDAO.listar(null, null);
        for (Grupo g : grupos) {
            if (g.getAsignaturaNombre().equals(asignatura)) {
                return g.getId();
            }
        }
        return -1;
    }

    static String mensaje(String m) {
        return m == null ? "null (matriculado)" : m;
    }


    static void compararMensaje(String caso, String esperadoContiene, String obtenido) {
        boolean ok;
        if (esperadoContiene == null) {
            ok = obtenido == null;
        } else {
            ok = obtenido != null && obtenido.contains(esperadoContiene);
        }
        if (ok) {
            correctos++;
            System.out.println("OK     " + caso + " -> " + mensaje(obtenido));
        } else {
            fallas++;
            System.out.println("FALLA  " + caso + " -> esperado '" + esperadoContiene + "' pero fue " + mensaje(obtenido));
        }
    }

    static void probarMatricula() {
        System.out.println();
        System.out.println("=== MATRÍCULA ===");
        Estudiante maria = EstudianteDAO.buscarPorCodigo("0001234567");
        Estudiante luis = EstudianteDAO.buscarPorCodigo("0007654321");
        int calculo = grupoDe("Cálculo");
        int ingSoft = grupoDe("Ingeniería de Software");
        int sisOp = grupoDe("Sistemas Operativos");
        int estadistica = grupoDe("Estadística");
        int redes = grupoDe("Redes de Computadores");

        compararMensaje("Luis -> Ingeniería de Software", "nivel superior", MatriculaControlador.matricular(luis.getId(), ingSoft));
        compararMensaje("Luis -> Cálculo", "aprobada", MatriculaControlador.matricular(luis.getId(), calculo));
        compararMensaje("María -> Cálculo otra vez", "Ya está matriculado", MatriculaControlador.matricular(maria.getId(), calculo));
        compararMensaje("Luis -> Sistemas Operativos", "Choca con", MatriculaControlador.matricular(luis.getId(), sisOp));
        compararMensaje("María -> Estadística", null, MatriculaControlador.matricular(maria.getId(), estadistica));
        compararMensaje("María -> Redes", null, MatriculaControlador.matricular(maria.getId(), redes));
        comparar("María tiene 5 materias", 5, MatriculaDAO.contarEnPeriodoActivo(maria.getId()));
        compararMensaje("María -> Sistemas Operativos", "máximo", MatriculaControlador.matricular(maria.getId(), sisOp));

        Periodo periodo = PeriodoDAO.obtenerActivo();
        PeriodoDAO.cambiarMatricula(periodo.getId(), false);
        compararMensaje("Con matrícula cerrada", "cerradas", MatriculaControlador.matricular(luis.getId(), sisOp));
        PeriodoDAO.cambiarMatricula(periodo.getId(), true);

        MatriculaDAO.anular(maria.getId(), estadistica);
        MatriculaDAO.anular(maria.getId(), redes);
        comparar("María vuelve a 3 materias", 3, MatriculaDAO.contarEnPeriodoActivo(maria.getId()));
    }


    static void probarArreglos() {
        System.out.println();
        System.out.println("=== ARREGLOS ===");
        Estudiante maria = EstudianteDAO.buscarPorCodigo("0001234567");
        int calculo = grupoDe("Cálculo");
        int estadistica = grupoDe("Estadística");


        Estudiante luis = EstudianteDAO.buscarPorCodigo("0007654321");
        comparar("actualizarNota de una matrícula que no existe", false,
                MatriculaDAO.actualizarNota(luis.getId(), estadistica, 8.0));
        comparar("actualizarNota de una matrícula que existe", true,
                MatriculaDAO.actualizarNota(maria.getId(), calculo, 8.0));
        MatriculaDAO.actualizarNota(maria.getId(), calculo, null);   


        java.sql.Date fecha = java.sql.Date.valueOf("2026-11-10");
        java.sql.Time hora = java.sql.Time.valueOf("08:00:00");
        comparar("registrar asistencia nueva", true, AsistenciaDAO.registrar(maria.getId(), 1, fecha, hora));
        comparar("registrar asistencia repetida", false, AsistenciaDAO.registrar(maria.getId(), 1, fecha, hora));


        Periodo periodo = PeriodoDAO.obtenerActivo();
        comparar("gruposDisponibles con matrícula abierta (hay alguno)", true,
                MatriculaControlador.gruposDisponibles(maria).size() > 0);
        PeriodoDAO.cambiarMatricula(periodo.getId(), false);
        comparar("gruposDisponibles con matrícula cerrada", 0, MatriculaControlador.gruposDisponibles(maria).size());
        PeriodoDAO.cambiarMatricula(periodo.getId(), true);
    }
}
