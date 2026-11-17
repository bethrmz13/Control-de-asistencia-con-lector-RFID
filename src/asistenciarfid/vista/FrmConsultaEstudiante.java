/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package asistenciarfid.vista;
import asistenciarfid.modelo.Usuario;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.Periodo;
import asistenciarfid.modelo.Matricula;
import asistenciarfid.modelo.RegistroAcademico;

import asistenciarfid.dao.EstudianteDAO;
import asistenciarfid.dao.PeriodoDAO;
import asistenciarfid.dao.MatriculaDAO;
import asistenciarfid.dao.AsistenciaDAO;
import asistenciarfid.dao.RegistroAcademicoDAO;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USER
 */
public class FrmConsultaEstudiante extends javax.swing.JInternalFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmConsultaEstudiante.class.getName());

    /**
     * Creates new form FrmConsultaEstudiante
     */
    public FrmConsultaEstudiante() {
        initComponents();
    }
    
    private Usuario usuario;
    private Estudiante estudiante;
    private DefaultTableModel modeloNotas;
    private DefaultTableModel modeloRecord;

    public FrmConsultaEstudiante(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        configurarTablas();
        actualizarDatos();
    }
    
    private void configurarTablas() {

        modeloNotas = new DefaultTableModel();

        modeloNotas.addColumn("Asignatura");
        modeloNotas.addColumn("Paralelo");
        modeloNotas.addColumn("Asistencias");
        modeloNotas.addColumn("Nota");

        jtblNotas.setModel(modeloNotas);

        jtblNotas.setDefaultEditor(
                Object.class, null
        );

        modeloRecord = new DefaultTableModel();

        modeloRecord.addColumn("Nivel");
        modeloRecord.addColumn("Asignatura");
        modeloRecord.addColumn("Estado");

        jtblRecord.setModel(modeloRecord);

        jtblRecord.setDefaultEditor(
                Object.class, null
        );
    }
    
    
    private boolean cargarEstudiante() {

        if (usuario == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se ha iniciado sesión."
            );

            dispose();
            return false;
        }

        if (!"ESTUDIANTE".equalsIgnoreCase(usuario.getPerfil())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Esta ventana es exclusiva para estudiantes."
            );

            dispose();
            return false;
        }

        estudiante = EstudianteDAO.buscarPorUsuarioId(usuario.getId());

        if (estudiante == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró información del estudiante."
            );

            dispose();
            return false;
        }

        jlblNombre.setText(
                estudiante.getNombre() + " " + estudiante.getApellido()
        );

        jlblNivel.setText(
                String.valueOf(estudiante.getNivel())
        );

        return true;
    }

    private void cargarNotasAsistencias() {

        modeloNotas.setRowCount(0);

        if (estudiante == null) {
            return;
        }

        Periodo periodo = PeriodoDAO.obtenerActivo();

        if (periodo == null) {

            jlblPeriodo.setText("No hay un periodo activo");

            return;
        }

        jlblPeriodo.setText(periodo.getNombre());

        List<Matricula> matriculas =
                MatriculaDAO.listarPorEstudiante(estudiante.getId());

        for (Matricula matricula : matriculas) {

            int asistencias = AsistenciaDAO.contarPresentes(
                    estudiante.getId(),
                    matricula.getGrupoId(),
                    periodo.getFechaInicio(),
                    periodo.getFechaFin()
            );

            String nota;

            if (matricula.getNota() == null) {

                nota = "Sin nota";

            } else {

                nota = String.valueOf(matricula.getNota());
            }

            modeloNotas.addRow(new Object[]{

                matricula.getAsignaturaNombre(),
                matricula.getParalelo(),
                asistencias,
                nota

            });
        }
    }

    
    private void cargarRecordAcademico() {

        modeloRecord.setRowCount(0);

        if (estudiante == null) {
            return;
        }
        List<RegistroAcademico> registros =
                RegistroAcademicoDAO.listarPorEstudiante(
                        estudiante.getId()
                );

        for (RegistroAcademico registro : registros) {

            modeloRecord.addRow(new Object[]{

                registro.getAsignaturaNivel(),
                registro.getAsignaturaNombre(),
                registro.getEstado()

            });
        }
    }
    
    private void actualizarDatos() {

        if (cargarEstudiante()) {

            cargarNotasAsistencias();

            cargarRecordAcademico();
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
        jLabel2 = new javax.swing.JLabel();
        jlblNombre = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jlblNivel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jlblPeriodo = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblNotas = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblRecord = new javax.swing.JTable();
        jbtnActualizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("CONSULTA DEL ESTUDIANTE");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Informacion del estudiante");

        jLabel3.setText("Nombre:");

        jLabel4.setText("Nivel:");

        jLabel5.setText("Periodo:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("\tMIS NOTAS Y ASISTENCIAS DEL PERIODO");

        jtblNotas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Asignatura", "Paralelo", "Asistencia", "Notas"
            }
        ));
        jScrollPane1.setViewportView(jtblNotas);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("\tMI RÉCORD ACADÉMICO");

        jtblRecord.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nivel", "Asignaturas", "Estado"
            }
        ));
        jScrollPane2.setViewportView(jtblRecord);

        jbtnActualizar.setText("Actualizar");
        jbtnActualizar.addActionListener(this::jbtnActualizarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(238, 238, 238))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlblNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlblPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(184, 184, 184)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(216, 216, 216)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(267, 267, 267)
                        .addComponent(jbtnActualizar)))
                .addContainerGap(112, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jlblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jlblNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jlblPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jbtnActualizar)
                .addGap(0, 41, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnActualizarActionPerformed
        // TODO add your handling code here:
        actualizarDatos();
    }//GEN-LAST:event_jbtnActualizarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new FrmConsultaEstudiante().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnActualizar;
    private javax.swing.JLabel jlblNivel;
    private javax.swing.JLabel jlblNombre;
    private javax.swing.JLabel jlblPeriodo;
    private javax.swing.JTable jtblNotas;
    private javax.swing.JTable jtblRecord;
    // End of variables declaration//GEN-END:variables
}
