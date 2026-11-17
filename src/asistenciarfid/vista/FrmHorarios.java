package asistenciarfid.vista;

import asistenciarfid.controlador.HorarioControlador;
import asistenciarfid.modelo.Usuario;
import asistenciarfid.modelo.Grupo;
import asistenciarfid.modelo.Horario;
import asistenciarfid.modelo.Perfil;
import asistenciarfid.dao.GrupoDAO;
import asistenciarfid.dao.HorarioDAO;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class FrmHorarios extends javax.swing.JInternalFrame {

    private Usuario usuario;

    public FrmHorarios() {
        this(null);
    }

    public FrmHorarios(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        iniciarFormulario();
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }


    private List<Grupo> grupos = new ArrayList<>();
    private List<Horario> horarios = new ArrayList<>();
    private int idHorarioSeleccionado;
    private boolean nuevo;
    private boolean cargandoGrupos;
    private Time horaInicioValidada;
    private Time horaFinValidada;

    private boolean tienePermiso() {
        return usuario != null && (Perfil.ADMIN.equals(usuario.getPerfil())
                || (Perfil.COORDINADOR.equals(usuario.getPerfil())
                && usuario.getCarreraId() != null && usuario.getCarreraId() > 0));
    }

    private Integer filtroCarrera() {
        if (Perfil.COORDINADOR.equals(usuario.getPerfil())) {
            return usuario.getCarreraId();
        }
        return null;
    }

    private void iniciarFormulario() {
        jcbxDia.setModel(new DefaultComboBoxModel<Object>(new String[]{
            "Seleccione", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"}));
        jtblHorarios.setDefaultEditor(Object.class, null);
        jtblHorarios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtblHorarios.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evento) {
                if (!evento.getValueIsAdjusting()) {
                    seleccionarHorario();
                }
            }
        });
        if (tienePermiso()) {
            cargarGrupos();
            if (grupos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay grupos disponibles. Revise el periodo activo y la conexión.");
            }
        } else if (usuario != null) {
            JOptionPane.showMessageDialog(this,
                    "Solo ADMIN o un COORDINADOR con carrera pueden gestionar horarios.");
        }
        Cancelar();
    }

    private void cargarGrupos() {
        cargandoGrupos = true;
        DefaultComboBoxModel<Object> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Seleccione");
        grupos = new ArrayList<>();
        if (tienePermiso()) {
            grupos = GrupoDAO.listar(filtroCarrera(), null);
            for (Grupo grupo : grupos) {
                modelo.addElement(grupo);
            }
        }
        jcbxGrupo.setModel(modelo);
        cargandoGrupos = false;
        limpiarTabla();
        Cancelar();
    }

    private Grupo grupoSeleccionado() {
        if (!tienePermiso() || jcbxGrupo.getSelectedIndex() <= 0
                || !(jcbxGrupo.getSelectedItem() instanceof Grupo)) {
            return null;
        }
        Grupo seleccionado = (Grupo) jcbxGrupo.getSelectedItem();
        for (Grupo grupo : grupos) {
            if (grupo.getId() == seleccionado.getId()) {
                return grupo;
            }
        }
        return null;
    }

    
    private boolean validarGrupo() {
        Grupo seleccionado = grupoSeleccionado();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un grupo permitido del periodo activo.");
            jcbxGrupo.requestFocus();
            return false;
        }
        List<Grupo> vigentes = GrupoDAO.listar(filtroCarrera(), null);
        for (Grupo vigente : vigentes) {
            if (vigente.getId() == seleccionado.getId()) {
                return true;
            }
        }
        JOptionPane.showMessageDialog(this,
                "El grupo ya no está disponible para su perfil o periodo. Selecciónelo nuevamente.");
        cargarGrupos();
        return false;
    }

    private void limpiarTabla() {
        idHorarioSeleccionado = 0;
        jtblHorarios.clearSelection();
        horarios = new ArrayList<>();
        String[] titulos = {"Día", "Hora de inicio", "Hora de finalización", "Aula"};
        jtblHorarios.setModel(new DefaultTableModel(null, titulos));
    }

    private void cargarHorarios() {
        limpiarTabla();
        Grupo grupo = grupoSeleccionado();
        if (grupo == null || !validarGrupo()) {
            return;
        }
        horarios = HorarioDAO.listarPorGrupo(grupo.getId());
        DefaultTableModel modelo = (DefaultTableModel) jtblHorarios.getModel();
        for (Horario horario : horarios) {
            modelo.addRow(new Object[]{horario.getNombreDia(), mostrarHora(horario.getHoraInicio()),
                mostrarHora(horario.getHoraFin()), horario.getAula()});
        }
    }

    private String mostrarHora(Time hora) {
        return hora.toString().substring(0, 5);
    }

    private void seleccionarHorario() {
        if (!tienePermiso() || nuevo || jtblHorarios.getSelectedRow() < 0
                || grupoSeleccionado() == null) {
            return;
        }
        int fila = jtblHorarios.convertRowIndexToModel(jtblHorarios.getSelectedRow());
        if (fila >= horarios.size()) {
            return;
        }
        Horario seleccionado = horarios.get(fila);
        if (seleccionado.getGrupoId() != grupoSeleccionado().getId()) {
            return;
        }
        idHorarioSeleccionado = seleccionado.getId();
        jcbxDia.setSelectedIndex(seleccionado.getDiaSemana());
        jtxtHoraInicio.setText(mostrarHora(seleccionado.getHoraInicio()));
        jtxtHoraFin.setText(mostrarHora(seleccionado.getHoraFin()));
        jtxtAula.setText(seleccionado.getAula());
        InformacionEditar();
    }

    private void habilitarInformacion(boolean habilitar) {
        jcbxDia.setEnabled(habilitar);
        jtxtHoraInicio.setEnabled(habilitar);
        jtxtHoraFin.setEnabled(habilitar);
        jtxtAula.setEnabled(habilitar);
    }

    private void InformacionInicio() {
        habilitarInformacion(false);
        jcbxGrupo.setEnabled(tienePermiso() && !grupos.isEmpty());
    }

    private void BotonesInicio() {
        jbtnNuevo.setEnabled(grupoSeleccionado() != null);
        jbtnGuardar.setEnabled(false);
        jbtnModificar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(false);
        jtblHorarios.setEnabled(tienePermiso());
    }

    private void InformacionNuevo() {
        habilitarInformacion(true);
        jcbxGrupo.setEnabled(false);
        jtblHorarios.setEnabled(false);
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(true);
        jbtnModificar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(true);
        jcbxDia.requestFocus();
    }

    private void InformacionEditar() {
        habilitarInformacion(true);
        jcbxGrupo.setEnabled(false);
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(false);
        jbtnModificar.setEnabled(true);
        jbtnEliminar.setEnabled(true);
        jbtnCancelar.setEnabled(true);
    }

    private void Limpiar() {
        idHorarioSeleccionado = 0;
        jtblHorarios.clearSelection();
        jcbxDia.setSelectedIndex(0);
        jtxtHoraInicio.setText("");
        jtxtHoraFin.setText("");
        jtxtAula.setText("");
        horaInicioValidada = null;
        horaFinValidada = null;
    }

    private void Cancelar() {
        nuevo = false;
        Limpiar();
        InformacionInicio();
        BotonesInicio();
    }

    private Time convertirHora(String texto) {
        if (!texto.matches("([01][0-9]|2[0-3]):[0-5][0-9]")) {
            throw new IllegalArgumentException("Formato HH:mm inválido.");
        }
        return Time.valueOf(texto + ":00");
    }

    private boolean validarCampos() {
        if (!tienePermiso()) {
            JOptionPane.showMessageDialog(this, "Su perfil no permite gestionar horarios.");
            return false;
        }
        if (grupoSeleccionado() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un grupo del periodo activo.");
            jcbxGrupo.requestFocus();
            return false;
        }
        if (jcbxDia.getSelectedIndex() < 1 || jcbxDia.getSelectedIndex() > 7) {
            JOptionPane.showMessageDialog(this, "Seleccione un día de lunes a domingo.");
            jcbxDia.requestFocus();
            return false;
        }
        try {
            horaInicioValidada = convertirHora(jtxtHoraInicio.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese la hora de inicio en formato HH:mm (00:00 a 23:59).");
            jtxtHoraInicio.requestFocus();
            return false;
        }
        try {
            horaFinValidada = convertirHora(jtxtHoraFin.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese la hora de fin en formato HH:mm (00:00 a 23:59).");
            jtxtHoraFin.requestFocus();
            return false;
        }
        if (!horaInicioValidada.before(horaFinValidada)) {
            JOptionPane.showMessageDialog(this, "La hora inicial debe ser menor que la hora final.");
            jtxtHoraFin.requestFocus();
            return false;
        }
        
        if (jtxtAula.getText().trim().length() > 20) {
            JOptionPane.showMessageDialog(this, "El aula admite hasta 20 caracteres y es opcional.");
            jtxtAula.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validarSeleccion() {
        if (!tienePermiso()) {
            JOptionPane.showMessageDialog(this, "Su perfil no permite gestionar horarios.");
            return false;
        }
        int fila = jtblHorarios.getSelectedRow();
        if (nuevo || fila < 0 || idHorarioSeleccionado <= 0 || grupoSeleccionado() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un horario de la tabla.");
            jtblHorarios.requestFocus();
            return false;
        }
        int indice = jtblHorarios.convertRowIndexToModel(fila);
        if (indice >= horarios.size() || horarios.get(indice).getId() != idHorarioSeleccionado
                || horarios.get(indice).getGrupoId() != grupoSeleccionado().getId()) {
            JOptionPane.showMessageDialog(this, "El horario no pertenece al grupo seleccionado.");
            jtblHorarios.requestFocus();
            return false;
        }
        return true;
    }

    private void guardarHorario(boolean modificar) {
        if (modificar) {
            if (!validarSeleccion()) {
                return;
            }
        } else if (!tienePermiso() || !nuevo) {
            return;
        }
        if (!validarCampos() || !validarGrupo()) {
            return;
        }
        Horario horario = new Horario();
        horario.setId(idHorarioSeleccionado);
        horario.setGrupoId(grupoSeleccionado().getId());
        horario.setDiaSemana(jcbxDia.getSelectedIndex());
        horario.setHoraInicio(horaInicioValidada);
        horario.setHoraFin(horaFinValidada);
        String aula = jtxtAula.getText().trim();
        horario.setAula(aula.isEmpty() ? null : aula);
        String errorChoque = HorarioControlador.validarChoques(horario);
        if (errorChoque != null) {
            JOptionPane.showMessageDialog(this, errorChoque);
            return;
        }
        boolean resultado;
        if (modificar) {
            resultado = HorarioDAO.actualizar(horario);
        } else {
            resultado = HorarioDAO.insertar(horario);
        }
        if (resultado) {
            cargarHorarios();
            Cancelar();
            JOptionPane.showMessageDialog(this, "Horario " + (modificar ? "modificado." : "registrado."));
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el horario. Revise los datos y el mensaje del DAO.");
        }
    }

    private void eliminarHorario() {
        if (!validarSeleccion() || !validarGrupo()) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar el horario seleccionado?", "Eliminar horario",
                JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        
        if (!validarGrupo()) {
            return;
        }
        if (HorarioDAO.eliminar(idHorarioSeleccionado)) {
            cargarHorarios();
            Cancelar();
            JOptionPane.showMessageDialog(this, "Horario eliminado.");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el horario.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlblGrupo = new javax.swing.JLabel();
        jcbxGrupo = new javax.swing.JComboBox<>();
        jlblDia = new javax.swing.JLabel();
        jcbxDia = new javax.swing.JComboBox<>();
        jlblHoraInicio = new javax.swing.JLabel();
        jtxtHoraInicio = new javax.swing.JTextField();
        jlblHoraFin = new javax.swing.JLabel();
        jtxtHoraFin = new javax.swing.JTextField();
        jlblAula = new javax.swing.JLabel();
        jtxtAula = new javax.swing.JTextField();
        jbtnNuevo = new javax.swing.JButton();
        jbtnGuardar = new javax.swing.JButton();
        jbtnModificar = new javax.swing.JButton();
        jbtnEliminar = new javax.swing.JButton();
        jbtnCancelar = new javax.swing.JButton();
        jscrHorarios = new javax.swing.JScrollPane();
        jtblHorarios = new javax.swing.JTable();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestión de horarios");

        jlblGrupo.setText("Grupo:");
        jcbxGrupo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione" }));
        jcbxGrupo.addActionListener(this::jcbxGrupoActionPerformed);
        jlblDia.setText("Día:");
        jcbxDia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione" }));
        jlblHoraInicio.setText("Inicio (HH:mm):");
        jlblHoraFin.setText("Fin (HH:mm):");
        jlblAula.setText("Aula (opcional):");
        jbtnNuevo.setText("Nuevo");
        jbtnNuevo.addActionListener(this::jbtnNuevoActionPerformed);
        jbtnGuardar.setText("Guardar");
        jbtnGuardar.addActionListener(this::jbtnGuardarActionPerformed);
        jbtnModificar.setText("Modificar");
        jbtnModificar.addActionListener(this::jbtnModificarActionPerformed);
        jbtnEliminar.setText("Eliminar");
        jbtnEliminar.addActionListener(this::jbtnEliminarActionPerformed);
        jbtnCancelar.setText("Cancelar");
        jbtnCancelar.addActionListener(this::jbtnCancelarActionPerformed);

        jtblHorarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] { "Día", "Hora de inicio", "Hora de finalización", "Aula" }
        ));
        jscrHorarios.setViewportView(jtblHorarios);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblGrupo, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jcbxGrupo, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblDia, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jcbxDia, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblHoraInicio, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jtxtHoraInicio, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblHoraFin, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jtxtHoraFin, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblAula, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jtxtAula, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbtnNuevo)
                        .addGap(8)
                        .addComponent(jbtnGuardar)
                        .addGap(8)
                        .addComponent(jbtnModificar)
                        .addGap(8)
                        .addComponent(jbtnEliminar)
                        .addGap(8)
                        .addComponent(jbtnCancelar))
                    .addComponent(jscrHorarios, 500, 650, Short.MAX_VALUE))
                .addGap(12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblGrupo)
                    .addComponent(jcbxGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblDia)
                    .addComponent(jcbxDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblHoraInicio)
                    .addComponent(jtxtHoraInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblHoraFin)
                    .addComponent(jtxtHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblAula)
                    .addComponent(jtxtAula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnNuevo)
                    .addComponent(jbtnGuardar)
                    .addComponent(jbtnModificar)
                    .addComponent(jbtnEliminar)
                    .addComponent(jbtnCancelar))
                .addGap(12)
                .addComponent(jscrHorarios, 150, 220, Short.MAX_VALUE)
                .addGap(12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jcbxGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbxGrupoActionPerformed
        if (!cargandoGrupos) {
            Cancelar();
            cargarHorarios();
            BotonesInicio();
        }
    }//GEN-LAST:event_jcbxGrupoActionPerformed

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        if (tienePermiso() && validarGrupo()) {
            nuevo = true;
            Limpiar();
            InformacionNuevo();
        }
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        guardarHorario(false);
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jbtnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnModificarActionPerformed
        guardarHorario(true);
    }//GEN-LAST:event_jbtnModificarActionPerformed

    private void jbtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEliminarActionPerformed
        eliminarHorario();
    }//GEN-LAST:event_jbtnEliminarActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        Cancelar();
    }//GEN-LAST:event_jbtnCancelarActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FrmHorarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jlblGrupo;
    private javax.swing.JComboBox<Object> jcbxGrupo;
    private javax.swing.JLabel jlblDia;
    private javax.swing.JComboBox<Object> jcbxDia;
    private javax.swing.JLabel jlblHoraInicio;
    private javax.swing.JTextField jtxtHoraInicio;
    private javax.swing.JLabel jlblHoraFin;
    private javax.swing.JTextField jtxtHoraFin;
    private javax.swing.JLabel jlblAula;
    private javax.swing.JTextField jtxtAula;
    private javax.swing.JButton jbtnNuevo;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JButton jbtnModificar;
    private javax.swing.JButton jbtnEliminar;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JScrollPane jscrHorarios;
    private javax.swing.JTable jtblHorarios;
    // End of variables declaration//GEN-END:variables
}
