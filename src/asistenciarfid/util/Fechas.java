/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.util;

/**
 *
 * @author USER
 */
import java.util.Calendar;
import java.util.Date;

public class Fechas {

    // Dia de la semana como en la base de datos: 1 = lunes ... 7 = domingo
    public static int diaSemana(Date fecha) {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        int dia = c.get(Calendar.DAY_OF_WEEK);   // en Java: 1 = domingo ... 7 = sabado
        if (dia == Calendar.SUNDAY) {
            return 7;
        }
        return dia - 1;
    }

    // Minutos desde las 00:00 (ej. 16:10 -> 970). Sirve para comparar horas con numeros.
    public static int minutosDelDia(Date fecha) {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        return c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
    }

    // Crea una fecha y hora para las pruebas. El mes va de 1 a 12.
    // Ejemplo: Fechas.crear(2026, 11, 9, 16, 10) es el lunes 9 de noviembre a las 16:10.
    public static Date crear(int anio, int mes, int dia, int hora, int minuto) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(anio, mes - 1, dia, hora, minuto, 0);
        return c.getTime();
    }
}
