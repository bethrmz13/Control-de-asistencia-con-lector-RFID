/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.modelo;

public class FilaReporte {

    private int estudianteId;
    private String estudianteNombre;
    private int presentes;
    private int programadas;
    private int faltas;
    private double porcentaje;        

    public FilaReporte() {
    }

    public int getEstudianteId() { return estudianteId; }
    public void setEstudianteId(int estudianteId) { this.estudianteId = estudianteId; }

    public String getEstudianteNombre() { return estudianteNombre; }
    public void setEstudianteNombre(String estudianteNombre) { this.estudianteNombre = estudianteNombre; }

    public int getPresentes() { return presentes; }
    public void setPresentes(int presentes) { this.presentes = presentes; }

    public int getProgramadas() { return programadas; }
    public void setProgramadas(int programadas) { this.programadas = programadas; }

    public int getFaltas() { return faltas; }
    public void setFaltas(int faltas) { this.faltas = faltas; }

    public double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }
}
