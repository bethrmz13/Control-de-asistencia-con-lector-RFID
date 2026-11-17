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


    public static int diaSemana(Date fecha) {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        int dia = c.get(Calendar.DAY_OF_WEEK);   
        if (dia == Calendar.SUNDAY) {
            return 7;
        }
        return dia - 1;
    }

    public static int minutosDelDia(Date fecha) {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        return c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
    }


    public static Date crear(int anio, int mes, int dia, int hora, int minuto) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(anio, mes - 1, dia, hora, minuto, 0);
        return c.getTime();
    }
}
