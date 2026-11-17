/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asistenciarfid.modelo;

public class ResultadoRegistro {

    private String estado;            
    private Estudiante estudiante;    
    private Horario horario;          
    private String mensaje;

    public ResultadoRegistro(String estado, Estudiante estudiante, Horario horario, String mensaje) {
        this.estado = estado;
        this.estudiante = estudiante;
        this.horario = horario;
        this.mensaje = mensaje;
    }

    public String getEstado() { return estado; }
    public Estudiante getEstudiante() { return estudiante; }
    public Horario getHorario() { return horario; }
    public String getMensaje() { return mensaje; }
}
