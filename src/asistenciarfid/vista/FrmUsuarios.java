package asistenciarfid.vista;

import asistenciarfid.modelo.Usuario;

public class FrmUsuarios extends javax.swing.JInternalFrame {

    private Usuario usuario;

    public FrmUsuarios() {
        initComponents();
    }

    public FrmUsuarios(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlblUsuario = new javax.swing.JLabel();
        jtxtUsuario = new javax.swing.JTextField();
        jlblClave = new javax.swing.JLabel();
        jpwdClave = new javax.swing.JPasswordField();
        jlblNombre = new javax.swing.JLabel();
        jtxtNombre = new javax.swing.JTextField();
        jlblPerfil = new javax.swing.JLabel();
        jcbxPerfil = new javax.swing.JComboBox<>();
        jlblCarrera = new javax.swing.JLabel();
        jcbxCarrera = new javax.swing.JComboBox<>();
        jlblActivo = new javax.swing.JLabel();
        jchkActivo = new javax.swing.JCheckBox();
        jbtnNuevo = new javax.swing.JButton();
        jbtnGuardar = new javax.swing.JButton();
        jbtnModificar = new javax.swing.JButton();
        jbtnEliminar = new javax.swing.JButton();
        jbtnCancelar = new javax.swing.JButton();
        jscrUsuarios = new javax.swing.JScrollPane();
        jtblUsuarios = new javax.swing.JTable();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestión de usuarios");

        jlblUsuario.setText("Usuario:");
        jlblClave.setText("Contraseña:");
        jlblNombre.setText("Nombre:");
        jlblPerfil.setText("Perfil:");
        jcbxPerfil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione" }));
        jcbxPerfil.addActionListener(this::jcbxPerfilActionPerformed);
        jlblCarrera.setText("Carrera:");
        jcbxCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione" }));
        jlblActivo.setText("Estado:");
        jchkActivo.setText("Activo");
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

        jtblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] { "ID", "Usuario", "Nombre", "Perfil", "Carrera", "Activo" }
        ));
        jscrUsuarios.setViewportView(jtblUsuarios);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblUsuario, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jtxtUsuario, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblClave, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jpwdClave, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblNombre, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jtxtNombre, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblPerfil, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jcbxPerfil, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblCarrera, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jcbxCarrera, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblActivo, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jchkActivo, 250, 500, Short.MAX_VALUE))
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
                    .addComponent(jscrUsuarios, 500, 650, Short.MAX_VALUE))
                .addGap(12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblUsuario)
                    .addComponent(jtxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblClave)
                    .addComponent(jpwdClave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblNombre)
                    .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblPerfil)
                    .addComponent(jcbxPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblCarrera)
                    .addComponent(jcbxCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblActivo)
                    .addComponent(jchkActivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnNuevo)
                    .addComponent(jbtnGuardar)
                    .addComponent(jbtnModificar)
                    .addComponent(jbtnEliminar)
                    .addComponent(jbtnCancelar))
                .addGap(12)
                .addComponent(jscrUsuarios, 150, 220, Short.MAX_VALUE)
                .addGap(12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jcbxPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbxPerfilActionPerformed
        // La operación se incorpora en el commit de funcionalidad.
    }//GEN-LAST:event_jcbxPerfilActionPerformed

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
                new FrmUsuarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jlblUsuario;
    private javax.swing.JTextField jtxtUsuario;
    private javax.swing.JLabel jlblClave;
    private javax.swing.JPasswordField jpwdClave;
    private javax.swing.JLabel jlblNombre;
    private javax.swing.JTextField jtxtNombre;
    private javax.swing.JLabel jlblPerfil;
    private javax.swing.JComboBox<Object> jcbxPerfil;
    private javax.swing.JLabel jlblCarrera;
    private javax.swing.JComboBox<Object> jcbxCarrera;
    private javax.swing.JLabel jlblActivo;
    private javax.swing.JCheckBox jchkActivo;
    private javax.swing.JButton jbtnNuevo;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JButton jbtnModificar;
    private javax.swing.JButton jbtnEliminar;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JScrollPane jscrUsuarios;
    private javax.swing.JTable jtblUsuarios;
    // End of variables declaration//GEN-END:variables
}
