/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.modelo;


import java.sql.Time;

public class Horario {

    private int id;
    private int grupoId;
    private int diaSemana;            // 1 = lunes ... 7 = domingo
    private Time horaInicio;
    private Time horaFin;
    private String aula;

    // Solo para mostrar en tablas
    private String asignaturaNombre;
    private String paralelo;

    public Horario() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGrupoId() { return grupoId; }
    public void setGrupoId(int grupoId) { this.grupoId = grupoId; }

    public int getDiaSemana() { return diaSemana; }
    public void setDiaSemana(int diaSemana) { this.diaSemana = diaSemana; }

    public Time getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Time horaInicio) { this.horaInicio = horaInicio; }

    public Time getHoraFin() { return horaFin; }
    public void setHoraFin(Time horaFin) { this.horaFin = horaFin; }

    public String getAula() { return aula; }
    public void setAula(String aula) { this.aula = aula; }

    public String getAsignaturaNombre() { return asignaturaNombre; }
    public void setAsignaturaNombre(String asignaturaNombre) { this.asignaturaNombre = asignaturaNombre; }

    public String getParalelo() { return paralelo; }
    public void setParalelo(String paralelo) { this.paralelo = paralelo; }

    // Nombre del dia para mostrar en tablas
    public String getNombreDia() {
        String[] dias = {"", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        if (diaSemana < 1 || diaSemana > 7) {
            return "";
        }
        return dias[diaSemana];
    }
}