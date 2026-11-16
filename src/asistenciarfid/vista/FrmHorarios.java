package asistenciarfid.vista;

import asistenciarfid.modelo.Usuario;

public class FrmHorarios extends javax.swing.JInternalFrame {

    private Usuario usuario;

    public FrmHorarios() {
        initComponents();
    }

    public FrmHorarios(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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
        // La operación se incorpora en el commit de funcionalidad.
    }//GEN-LAST:event_jcbxGrupoActionPerformed

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        // La operación se incorpora en el commit de funcionalidad.
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        // La operación se incorpora en el commit de funcionalidad.
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jbtnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnModificarActionPerformed
        // La operación se incorpora en el commit de funcionalidad.
    }//GEN-LAST:event_jbtnModificarActionPerformed

    private void jbtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEliminarActionPerformed
        // La operación se incorpora en el commit de funcionalidad.
    }//GEN-LAST:event_jbtnEliminarActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        // La operación se incorpora en el commit de funcionalidad.
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
