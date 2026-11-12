/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.modelo;

public class Grupo {

    private int id;
    private int periodoId;
    private int asignaturaId;
    private String paralelo;
    private Integer docenteId;        

    
    private String asignaturaNombre;
    private int asignaturaNivel;
    private String docenteNombre;

    public Grupo() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPeriodoId() { return periodoId; }
    public void setPeriodoId(int periodoId) { this.periodoId = periodoId; }

    public int getAsignaturaId() { return asignaturaId; }
    public void setAsignaturaId(int asignaturaId) { this.asignaturaId = asignaturaId; }

    public String getParalelo() { return paralelo; }
    public void setParalelo(String paralelo) { this.paralelo = paralelo; }

    public Integer getDocenteId() { return docenteId; }
    public void setDocenteId(Integer docenteId) { this.docenteId = docenteId; }

    public String getAsignaturaNombre() { return asignaturaNombre; }
    public void setAsignaturaNombre(String asignaturaNombre) { this.asignaturaNombre = asignaturaNombre; }

    public int getAsignaturaNivel() { return asignaturaNivel; }
    public void setAsignaturaNivel(int asignaturaNivel) { this.asignaturaNivel = asignaturaNivel; }

    public String getDocenteNombre() { return docenteNombre; }
    public void setDocenteNombre(String docenteNombre) { this.docenteNombre = docenteNombre; }

    @Override
    public String toString() {
        return asignaturaNombre + " - " + paralelo;
    }
}
