/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.modelo;

public class RegistroAcademico {

    private int estudianteId;
    private int asignaturaId;
    private String estado;            // usa las constantes de EstadoMateria

    // Solo para mostrar en tablas
    private String asignaturaNombre;
    private int asignaturaNivel;

    public RegistroAcademico() {
    }

    public int getEstudianteId() { return estudianteId; }
    public void setEstudianteId(int estudianteId) { this.estudianteId = estudianteId; }

    public int getAsignaturaId() { return asignaturaId; }
    public void setAsignaturaId(int asignaturaId) { this.asignaturaId = asignaturaId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getAsignaturaNombre() { return asignaturaNombre; }
    public void setAsignaturaNombre(String asignaturaNombre) { this.asignaturaNombre = asignaturaNombre; }

    public int getAsignaturaNivel() { return asignaturaNivel; }
    public void setAsignaturaNivel(int asignaturaNivel) { this.asignaturaNivel = asignaturaNivel; }
}
