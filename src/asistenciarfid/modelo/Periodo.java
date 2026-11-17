package asistenciarfid.modelo;


import java.sql.Date;

public class Periodo {

    private int id;
    private String nombre;
    private Date fechaInicio;
    private Date fechaFin;
    private boolean activo;
    private boolean matriculaAbierta;

    public Periodo() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean isMatriculaAbierta() { return matriculaAbierta; }
    public void setMatriculaAbierta(boolean matriculaAbierta) { this.matriculaAbierta = matriculaAbierta; }

    @Override
    public String toString() {
        return nombre;
    }
}