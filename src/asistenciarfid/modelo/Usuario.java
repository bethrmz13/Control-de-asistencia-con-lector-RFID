/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.modelo;

/**
 *
 * @author USER
 */

public class Usuario {

    private int id;
    private String username;
    private String nombre;
    private String perfil;        // una constante de la clase Perfil (ADMIN, DOCENTE...)
    private Integer carreraId;    // solo se usa si el perfil es COORDINADOR (si no, null)
    private boolean activo;

    public Usuario() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public Integer getCarreraId() { return carreraId; }
    public void setCarreraId(Integer carreraId) { this.carreraId = carreraId; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombre;
    }
}
