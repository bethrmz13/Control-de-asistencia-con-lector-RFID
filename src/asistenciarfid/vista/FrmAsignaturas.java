/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package asistenciarfid.vista;

import asistenciarfid.modelo.Usuario;
import asistenciarfid.modelo.Asignatura;
import asistenciarfid.modelo.Carrera;

import asistenciarfid.dao.AsignaturaDAO;
import asistenciarfid.dao.CarreraDAO;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author USER
 */
public class FrmAsignaturas extends javax.swing.JInternalFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmAsignaturas.class.getName());

    /**
     * Creates new form FrmAsignaturas
     */
    public FrmAsignaturas() {
        initComponents();
    }
    
    private Usuario usuario;
    private DefaultTableModel modeloTabla;
    private int idAsignaturaSeleccionada = 0;
    private boolean modoEdicion = false;

    public FrmAsignaturas(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
       
        configurarNivel();
        configurarTabla();
        cargarCarreras();
        cargarAsignaturas();
        
        estadoInicial();
    }
    private void configurarNivel() {

        jspnNivel.setModel(
            new SpinnerNumberModel(1, 1, 10, 1)
        );

    }
    
    private void configurarTabla() {

        modeloTabla = new DefaultTableModel();

        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Nivel");
        modeloTabla.addColumn("Carrera");

        jtblAsignaturas.setModel(modeloTabla);

        jtblAsignaturas.setDefaultEditor(
            Object.class, null
        );

        jtblAsignaturas.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION
        );

    }
    
    
    
    private void cargarCarreras() {

        jcbxCarrera.removeAllItems();

        List<Carrera> carreras = CarreraDAO.listar();

        for (Carrera carrera : carreras) {

            if (usuario != null
                    && "COORDINADOR".equalsIgnoreCase(usuario.getPerfil())) {

                if (usuario.getCarreraId() != null
                        && carrera.getId() == usuario.getCarreraId()) {

                    jcbxCarrera.addItem(carrera);

                }

            } else {

                jcbxCarrera.addItem(carrera);

            }
        }
    }
    
    private void cargarAsignaturas() {

        modeloTabla.setRowCount(0);

        Integer carreraId = null;

        if (usuario != null
                && "COORDINADOR".equalsIgnoreCase(usuario.getPerfil())) {

            carreraId = usuario.getCarreraId();

            if (carreraId == null) {
                return;
            }
        }

        List<Asignatura> asignaturas =
                AsignaturaDAO.listar(carreraId);

        for (Asignatura asignatura : asignaturas) {

            Carrera carrera =
                    CarreraDAO.buscarPorId(asignatura.getCarreraId());

            String nombreCarrera =
                    carrera != null ? carrera.getNombre() : "";

            modeloTabla.addRow(new Object[]{

                asignatura.getId(),
                asignatura.getNombre(),
                asignatura.getNivel(),
                nombreCarrera

            });
        }
    }

    private void limpiarCampos() {

        jtxtNombre.setText("");

        jspnNivel.setValue(1);

        if (jcbxCarrera.getItemCount() > 0) {
            jcbxCarrera.setSelectedIndex(0);
        }

        jtblAsignaturas.clearSelection();

        idAsignaturaSeleccionada = 0;

        jtxtNombre.requestFocusInWindow();
    }
                                          
    
    private void nuevo() {

        if (usuario == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe iniciar sesión para gestionar asignaturas."
            );

            return;
        }

        String perfil = usuario.getPerfil();

        if (!"ADMIN".equalsIgnoreCase(perfil)
                && !"COORDINADOR".equalsIgnoreCase(perfil)) {

            JOptionPane.showMessageDialog(
                    this,
                    "No tiene permisos para registrar asignaturas."
            );

            return;
        }

        if ("COORDINADOR".equalsIgnoreCase(perfil)
                && usuario.getCarreraId() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "El coordinador no tiene una carrera asignada."
            );

            return;
        }

        limpiarCampos();

        jtxtNombre.setEnabled(true);
        jspnNivel.setEnabled(true);

        boolean esCoordinador =
                "COORDINADOR".equalsIgnoreCase(perfil);

        jcbxCarrera.setEnabled(!esCoordinador);

        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(true);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(true);

        jtxtNombre.requestFocusInWindow();

    }

    private void guardar() {

        if (usuario == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe iniciar sesión para guardar asignaturas."
            );

            return;
        }

        String perfil = usuario.getPerfil();

        if (!"ADMIN".equalsIgnoreCase(perfil)
                && !"COORDINADOR".equalsIgnoreCase(perfil)) {

            JOptionPane.showMessageDialog(
                    this,
                    "No tiene permisos para guardar asignaturas."
            );

            return;
        }

        if (!validarCampos()) {
            return;
        }

        String nombre = jtxtNombre.getText().trim();

        int nivel = (Integer) jspnNivel.getValue();

        Carrera carrera =
                (Carrera) jcbxCarrera.getSelectedItem();


        if ("COORDINADOR".equalsIgnoreCase(perfil)) {

            if (usuario.getCarreraId() == null
                    || !usuario.getCarreraId().equals(carrera.getId())) {

                JOptionPane.showMessageDialog(
                        this,
                        "Solo puede registrar asignaturas de su carrera."
                );

                return;
            }
        }

        Asignatura asignatura = new Asignatura();

        asignatura.setNombre(nombre);
        asignatura.setNivel(nivel);
        asignatura.setCarreraId(carrera.getId());

        
        if (modoEdicion) {

            if (idAsignaturaSeleccionada == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione una asignatura para modificar."
                );

                return;
            }

            Asignatura asignaturaOriginal =
                    AsignaturaDAO.buscarPorId(idAsignaturaSeleccionada);

            if (asignaturaOriginal == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "La asignatura que desea modificar ya no existe."
                );

                return;
            }

            if ("COORDINADOR".equalsIgnoreCase(perfil)) {

                if (usuario.getCarreraId() == null
                        || !usuario.getCarreraId().equals(
                                asignaturaOriginal.getCarreraId())) {

                    JOptionPane.showMessageDialog(
                            this,
                            "No puede modificar asignaturas de otra carrera."
                    );

                    return;
                }
            }
        }
        
        
        
        
        
        
        
        
        boolean resultado;

            if (modoEdicion) {

                asignatura.setId(idAsignaturaSeleccionada);

                resultado = AsignaturaDAO.actualizar(asignatura);

            } else {

                resultado = AsignaturaDAO.insertar(asignatura);

            }

        if (resultado) {

            JOptionPane.showMessageDialog(
                    this,
                    modoEdicion
                            ? "Asignatura actualizada correctamente."
                            : "Asignatura guardada correctamente."
            );

            
            limpiarCampos();
            cargarAsignaturas();
            estadoInicial();
         

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar la asignatura."
            );
        }
    }
    
    private boolean validarCampos() {

        String nombre = jtxtNombre.getText().trim();
        if (nombre.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese el nombre de la asignatura."
            );

            jtxtNombre.requestFocusInWindow();

            return false;
        }

        int nivel = (Integer) jspnNivel.getValue();

        if (nivel < 1 || nivel > 10) {

            JOptionPane.showMessageDialog(
                    this,
                    "El nivel debe estar entre 1 y 10."
            );

            jspnNivel.requestFocusInWindow();

            return false;
        }

        Carrera carrera =
                (Carrera) jcbxCarrera.getSelectedItem();

        if (carrera == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una carrera."
            );

            jcbxCarrera.requestFocusInWindow();

            return false;
        }

        return true;
    }
    
    private void estadoInicial() {

        jtxtNombre.setEnabled(false);
        jspnNivel.setEnabled(false);
        jcbxCarrera.setEnabled(false);

        jbtnNuevo.setEnabled(true);
        jbtnGuardar.setEnabled(false);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(false);

        idAsignaturaSeleccionada = 0;
        modoEdicion = false;
    }
    
    private void seleccionarAsignatura() {
        
        if (modoEdicion || jbtnGuardar.isEnabled()) {
            return;
        }

        int fila = jtblAsignaturas.getSelectedRow();

        if (fila == -1) {
            return;
        }

        int filaModelo = jtblAsignaturas.convertRowIndexToModel(fila);

        int id = Integer.parseInt(
                modeloTabla.getValueAt(filaModelo, 0).toString()
        );

        Asignatura asignatura = AsignaturaDAO.buscarPorId(id);

        if (asignatura == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró la asignatura seleccionada."
            );

            return;
        }

        if (usuario == null) {
            return;
        }

        String perfil = usuario.getPerfil();

        boolean esAdmin =
                "ADMIN".equalsIgnoreCase(perfil);

        boolean esCoordinador =
                "COORDINADOR".equalsIgnoreCase(perfil);

        boolean mismaCarrera =
                usuario.getCarreraId() != null
                && usuario.getCarreraId().equals(asignatura.getCarreraId());

        if (!esAdmin && !(esCoordinador && mismaCarrera)) {

            JOptionPane.showMessageDialog(
                    this,
                    "No tiene permisos para gestionar esta asignatura."
            );

            return;
        }

        idAsignaturaSeleccionada = asignatura.getId();

        jtxtNombre.setText(asignatura.getNombre());

        jspnNivel.setValue(asignatura.getNivel());

        for (int i = 0; i < jcbxCarrera.getItemCount(); i++) {

            Carrera carrera = jcbxCarrera.getItemAt(i);

            if (carrera.getId() == asignatura.getCarreraId()) {

                jcbxCarrera.setSelectedIndex(i);

                break;
            }
        }

        jtxtNombre.setEnabled(false);
        jspnNivel.setEnabled(false);
        jcbxCarrera.setEnabled(false);

        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(false);
        jbtnEditar.setEnabled(true);
        jbtnEliminar.setEnabled(true);
        jbtnCancelar.setEnabled(true);
    }
    
    private void cancelar() {

        limpiarCampos();

        estadoInicial();
    }
    
    private void editar() {

        if (idAsignaturaSeleccionada == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una asignatura para editar."
            );

            return;
        }

        if (usuario == null) {
            return;
        }

        String perfil = usuario.getPerfil();

        if (!"ADMIN".equalsIgnoreCase(perfil)
                && !"COORDINADOR".equalsIgnoreCase(perfil)) {

            JOptionPane.showMessageDialog(
                    this,
                    "No tiene permisos para editar asignaturas."
            );

            return;
        }

        Asignatura asignatura =
                AsignaturaDAO.buscarPorId(idAsignaturaSeleccionada);

        if (asignatura == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "La asignatura seleccionada ya no existe."
            );

            return;
        }

        if ("COORDINADOR".equalsIgnoreCase(perfil)
                && (usuario.getCarreraId() == null
                || !usuario.getCarreraId().equals(asignatura.getCarreraId()))) {

            JOptionPane.showMessageDialog(
                    this,
                    "Solo puede editar asignaturas de su carrera."
            );

            return;
        }

        modoEdicion = true;

        jtxtNombre.setEnabled(true);
        jspnNivel.setEnabled(true);

        boolean esCoordinador =
                "COORDINADOR".equalsIgnoreCase(perfil);

        jcbxCarrera.setEnabled(!esCoordinador);

        jbtnGuardar.setEnabled(true);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(true);

        jtxtNombre.requestFocusInWindow();
    }
    
    private void eliminar() {

        if (idAsignaturaSeleccionada == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una asignatura para eliminar."
            );

            return;
        }

        if (usuario == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe iniciar sesión para eliminar asignaturas."
            );

            return;
        }

        String perfil = usuario.getPerfil();
        if (!"ADMIN".equalsIgnoreCase(perfil)
                && !"COORDINADOR".equalsIgnoreCase(perfil)) {

            JOptionPane.showMessageDialog(
                    this,
                    "No tiene permisos para eliminar asignaturas."
            );

            return;
        }

        Asignatura asignatura =
                AsignaturaDAO.buscarPorId(idAsignaturaSeleccionada);

        if (asignatura == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "La asignatura seleccionada no existe."
            );

            return;
        }

        if ("COORDINADOR".equalsIgnoreCase(perfil)) {

            if (usuario.getCarreraId() == null
                    || !usuario.getCarreraId().equals(asignatura.getCarreraId())) {

                JOptionPane.showMessageDialog(
                        this,
                        "Solo puede eliminar asignaturas de su carrera."
                );

                return;
            }
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar la asignatura "
                        + asignatura.getNombre() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        boolean resultado =
                AsignaturaDAO.eliminar(idAsignaturaSeleccionada);

        if (resultado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Asignatura eliminada correctamente."
            );

            limpiarCampos();
            cargarAsignaturas();
            estadoInicial();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo eliminar la asignatura."
            );
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jtxtNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jspnNivel = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jcbxCarrera = new javax.swing.JComboBox<>();
        jbtnNuevo = new javax.swing.JButton();
        jbtnGuardar = new javax.swing.JButton();
        jbtnEditar = new javax.swing.JButton();
        jbtnEliminar = new javax.swing.JButton();
        jbtnCancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblAsignaturas = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Nombre:");

        jtxtNombre.addActionListener(this::jtxtNombreActionPerformed);

        jLabel2.setText("Nivel:");

        jspnNivel.setModel(new javax.swing.SpinnerNumberModel(1, 1, 10, 1));

        jLabel3.setText("Carrera:");

        jbtnNuevo.setText("Nuevo");
        jbtnNuevo.addActionListener(this::jbtnNuevoActionPerformed);

        jbtnGuardar.setText("Guardar");
        jbtnGuardar.addActionListener(this::jbtnGuardarActionPerformed);

        jbtnEditar.setText("Editar");
        jbtnEditar.addActionListener(this::jbtnEditarActionPerformed);

        jbtnEliminar.setText("Eliminar");
        jbtnEliminar.addActionListener(this::jbtnEliminarActionPerformed);

        jbtnCancelar.setText("Cancelar");
        jbtnCancelar.addActionListener(this::jbtnCancelarActionPerformed);

        jtblAsignaturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jtblAsignaturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtblAsignaturasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtblAsignaturas);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Asignaturas");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(55, 55, 55)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jcbxCarrera, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jspnNivel)
                    .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 186, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnCancelar)
                    .addComponent(jbtnEliminar)
                    .addComponent(jbtnGuardar)
                    .addComponent(jbtnNuevo)
                    .addComponent(jbtnEditar))
                .addGap(17, 17, 17))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(226, 226, 226)
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnNuevo))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jspnNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnGuardar))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jcbxCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnEditar))
                .addGap(26, 26, 26)
                .addComponent(jbtnEliminar)
                .addGap(26, 26, 26)
                .addComponent(jbtnCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtNombreActionPerformed

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        // TODO add your handling code here:
         guardar();
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jbtnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditarActionPerformed
        // TODO add your handling code here:
        editar();
    }//GEN-LAST:event_jbtnEditarActionPerformed

    private void jbtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEliminarActionPerformed
        // TODO add your handling code here:
        eliminar();
        
    }//GEN-LAST:event_jbtnEliminarActionPerformed

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        // TODO add your handling code here:
        nuevo();
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jtblAsignaturasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtblAsignaturasMouseClicked
        // TODO add your handling code here:
        seleccionarAsignatura();
    }//GEN-LAST:event_jtblAsignaturasMouseClicked

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        // TODO add your handling code here:
        cancelar();
    }//GEN-LAST:event_jbtnCancelarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmAsignaturas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JButton jbtnEditar;
    private javax.swing.JButton jbtnEliminar;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JButton jbtnNuevo;
    private javax.swing.JComboBox<Carrera> jcbxCarrera;
    private javax.swing.JSpinner jspnNivel;
    private javax.swing.JTable jtblAsignaturas;
    private javax.swing.JTextField jtxtNombre;
    // End of variables declaration//GEN-END:variables
}
