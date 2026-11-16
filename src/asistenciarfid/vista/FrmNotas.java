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
import asistenciarfid.util.Parametros;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USER
 */
public class FrmNotas extends javax.swing.JInternalFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmNotas.class.getName());

    /**
     * Creates new form FrmNotas
     */
    public FrmNotas() {
        initComponents();
    }
    
    private Usuario usuario;
    private List<Grupo> listaGrupos = new ArrayList<>();          // todos los grupos que este usuario puede ver
    private List<Matricula> listaMatriculas = new ArrayList<>();  // paralela a las filas de la tabla
    private int[] cadena = {0, 1, 2, 3};                          // orden de los filtros: 0 nivel, 1 asignatura, 2 paralelo, 3 docente
    private boolean cargando = false;                             // true mientras se llenan los combos por codigo (ignora sus eventos)

    public FrmNotas(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        cargarGrupos();
        if (usuario != null && Perfil.DOCENTE.equals(usuario.getPerfil())) {
            // El docente filtra por nivel, asignatura y paralelo (solo ve sus grupos)
            jlblDocente.setVisible(false);
            jcbxDocente.setVisible(false);
            cadena = new int[]{0, 1, 2};
        }
        jcbxNivel.addActionListener(e -> filtroCambio(0));
        jcbxAsignatura.addActionListener(e -> filtroCambio(1));
        jcbxParalelo.addActionListener(e -> filtroCambio(2));
        jcbxDocente.addActionListener(e -> filtroCambio(3));
        reconstruir(0);
        cargarTabla();

    }

    // DOCENTE: solo sus grupos. COORDINADOR: los de su carrera. ADMIN: todos.
    private void cargarGrupos() {
        if (usuario != null && Perfil.DOCENTE.equals(usuario.getPerfil())) {
            listaGrupos = GrupoDAO.listar(null, usuario.getId());
        } else if (usuario != null && Perfil.COORDINADOR.equals(usuario.getPerfil())) {
            listaGrupos = GrupoDAO.listar(usuario.getCarreraId(), null);
        } else {
            listaGrupos = GrupoDAO.listar(null, null);
        }
    }

    // ---- Filtros en cadena ----
    // Cada filtro tiene un numero: 0 = nivel, 1 = asignatura, 2 = paralelo, 3 = docente
    // "cadena" dice en que orden se usan (el docente solo usa 1 y 2)

    private JComboBox<String> comboDe(int campo) {
        if (campo == 0) {
            return jcbxNivel;
        }
        if (campo == 1) {
            return jcbxAsignatura;
        }
        if (campo == 2) {
            return jcbxParalelo;
        }
        return jcbxDocente;
    }

    // En que posicion de la cadena esta un filtro
    private int posicionDe(int campo) {
        for (int i = 0; i < cadena.length; i++) {
            if (cadena[i] == campo) {
                return i;
            }
        }
        return -1;
    }

    // El texto que tiene un grupo para ese filtro
    private String textoCampo(Grupo g, int campo) {
        if (campo == 0) {
            return String.valueOf(g.getAsignaturaNivel());
        }
        if (campo == 1) {
            return g.getAsignaturaNombre();
        }
        if (campo == 2) {
            return g.getParalelo();
        }
        String docente = g.getDocenteNombre();
        if (docente == null) {
            docente = "Sin docente";
        }
        return docente;
    }

    // Lo que esta elegido en un filtro; null si esta en "Seleccione"
    private String elegido(int campo) {
        JComboBox<String> combo = comboDe(campo);
        if (combo.getSelectedIndex() <= 0) {
            return null;
        }
        return String.valueOf(combo.getSelectedItem());
    }

    // Un grupo coincide si cumple los filtros elegidos de las primeras "hasta" posiciones de la cadena
    private boolean coincide(Grupo g, int hasta) {
        for (int pos = 0; pos < hasta; pos++) {
            String valor = elegido(cadena[pos]);
            if (valor != null && !valor.equals(textoCampo(g, cadena[pos]))) {
                return false;
            }
        }
        return true;
    }

    // Opciones de la posicion "pos": solo lo que existe con lo elegido ANTES en la cadena
    private List<String> opciones(int pos) {
        List<String> lista = new ArrayList<>();
        for (Grupo g : listaGrupos) {
            if (coincide(g, pos)) {
                String texto = textoCampo(g, cadena[pos]);
                if (!lista.contains(texto)) {
                    lista.add(texto);
                }
            }
        }
        if (cadena[pos] >= 2) {
            Collections.sort(lista);   // paralelos y docentes en orden alfabetico
        }
        return lista;
    }

    // Vuelve a llenar los combos desde la posicion "desde" en adelante.
    // Un combo queda habilitado solo si el anterior ya tiene algo elegido.
    private void reconstruir(int desde) {
        cargando = true;
        for (int pos = desde; pos < cadena.length; pos++) {
            boolean habilitado = (pos == 0) || elegido(cadena[pos - 1]) != null;
            DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
            modelo.addElement("Seleccione");
            if (habilitado) {
                for (String o : opciones(pos)) {
                    modelo.addElement(o);
                }
            }
            JComboBox<String> combo = comboDe(cadena[pos]);
            combo.setModel(modelo);
            combo.setEnabled(habilitado);
        }
        cargando = false;
    }

    // Lo llaman los 4 combos cuando el usuario elige algo
    private void filtroCambio(int campo) {
        if (cargando) {
            return;
        }
        reconstruir(posicionDe(campo) + 1);   // se reinician los pasos siguientes
        cargarTabla();
    }

    // ---- Tabla ----
    // Muestra los estudiantes de los grupos que cumplen lo elegido. Solo la columna Nota se edita.
    private void cargarTabla() {
        String titulos[] = {"Estudiante", "Asignatura", "Paralelo", "Nota"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return columna == 3;
            }
        };
        listaMatriculas = new ArrayList<>();
        // Hasta elegir el primer filtro no se muestra nada
        if (elegido(cadena[0]) != null) {
            for (Grupo g : listaGrupos) {
                if (coincide(g, cadena.length)) {
                    List<Matricula> delGrupo = MatriculaDAO.listarPorGrupo(g.getId());
                    for (Matricula m : delGrupo) {
                        listaMatriculas.add(m);
                        String nota = "";
                        if (m.getNota() != null) {
                            nota = String.valueOf(m.getNota());
                        }
                        modelo.addRow(new Object[]{m.getEstudianteNombre(), g.getAsignaturaNombre(), g.getParalelo(), nota});
                    }
                }
            }
        }
        jtblNotas.setModel(modelo);
    }

    private void guardarNotas() {
        // Si el usuario todavia esta escribiendo en una celda, se termina la edicion
        if (jtblNotas.isEditing()) {
            jtblNotas.getCellEditor().stopCellEditing();
        }
        if (listaMatriculas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay estudiantes en la tabla. Elija los filtros (nivel, asignatura y paralelo).");
            return;
        }

        // 1) Primero se validan TODAS las notas (vacia, o numero entre 0 y 10)
        Double[] notas = new Double[listaMatriculas.size()];
        for (int i = 0; i < listaMatriculas.size(); i++) {
            Object valor = jtblNotas.getValueAt(i, 3);
            String texto = "";
            if (valor != null) {
                texto = valor.toString().trim().replace(',', '.');
            }
            String quien = jtblNotas.getValueAt(i, 0) + " (" + jtblNotas.getValueAt(i, 1)
                    + " " + jtblNotas.getValueAt(i, 2) + ")";
            if (texto.isEmpty()) {
                notas[i] = null;
            } else {
                try {
                    double n = Double.parseDouble(texto);
                    if (Double.isNaN(n) || n < 0 || n > Parametros.NOTA_MAXIMA) {
                        JOptionPane.showMessageDialog(this, "La nota de " + quien + " debe estar entre 0 y 10");
                        jtblNotas.changeSelection(i, 3, false, false);
                        return;
                    }
                    notas[i] = n;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "La nota de " + quien + " no es un número válido");
                    jtblNotas.changeSelection(i, 3, false, false);
                    return;
                }
            }
        }

        // 2) Si todo esta bien, se guarda fila por fila
        int guardadas = 0;
        for (int i = 0; i < listaMatriculas.size(); i++) {
            Matricula m = listaMatriculas.get(i);
            if (MatriculaDAO.actualizarNota(m.getEstudianteId(), m.getGrupoId(), notas[i])) {
                guardadas++;
            }
        }
        JOptionPane.showMessageDialog(this, "Se guardaron " + guardadas + " de " + listaMatriculas.size() + " notas");
        cargarTabla();
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
        jPanel1 = new javax.swing.JPanel();
        jlblNivel = new javax.swing.JLabel();
        jlblAsignatura = new javax.swing.JLabel();
        jlblParalelo = new javax.swing.JLabel();
        jcbxNivel = new javax.swing.JComboBox<>();
        jcbxAsignatura = new javax.swing.JComboBox<>();
        jcbxParalelo = new javax.swing.JComboBox<>();
        jlblDocente = new javax.swing.JLabel();
        jcbxDocente = new javax.swing.JComboBox<>();
        jScrollPanel1 = new javax.swing.JScrollPane();
        jtblNotas = new javax.swing.JTable();
        jbtnGuardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Notas");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jlblNivel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jlblNivel.setText("Nivel");

        jlblAsignatura.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jlblAsignatura.setText("Asignatura");

        jlblParalelo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jlblParalelo.setText("Paralelo");

        jcbxNivel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbxNivel.addActionListener(this::jcbxNivelActionPerformed);

        jcbxAsignatura.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbxParalelo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jlblDocente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jlblDocente.setText("Docente");

        jcbxDocente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlblNivel)
                        .addGap(45, 45, 45)
                        .addComponent(jcbxNivel, 0, 250, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblAsignatura)
                            .addComponent(jlblParalelo)
                            .addComponent(jlblDocente))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jcbxAsignatura, 0, 250, Short.MAX_VALUE)
                            .addComponent(jcbxParalelo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jcbxDocente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblNivel)
                    .addComponent(jcbxNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblAsignatura)
                    .addComponent(jcbxAsignatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblParalelo)
                    .addComponent(jcbxParalelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblDocente)
                    .addComponent(jcbxDocente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jtblNotas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPanel1.setViewportView(jtblNotas);

        jbtnGuardar.setText("Guardar Nota");
        jbtnGuardar.addActionListener(this::jbtnGuardarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(195, 195, 195)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jbtnGuardar)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jbtnGuardar)
                        .addContainerGap(189, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jcbxNivelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbxNivelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbxNivelActionPerformed

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
       guardarNotas();

    }//GEN-LAST:event_jbtnGuardarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new FrmNotas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPanel1;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JComboBox<String> jcbxAsignatura;
    private javax.swing.JComboBox<String> jcbxDocente;
    private javax.swing.JComboBox<String> jcbxNivel;
    private javax.swing.JComboBox<String> jcbxParalelo;
    private javax.swing.JLabel jlblAsignatura;
    private javax.swing.JLabel jlblDocente;
    private javax.swing.JLabel jlblNivel;
    private javax.swing.JLabel jlblParalelo;
    private javax.swing.JTable jtblNotas;
    // End of variables declaration//GEN-END:variables
}
