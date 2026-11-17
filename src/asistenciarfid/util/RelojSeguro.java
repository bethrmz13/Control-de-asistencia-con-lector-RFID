/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package asistenciarfid.util;

import asistenciarfid.db.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.swing.JOptionPane;

// Reloj del programa. Se usa en lugar de new Date() para que cambiar la hora de Windows
// no afecte el registro de asistencia.
// Al iniciar lee la hora de MySQL una sola vez. Despues cuenta el tiempo con System.nanoTime(),
// que es un contador que no cambia aunque alguien modifique la hora del computador.
public class RelojSeguro {

    private static boolean iniciado = false;
    private static long horaBase;    // hora en milisegundos leida al iniciar
    private static long nanosBase;   // System.nanoTime() en ese mismo momento

    // Se llama una vez al abrir el programa (si no, ahora() la llama sola la primera vez)
    public static void iniciar() {
        if (iniciado) {
            return;
        }
        long hora = horaDeMysql();
        if (hora == 0) {
            // Si no se pudo leer MySQL se usa la hora del sistema
            hora = System.currentTimeMillis();
        }
        horaBase = hora;
        nanosBase = System.nanoTime();
        iniciado = true;
    }

    // Hora actual confiable
    public static Date ahora() {
        iniciar();
        long transcurrido = (System.nanoTime() - nanosBase) / 1000000;
        return new Date(horaBase + transcurrido);
    }

    // Hora del servidor MySQL en milisegundos. Devuelve 0 si no se pudo leer.
    private static long horaDeMysql() {
        long hora = 0;
        Conexion cn = new Conexion();
        Connection cc = cn.conectar();
        if (cc == null) {
            return 0;
        }
        try {
            PreparedStatement ps = cc.prepareStatement("SELECT UNIX_TIMESTAMP()");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                hora = rs.getLong(1) * 1000;
            }
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return hora;
    }
}


    