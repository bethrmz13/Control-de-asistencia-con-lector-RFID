/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.modelo;

public class Asignatura {

    private int id;
    private int carreraId;
    private String nombre;
    private int nivel;

    public Asignatura() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCarreraId() { return carreraId; }
    public void setCarreraId(int carreraId) { this.carreraId = carreraId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }

    @Override
    public String toString() {
        return nombre;
    }
}
