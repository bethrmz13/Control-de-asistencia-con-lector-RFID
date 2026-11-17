/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.util;

import asistenciarfid.db.Conexion;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class RelojSeguro {

    private static final String[] SERVIDORES_NTP = {"pool.ntp.org", "time.google.com"};
    private static final int TIMEOUT_NTP_MS = 1500;
    private static final long RESINCRONIZAR_NANOS = 10L * 60L * 1_000_000_000L;
    private static final long TOLERANCIA_SISTEMA_MS = 2L * 60L * 1000L;
    private static final long SEGUNDOS_1900_A_1970 = 2208988800L;

    public static final String FUENTE_NTP = "NTP";
    public static final String FUENTE_MYSQL = "MYSQL";
    public static final String FUENTE_SISTEMA = "SISTEMA";

    private static boolean iniciado = false;
    private static boolean sincronizando = false;
    private static long baseMillis;        // hora real en el momento de sincronizar
    private static long baseNanos;         // System.nanoTime() en ese mismo momento
    private static long ultimoIntentoNanos;
    private static String fuente = FUENTE_SISTEMA;

    // Hora actual confiable. La pantalla de registro debe usar esto en lugar de new Date().
    public static synchronized Date ahora() {
        iniciar();
        long ahoraNanos = System.nanoTime();
        if (!sincronizando && ahoraNanos - ultimoIntentoNanos > RESINCRONIZAR_NANOS) {
            resincronizarEnSegundoPlano(ahoraNanos);
        }
        long transcurridoMs = (System.nanoTime() - baseNanos) / 1_000_000L;
        return new Date(baseMillis + transcurridoMs);
    }

    // Se puede llamar al abrir el programa para que la primera lectura no espere.
    public static synchronized void iniciar() {
        if (iniciado) {
            return;
        }
        Long hora = horaNtp();
        String origen = FUENTE_NTP;
        if (hora == null) {
            hora = horaMysql();
            origen = FUENTE_MYSQL;
        }
        if (hora == null) {
            hora = System.currentTimeMillis();
            origen = FUENTE_SISTEMA;
        }
        baseMillis = hora;
        baseNanos = System.nanoTime();
        ultimoIntentoNanos = baseNanos;
        fuente = origen;
        iniciado = true;
    }

    // De donde salio la hora: NTP, MYSQL o SISTEMA (la menos confiable)
    public static synchronized String getFuente() {
        return fuente;
    }

    // true si el reloj de Windows difiere mas de 2 minutos de la hora confiable
    public static boolean sistemaDesajustado() {
        long diferencia = Math.abs(System.currentTimeMillis() - ahora().getTime());
        return diferencia > TOLERANCIA_SISTEMA_MS;
    }

    private static void resincronizarEnSegundoPlano(long ahoraNanos) {
        sincronizando = true;
        ultimoIntentoNanos = ahoraNanos;
        Thread hilo = new Thread(() -> {
            Long hora = horaNtp();
            long nanos = System.nanoTime();
            aplicarNtp(hora, nanos);
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    private static synchronized void aplicarNtp(Long hora, long nanos) {
        if (hora != null) {
            baseMillis = hora;
            baseNanos = nanos;
            fuente = FUENTE_NTP;
        }
        sincronizando = false;
    }

    // Pide la hora a un servidor NTP (protocolo SNTP sobre UDP, puerto 123). null si falla.
    private static Long horaNtp() {
        for (String servidor : SERVIDORES_NTP) {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(TIMEOUT_NTP_MS);
                byte[] datos = new byte[48];
                datos[0] = 0x1B;   // version 3, modo cliente
                InetAddress direccion = InetAddress.getByName(servidor);
                socket.send(new DatagramPacket(datos, datos.length, direccion, 123));
                DatagramPacket respuesta = new DatagramPacket(datos, datos.length);
                socket.receive(respuesta);

                if ((datos[1] & 0xFF) == 0) {
                    continue;      // estrato 0: el servidor pidio no usarlo
                }
                long segundos = leer32(datos, 40);
                long fraccion = leer32(datos, 44);
                if (segundos == 0) {
                    continue;
                }
                return (segundos - SEGUNDOS_1900_A_1970) * 1000L + (fraccion * 1000L) / 0x100000000L;
            } catch (Exception ex) {
                // sin internet o servidor no disponible: se prueba con el siguiente
            }
        }
        return null;
    }

    private static long leer32(byte[] b, int pos) {
        return ((b[pos] & 0xFFL) << 24) | ((b[pos + 1] & 0xFFL) << 16)
                | ((b[pos + 2] & 0xFFL) << 8) | (b[pos + 3] & 0xFFL);
    }

    // Hora del servidor MySQL en milisegundos desde 1970 (no depende de zonas horarias). null si falla.
    private static Long horaMysql() {
        Connection cc = null;
        try {
            cc = new Conexion().conectar();
            if (cc == null) {
                return null;
            }
            Statement st = cc.createStatement();
            ResultSet rs = st.executeQuery("SELECT UNIX_TIMESTAMP()");
            Long hora = null;
            if (rs.next()) {
                hora = rs.getLong(1) * 1000L;
            }
            rs.close();
            st.close();
            return hora;
        } catch (Exception ex) {
            return null;
        } finally {
            try {
                if (cc != null) {
                    cc.close();
                }
            } catch (Exception ex) {
                // nada
            }
        }
    }
}
