/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.modelo;

public class Estudiante {

    private int id;
    private Integer usuarioId;     
    private String codigoRfid;
    private String nombre;
    private String apellido;
    private String correo;
    private int carreraId;
    private int nivel;             

    public Estudiante() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getCodigoRfid() { return codigoRfid; }
    public void setCodigoRfid(String codigoRfid) { this.codigoRfid = codigoRfid; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public int getCarreraId() { return carreraId; }
    public void setCarreraId(int carreraId) { this.carreraId = carreraId; }

    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}
