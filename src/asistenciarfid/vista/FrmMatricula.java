/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package asistenciarfid.vista;

import asistenciarfid.modelo.Usuario;
import asistenciarfid.controlador.MatriculaControlador;
import asistenciarfid.dao.EstudianteDAO;
import asistenciarfid.dao.MatriculaDAO;
import asistenciarfid.dao.PeriodoDAO;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.Grupo;
import asistenciarfid.modelo.Matricula;
import asistenciarfid.modelo.Periodo;
import asistenciarfid.util.Parametros;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USER
 */
public class FrmMatricula extends javax.swing.JInternalFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmMatricula.class.getName());
    private Usuario usuario;
    private Estudiante estudiante;
    private boolean abierta = false;
    private List<Grupo> disponibles = new ArrayList<>();
    private List<Matricula> matriculadas = new ArrayList<>();    
    /**
     * Creates new form FrmMatricula
     */
    public FrmMatricula() {
        initComponents();
    }
    
    public FrmMatricula(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        cargarDatos();
    }

    // Busca al estudiante, revisa si la matrícula está abierta y llena todo
    private void cargarDatos() {
        estudiante = EstudianteDAO.buscarPorUsuarioId(usuario.getId());
        if (estudiante == null) {
            jlblAviso.setText("Tu usuario no está asociado a un estudiante.");
            abierta = false;
            actualizarBotones();
            return;
        }
        jlblNombre.setText(estudiante.getNombre() + " " + estudiante.getApellido());
        jlblNivel.setText("Nivel: " + estudiante.getNivel());

        Periodo periodo = PeriodoDAO.obtenerActivo();
        if (periodo == null) {
            jlblAviso.setText("No hay un periodo activo.");
            abierta = false;
        } else if (!periodo.isMatriculaAbierta()) {
            jlblAviso.setText("La matrícula está cerrada. Solo puede consultar.");
            abierta = false;
        } else {
            jlblAviso.setText("La matrícula está abierta.");
            abierta = true;
        }
        actualizarBotones();
        cargarDisponibles();
        cargarMatriculadas();
        actualizarContador();
    }

    // Los botones solo funcionan con la matrícula abierta
    private void actualizarBotones() {
        jbtnMatricular.setEnabled(abierta);
        jbtnAnular.setEnabled(abierta);
    }

    // Tabla 1: grupos que el estudiante puede escoger ahora (lo decide MatriculaControlador)
    private void cargarDisponibles() {
        if (estudiante == null) {
            return;
        }
        disponibles = MatriculaControlador.gruposDisponibles(estudiante);
        String titulos[] = {"Asignatura", "Nivel", "Paralelo"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (Grupo g : disponibles) {
            modelo.addRow(new Object[]{g.getAsignaturaNombre(), g.getAsignaturaNivel(), g.getParalelo()});
        }
        jtblDisponibles.setModel(modelo);
    }

    // Tabla 2: las materias que ya tiene matriculadas en el periodo activo
    private void cargarMatriculadas() {
        if (estudiante == null) {
            return;
        }
        matriculadas = MatriculaDAO.listarPorEstudiante(estudiante.getId());
        String titulos[] = {"Asignatura", "Paralelo", "Nota"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (Matricula m : matriculadas) {
            String nota = m.getNota() == null ? "Sin nota" : String.valueOf(m.getNota());
            modelo.addRow(new Object[]{m.getAsignaturaNombre(), m.getParalelo(), nota});
        }
        jtblMatriculadas.setModel(modelo);
    }

    // "Matriculadas: X de 5" y la barra de progreso
    private void actualizarContador() {
        if (estudiante == null) {
            return;
        }
        int total = MatriculaDAO.contarEnPeriodoActivo(estudiante.getId());
        jlblContador.setText("Matriculadas: " + total + " de " + Parametros.MAX_MATERIAS);
        jpgbMaterias.setMaximum(Parametros.MAX_MATERIAS);
        jpgbMaterias.setValue(total);
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    // Matricula en el grupo elegido. La regla la revisa el controlador, esta ventana solo muestra el resultado
    private void accionMatricular() {
        int fila = jtblDisponibles.getSelectedRow();
        if (fila < 0) {
            mensaje("Seleccione un grupo de la tabla de disponibles");
            return;
        }
        Grupo g = disponibles.get(fila);
        String error = MatriculaControlador.matricular(estudiante.getId(), g.getId());
        if (error != null) {
            mensaje(error);
            return;
        }
        mensaje("Se matriculó en " + g.getAsignaturaNombre() + " (paralelo " + g.getParalelo() + ")");
        cargarDisponibles();
        cargarMatriculadas();
        actualizarContador();
    }

    private void accionAnular() {
        int fila = jtblMatriculadas.getSelectedRow();
        if (fila < 0) {
            mensaje("Seleccione una materia de la tabla de matriculadas");
            return;
        }
        Matricula m = matriculadas.get(fila);
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Anular la matrícula en " + m.getAsignaturaNombre() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            if (MatriculaDAO.anular(estudiante.getId(), m.getGrupoId())) {
                mensaje("Matrícula anulada");
                cargarDisponibles();
                cargarMatriculadas();
                actualizarContador();
            } else {
                mensaje("No se pudo anular la matrícula");
            }
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

        jlblMatriculas = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jlblNombre = new javax.swing.JLabel();
        jlblAviso = new javax.swing.JLabel();
        jlblContador = new javax.swing.JLabel();
        jlblNivel = new javax.swing.JLabel();
        jpgbMaterias = new javax.swing.JProgressBar();
        jlblMatriculas1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblDisponibles = new javax.swing.JTable();
        jlblMatriculas2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblMatriculadas = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jbtnMatricular = new javax.swing.JButton();
        jbtnAnular = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(674, 671));

        jlblMatriculas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jlblMatriculas.setText("Matriculas");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jpgbMaterias.setMaximum(5);
        jpgbMaterias.setStringPainted(true);

        jlblMatriculas1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jlblMatriculas1.setText("Materias disponibles");

        jtblDisponibles.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jtblDisponibles);

        jlblMatriculas2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jlblMatriculas2.setText("Mis materias matriculadas");

        jtblMatriculadas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jtblMatriculadas);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlblNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblAviso, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblContador, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jpgbMaterias, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblMatriculas1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblMatriculas2, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 14, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(jlblAviso, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jlblContador, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpgbMaterias, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jlblMatriculas1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlblMatriculas2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jbtnMatricular.setText("Matricular");
        jbtnMatricular.addActionListener(this::jbtnMatricularActionPerformed);

        jbtnAnular.setText("Anular");
        jbtnAnular.addActionListener(this::jbtnAnularActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbtnAnular, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnMatricular, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jbtnMatricular, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jbtnAnular, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlblMatriculas, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(319, 319, 319))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlblMatriculas, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnMatricularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnMatricularActionPerformed
        accionMatricular();
    }//GEN-LAST:event_jbtnMatricularActionPerformed

    private void jbtnAnularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAnularActionPerformed
        accionAnular();
    }//GEN-LAST:event_jbtnAnularActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new FrmMatricula().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnAnular;
    private javax.swing.JButton jbtnMatricular;
    private javax.swing.JLabel jlblAviso;
    private javax.swing.JLabel jlblContador;
    private javax.swing.JLabel jlblMatriculas;
    private javax.swing.JLabel jlblMatriculas1;
    private javax.swing.JLabel jlblMatriculas2;
    private javax.swing.JLabel jlblNivel;
    private javax.swing.JLabel jlblNombre;
    private javax.swing.JProgressBar jpgbMaterias;
    private javax.swing.JTable jtblDisponibles;
    private javax.swing.JTable jtblMatriculadas;
    // End of variables declaration//GEN-END:variables
}
