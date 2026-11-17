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


public class RelojSeguro {

    private static boolean iniciado = false;
    private static long horaBase;    
    private static long nanosBase;   

    public static void iniciar() {
        if (iniciado) {
            return;
        }
        long hora = horaDeMysql();
        if (hora == 0) {
            hora = System.currentTimeMillis();
        }
        horaBase = hora;
        nanosBase = System.nanoTime();
        iniciado = true;
    }

    public static Date ahora() {
        iniciar();
        long transcurrido = (System.nanoTime() - nanosBase) / 1000000;
        return new Date(horaBase + transcurrido);
    }

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


    