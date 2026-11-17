/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package asistenciarfid.vista;

import asistenciarfid.dao.GrupoDAO;
import asistenciarfid.dao.MatriculaDAO;
import asistenciarfid.modelo.Grupo;
import asistenciarfid.modelo.Matricula;
import asistenciarfid.modelo.Perfil;
import asistenciarfid.modelo.Usuario;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USER
 */
public class FrmInscritos extends javax.swing.JInternalFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmInscritos.class.getName());

    /**
     * Creates new form FrmInscritos
     */
    public FrmInscritos() {
        initComponents();
    }
    
        private Usuario usuario;
    private List<Grupo> listaGrupos = new ArrayList<>();   // grupos que este usuario puede ver
    private boolean cargando = false;                      // true mientras se llenan los combos por código

    public FrmInscritos(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        if (!tienePermiso()) {
            JOptionPane.showMessageDialog(this, "Su perfil no puede consultar los inscritos.");
            jcbxAsignatura.setEnabled(false);
            jcbxParalelo.setEnabled(false);
            cargarTabla();
            return;
        }
        cargarGrupos();
        cargarAsignaturas();
        jcbxAsignatura.addActionListener(e -> asignaturaCambio());
        jcbxParalelo.addActionListener(e -> {
            if (!cargando) {
                cargarTabla();
            }
        });
        cargarTabla();
    }
    
        // Solo ADMIN, COORDINADOR y DOCENTE consultan inscritos
    private boolean tienePermiso() {
        return usuario != null && (Perfil.ADMIN.equals(usuario.getPerfil())
                || Perfil.COORDINADOR.equals(usuario.getPerfil())
                || Perfil.DOCENTE.equals(usuario.getPerfil()));
    }

    // DOCENTE: solo sus grupos. COORDINADOR: los de su carrera. ADMIN: todos.
    private void cargarGrupos() {
        if (Perfil.DOCENTE.equals(usuario.getPerfil())) {
            listaGrupos = GrupoDAO.listar(null, usuario.getId());
        } else if (Perfil.COORDINADOR.equals(usuario.getPerfil())) {
            listaGrupos = GrupoDAO.listar(usuario.getCarreraId(), null);
        } else {
            listaGrupos = GrupoDAO.listar(null, null);
        }
    }

    // Llena el combo de asignaturas con los nombres que hay en los grupos visibles
    private void cargarAsignaturas() {
        List<String> nombres = new ArrayList<>();
        for (Grupo g : listaGrupos) {
            if (!nombres.contains(g.getAsignaturaNombre())) {
                nombres.add(g.getAsignaturaNombre());
            }
        }
        Collections.sort(nombres);
        cargando = true;
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Seleccione");
        for (String n : nombres) {
            modelo.addElement(n);
        }
        jcbxAsignatura.setModel(modelo);
        cargando = false;
        cargarParalelos();
    }

    // Cuando se elige otra asignatura se rehacen los paralelos y la tabla
    private void asignaturaCambio() {
        if (cargando) {
            return;
        }
        cargarParalelos();
        cargarTabla();
    }

    // Llena el combo de paralelos solo con los de la asignatura elegida
    private void cargarParalelos() {
        List<String> paralelos = new ArrayList<>();
        boolean hayAsignatura = jcbxAsignatura.getSelectedIndex() > 0;
        if (hayAsignatura) {
            String asignatura = String.valueOf(jcbxAsignatura.getSelectedItem());
            for (Grupo g : listaGrupos) {
                if (g.getAsignaturaNombre().equals(asignatura) && !paralelos.contains(g.getParalelo())) {
                    paralelos.add(g.getParalelo());
                }
            }
            Collections.sort(paralelos);
        }
        cargando = true;
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Seleccione");
        for (String p : paralelos) {
            modelo.addElement(p);
        }
        jcbxParalelo.setModel(modelo);
        jcbxParalelo.setEnabled(hayAsignatura);
        cargando = false;
    }

    // Muestra los estudiantes inscritos en la asignatura y el paralelo elegidos
    private void cargarTabla() {
        String titulos[] = {"Nro", "Estudiante", "Asignatura", "Paralelo"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        int total = 0;
        if (jcbxAsignatura.getSelectedIndex() > 0 && jcbxParalelo.getSelectedIndex() > 0) {
            String asignatura = String.valueOf(jcbxAsignatura.getSelectedItem());
            String paralelo = String.valueOf(jcbxParalelo.getSelectedItem());
            for (Grupo g : listaGrupos) {
                if (g.getAsignaturaNombre().equals(asignatura) && g.getParalelo().equals(paralelo)) {
                    List<Matricula> inscritos = MatriculaDAO.listarPorGrupo(g.getId());
                    for (Matricula m : inscritos) {
                        total++;
                        modelo.addRow(new Object[]{total, m.getEstudianteNombre(),
                            g.getAsignaturaNombre(), g.getParalelo()});
                    }
                }
            }
        }
        jtblInscritos.setModel(modelo);
        jlblTotal.setText("Inscritos: " + total);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jlblAsignatura = new javax.swing.JLabel();
        jcbxAsignatura = new javax.swing.JComboBox<>();
        jlblParalelo = new javax.swing.JLabel();
        jcbxParalelo = new javax.swing.JComboBox<>();
        jscpInscritos = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblInscritos = new javax.swing.JTable();
        jlblTotal = new javax.swing.JLabel();

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("ESTUDIANTES INSCRITOS");

        jlblAsignatura.setText("Asignatura");

        jcbxAsignatura.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbxAsignatura.addActionListener(this::jcbxAsignaturaActionPerformed);

        jlblParalelo.setText("Paralelo");

        jcbxParalelo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jtblInscritos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jtblInscritos);

        jscpInscritos.setViewportView(jScrollPane2);

        jlblTotal.setText("Inscritos: 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jscpInscritos, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jlblParalelo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlblAsignatura, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jcbxAsignatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcbxParalelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(183, 183, 183))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblAsignatura)
                    .addComponent(jcbxAsignatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblParalelo)
                    .addComponent(jcbxParalelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jscpInscritos, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jlblTotal)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jcbxAsignaturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbxAsignaturaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbxAsignaturaActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new FrmInscritos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JComboBox<String> jcbxAsignatura;
    private javax.swing.JComboBox<String> jcbxParalelo;
    private javax.swing.JLabel jlblAsignatura;
    private javax.swing.JLabel jlblParalelo;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JScrollPane jscpInscritos;
    private javax.swing.JTable jtblInscritos;
    // End of variables declaration//GEN-END:variables
}
