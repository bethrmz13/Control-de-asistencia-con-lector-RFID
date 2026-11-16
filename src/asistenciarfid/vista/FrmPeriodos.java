/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package asistenciarfid.vista;

import asistenciarfid.modelo.Usuario;
import asistenciarfid.controlador.CierrePeriodoControlador;
import asistenciarfid.dao.PeriodoDAO;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.Matricula;
import asistenciarfid.modelo.Periodo;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USER
 */
public class FrmPeriodos extends javax.swing.JInternalFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmPeriodos.class.getName());

    /**
     * Creates new form FrmPeriodos
     */
    public FrmPeriodos() {
        initComponents();
    }
    
    private Usuario usuario;

        private List<Periodo> periodos;
    private Periodo seleccionado;
    private String modo = "";

    public FrmPeriodos(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jtblPeriodos.setDefaultEditor(Object.class, null);
        cargarPeriodos();
        seleccionarPeriodo();
        informacionInicio();
    }
    
        public void cargarPeriodos() {
        String titulos[] = {"Nombre", "Inicio", "Fin", "Activo", "Matrícula"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos);
        periodos = PeriodoDAO.listar();
        for (Periodo p : periodos) {
            modelo.addRow(new Object[]{p.getNombre(), p.getFechaInicio(), p.getFechaFin(),
                p.isActivo() ? "Sí" : "No", p.isMatriculaAbierta() ? "Abierta" : "Cerrada"});
        }
        jtblPeriodos.setModel(modelo);

        Periodo activo = PeriodoDAO.obtenerActivo();
        if (activo == null) {
            jlblActivo.setText("No hay un periodo activo");
        } else {
            jlblActivo.setText("Periodo activo: " + activo.getNombre()
                    + " (matrícula " + (activo.isMatriculaAbierta() ? "abierta" : "cerrada") + ")");
        }
    }

    // Al seleccionar una fila se llenan las cajas, como en clase
    public void seleccionarPeriodo() {
        jtblPeriodos.getSelectionModel().addListSelectionListener(e -> {
            int fila = jtblPeriodos.getSelectedRow();
            if (fila != -1 && !e.getValueIsAdjusting()) {
                seleccionado = periodos.get(fila);
                jtxtNombre.setText(seleccionado.getNombre());
                jtxtInicio.setText(String.valueOf(seleccionado.getFechaInicio()));
                jtxtFin.setText(String.valueOf(seleccionado.getFechaFin()));
                jtbtnMatricula.setSelected(seleccionado.isMatriculaAbierta());
                informacionSeleccion();
            }
        });
    }

    private void habilitarCampos(boolean estado) {
        jtxtNombre.setEnabled(estado);
        jtxtInicio.setEnabled(estado);
        jtxtFin.setEnabled(estado);
    }

    private void informacionInicio() {
        modo = "";
        seleccionado = null;
        jtblPeriodos.clearSelection();
        jtxtNombre.setText("");
        jtxtInicio.setText("");
        jtxtFin.setText("");
        jtbtnMatricula.setSelected(false);
        habilitarCampos(false);
        jbtnNuevo.setEnabled(true);
        jbtnGuardar.setEnabled(false);
        jbtnEditar.setEnabled(false);
        jbtnCancelar.setEnabled(false);
        jbtnActivar.setEnabled(false);
        jtbtnMatricula.setEnabled(false);
        jbtnCerrarPeriodo.setEnabled(false);
    }
    

    private void informacionNuevo() {
        modo = "NUEVO";
        seleccionado = null;
        jtblPeriodos.clearSelection();
        jtxtNombre.setText("");
        jtxtInicio.setText("");
        jtxtFin.setText("");
        habilitarCampos(true);
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(true);
        jbtnEditar.setEnabled(false);
        jbtnCancelar.setEnabled(true);
        jbtnActivar.setEnabled(false);
        jtbtnMatricula.setEnabled(false);
        jbtnCerrarPeriodo.setEnabled(false);
        jtxtNombre.requestFocus();
    }
    
        private void informacionEditar() {
        modo = "EDITAR";
        habilitarCampos(true);
        jbtnNuevo.setEnabled(false);
        jbtnEditar.setEnabled(false);
        jbtnGuardar.setEnabled(true);
        jtxtNombre.requestFocus();
    }

    // Con un periodo seleccionado: solo el periodo activo puede abrir la matrícula o cerrarse
    private void informacionSeleccion() {
        modo = "";
        habilitarCampos(false);
        jbtnNuevo.setEnabled(true);
        jbtnGuardar.setEnabled(false);
        jbtnEditar.setEnabled(true);
        jbtnCancelar.setEnabled(true);
        jbtnActivar.setEnabled(!seleccionado.isActivo());
        jtbtnMatricula.setEnabled(seleccionado.isActivo());
        jbtnCerrarPeriodo.setEnabled(seleccionado.isActivo());
    }
    
        // Lee una fecha de una caja. Si el formato está mal, avisa y devuelve null.
    private java.sql.Date leerFecha(javax.swing.JTextField caja, String nombreCampo, String ejemplo) {
        try {
            return java.sql.Date.valueOf(caja.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, nombreCampo + " inválida. Use el formato " + ejemplo);
            caja.requestFocus();
            return null;
        }
    }

    private boolean validarCampos() {
        if (jtxtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el nombre del periodo");
            jtxtNombre.requestFocus();
            return false;
        }
        java.sql.Date inicio = leerFecha(jtxtInicio, "La fecha de inicio", "2026-09-28");
        if (inicio == null) {
            return false;
        }
        java.sql.Date fin = leerFecha(jtxtFin, "La fecha de fin", "2027-02-26");
        if (fin == null) {
            return false;
        }
        if (!inicio.before(fin)) {
            JOptionPane.showMessageDialog(null, "La fecha de inicio debe ser anterior a la de fin");
            jtxtInicio.requestFocus();
            return false;
        }
        return true;
    }

    // Arma el Periodo con lo que hay en las cajas (se llama solo después de validarCampos)
    private Periodo armarPeriodo() {
        Periodo p = new Periodo();
        p.setNombre(jtxtNombre.getText().trim());
        p.setFechaInicio(java.sql.Date.valueOf(jtxtInicio.getText().trim()));
        p.setFechaFin(java.sql.Date.valueOf(jtxtFin.getText().trim()));
        if (modo.equals("EDITAR")) {
            p.setId(seleccionado.getId());
            p.setActivo(seleccionado.isActivo());
            p.setMatriculaAbierta(seleccionado.isMatriculaAbierta());
        } else {
            p.setActivo(false);
            p.setMatriculaAbierta(false);
        }
        return p;
    }

    public void guardar() {
        if (!validarCampos()) {
            return;
        }
        Periodo p = armarPeriodo();

        boolean ok;
        if (modo.equals("NUEVO")) {
            ok = PeriodoDAO.insertar(p);
        } else {
            ok = PeriodoDAO.actualizar(p);
        }

        if (ok) {
            JOptionPane.showMessageDialog(null, "Periodo guardado correctamente");
            cargarPeriodos();
            informacionInicio();
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo guardar el periodo");
        }
    }
    
        private boolean confirmar(String mensaje, String titulo) {
        int op = JOptionPane.showConfirmDialog(null, mensaje, titulo, JOptionPane.YES_NO_OPTION);
        return op == JOptionPane.YES_OPTION;
    }

    public void activar() {
        if (!confirmar("¿Activar el periodo " + seleccionado.getNombre()
                + "? El periodo activo actual quedará inactivo.", "Activar periodo")) {
            return;
        }
        if (PeriodoDAO.activar(seleccionado.getId())) {
            JOptionPane.showMessageDialog(null, "Periodo activado");
            cargarPeriodos();
            informacionInicio();
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo activar el periodo");
        }
    }

    public void cambiarMatricula() {
        boolean abrir = jtbtnMatricula.isSelected();
        if (PeriodoDAO.cambiarMatricula(seleccionado.getId(), abrir)) {
            JOptionPane.showMessageDialog(null, abrir ? "Matrícula abierta" : "Matrícula cerrada");
            cargarPeriodos();
            informacionInicio();
        } else {
            jtbtnMatricula.setSelected(!abrir);
            JOptionPane.showMessageDialog(null, "No se pudo cambiar la matrícula");
        }
    }

    // Muestra la lista de notas que faltan. Devuelve true si falta alguna (entonces no se cierra)
    private boolean faltanNotas() {
        List<Matricula> faltantes = CierrePeriodoControlador.notasFaltantes();
        if (faltantes.isEmpty()) {
            return false;
        }
        String lista = "";
        for (Matricula m : faltantes) {
            lista += "- " + m.getEstudianteNombre() + " (" + m.getAsignaturaNombre()
                    + " " + m.getParalelo() + ")\n";
        }
        JOptionPane.showMessageDialog(null, "No se puede cerrar: faltan las notas de:\n" + lista);
        return true;
    }

    private void mostrarEstudiantesPorRevisar(List<Estudiante> porRevisar) {
        String mensaje = "Periodo cerrado.";
        if (!porRevisar.isEmpty()) {
            mensaje += "\nEstudiantes por revisar (reprobaron alguna materia):\n";
            for (Estudiante e : porRevisar) {
                mensaje += "- " + e.getNombre() + " " + e.getApellido() + "\n";
            }
        }
        JOptionPane.showMessageDialog(null, mensaje);
    }

    public void cerrarPeriodo() {
        if (faltanNotas()) {
            return;
        }
        if (!confirmar("¿Cerrar el periodo? Las notas pasarán a aprobada o reprobada y los estudiantes "
                + "que aprobaron todo subirán de nivel.", "Cerrar periodo")) {
            return;
        }
        List<Estudiante> porRevisar = CierrePeriodoControlador.cerrarPeriodo();
        if (porRevisar == null) {
            JOptionPane.showMessageDialog(null, "No se pudo cerrar el periodo");
            return;
        }
        mostrarEstudiantesPorRevisar(porRevisar);
        cargarPeriodos();
        informacionInicio();
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlblActivo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblPeriodos = new javax.swing.JTable();
        jtxtNombre = new javax.swing.JTextField();
        jtxtInicio = new javax.swing.JTextField();
        jtxtFin = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jbtnNuevo = new javax.swing.JButton();
        jbtnGuardar = new javax.swing.JButton();
        jbtnEditar = new javax.swing.JButton();
        jbtnCancelar = new javax.swing.JButton();
        jbtnActivar = new javax.swing.JButton();
        jtbtnMatricula = new javax.swing.JToggleButton();
        jbtnCerrarPeriodo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jlblActivo.setText("jLabel1");

        jtblPeriodos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jtblPeriodos);

        jLabel1.setText("Nombre");

        jLabel2.setText("Inicio (aaaa-mm-dd)");

        jLabel3.setText("Fin (aaaa-mm-dd)");

        jbtnNuevo.setText("Nuevo");
        jbtnNuevo.addActionListener(this::jbtnNuevoActionPerformed);

        jbtnGuardar.setText("Guardar");
        jbtnGuardar.addActionListener(this::jbtnGuardarActionPerformed);

        jbtnEditar.setText("Editar");
        jbtnEditar.addActionListener(this::jbtnEditarActionPerformed);

        jbtnCancelar.setText("Cancelar");
        jbtnCancelar.addActionListener(this::jbtnCancelarActionPerformed);

        jbtnActivar.setText("Activar Periodo");
        jbtnActivar.addActionListener(this::jbtnActivarActionPerformed);

        jtbtnMatricula.setText("Matricula Abierta");
        jtbtnMatricula.addActionListener(this::jtbtnMatriculaActionPerformed);

        jbtnCerrarPeriodo.setText("Cerrar Periodo");
        jbtnCerrarPeriodo.addActionListener(this::jbtnCerrarPeriodoActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtNombre))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtInicio, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)))
                                .addGap(43, 43, 43)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtFin, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jbtnNuevo)
                                        .addGap(60, 60, 60))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jbtnCerrarPeriodo)
                                        .addGap(25, 25, 25)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jbtnActivar)
                                        .addGap(18, 18, 18)
                                        .addComponent(jtbtnMatricula))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jbtnGuardar)
                                        .addGap(46, 46, 46)
                                        .addComponent(jbtnEditar)
                                        .addGap(36, 36, 36)
                                        .addComponent(jbtnCancelar)))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblActivo)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(36, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jlblActivo)
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtxtInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jtxtFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCancelar)
                    .addComponent(jbtnGuardar)
                    .addComponent(jbtnNuevo)
                    .addComponent(jbtnEditar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCerrarPeriodo)
                    .addComponent(jbtnActivar)
                    .addComponent(jtbtnMatricula))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        informacionNuevo();
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        guardar();
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jbtnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditarActionPerformed
        informacionEditar();
    }//GEN-LAST:event_jbtnEditarActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        informacionInicio();
    }//GEN-LAST:event_jbtnCancelarActionPerformed

    private void jbtnActivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnActivarActionPerformed
        activar();
    }//GEN-LAST:event_jbtnActivarActionPerformed

    private void jtbtnMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbtnMatriculaActionPerformed
        cambiarMatricula();
    }//GEN-LAST:event_jtbtnMatriculaActionPerformed

    private void jbtnCerrarPeriodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCerrarPeriodoActionPerformed
        cerrarPeriodo();
    }//GEN-LAST:event_jbtnCerrarPeriodoActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new FrmPeriodos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnActivar;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JButton jbtnCerrarPeriodo;
    private javax.swing.JButton jbtnEditar;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JButton jbtnNuevo;
    private javax.swing.JLabel jlblActivo;
    private javax.swing.JTable jtblPeriodos;
    private javax.swing.JToggleButton jtbtnMatricula;
    private javax.swing.JTextField jtxtFin;
    private javax.swing.JTextField jtxtInicio;
    private javax.swing.JTextField jtxtNombre;
    // End of variables declaration//GEN-END:variables
}
