/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package asistenciarfid.vista;

import asistenciarfid.dao.EstudianteDAO;
import asistenciarfid.dao.HorarioDAO;
import asistenciarfid.dao.PeriodoDAO;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.Horario;
import asistenciarfid.modelo.Perfil;
import asistenciarfid.modelo.Periodo;
import asistenciarfid.modelo.Usuario;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * "Mi horario": muestra, solo en lectura, las clases semanales del periodo activo.
 *  - DOCENTE: las clases de los grupos que dicta.
 *  - ESTUDIANTE: las clases de los grupos en los que está matriculado.
 */
public class FrmMiHorario extends javax.swing.JInternalFrame {

    private static final String[] DIAS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    private Usuario usuario;

    // Para que NetBeans pueda abrir el diseño
    public FrmMiHorario() {
        initComponents();
    }

    public FrmMiHorario(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jtblSemana.setDefaultEditor(Object.class, null);
        jtblSemana.setRowSelectionAllowed(false);
        jtblSemana.setDefaultRenderer(Object.class, new RenderizadorMultilinea());
        jtblLista.setDefaultEditor(Object.class, null);
        cargarHorario();
    }

    // Busca las clases según el perfil y llena las dos tablas
    private void cargarHorario() {
        List<Horario> horarios = new ArrayList<>();
        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "No se ha iniciado sesión.");
            dispose();
            return;
        }
        if (Perfil.DOCENTE.equals(usuario.getPerfil())) {
            jlblNombre.setText("Horario del docente " + usuario.getNombre());
            horarios = HorarioDAO.listarDeDocente(usuario.getId());
        } else if (Perfil.ESTUDIANTE.equals(usuario.getPerfil())) {
            Estudiante estudiante = EstudianteDAO.buscarPorUsuarioId(usuario.getId());
            if (estudiante == null) {
                JOptionPane.showMessageDialog(this, "Su usuario no está asociado a un estudiante.");
                dispose();
                return;
            }
            jlblNombre.setText("Horario de " + estudiante.getNombre() + " " + estudiante.getApellido());
            horarios = HorarioDAO.listarDeEstudiante(estudiante.getId());
        } else {
            JOptionPane.showMessageDialog(this, "Esta ventana es para docentes y estudiantes.");
            dispose();
            return;
        }

        Periodo periodo = PeriodoDAO.obtenerActivo();
        if (periodo == null) {
            jlblPeriodo.setText("No hay un periodo activo");
        } else {
            jlblPeriodo.setText("Periodo: " + periodo.getNombre() + " (" + periodo.getFechaInicio()
                    + " a " + periodo.getFechaFin() + ")");
        }
        if (horarios.isEmpty() && periodo != null) {
            jlblPeriodo.setText(jlblPeriodo.getText() + "  -  No tiene clases con horario en este periodo");
        }
        llenarLista(horarios);
        llenarSemana(horarios);
    }

    private static String hora(java.sql.Time t) {
        return t.toString().substring(0, 5);
    }

    // Tabla en lista: una fila por clase, ordenada por día y hora
    private void llenarLista(List<Horario> horarios) {
        String[] titulos = {"Día", "Inicio", "Fin", "Asignatura", "Paralelo", "Aula"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos);
        for (Horario h : horarios) {
            modelo.addRow(new Object[]{h.getNombreDia(), hora(h.getHoraInicio()), hora(h.getHoraFin()),
                h.getAsignaturaNombre(), h.getParalelo(), h.getAula() == null ? "" : h.getAula()});
        }
        jtblLista.setModel(modelo);
    }

    // Tabla semanal: una fila por franja (inicio-fin) y una columna por día.
    // Sábado y domingo solo aparecen si hay clases.
    private void llenarSemana(List<Horario> horarios) {
        int ultimoDia = 5;
        for (Horario h : horarios) {
            if (h.getDiaSemana() > ultimoDia) {
                ultimoDia = h.getDiaSemana();
            }
        }
        List<String> franjas = new ArrayList<>();
        for (Horario h : horarios) {
            String franja = hora(h.getHoraInicio()) + " - " + hora(h.getHoraFin());
            if (!franjas.contains(franja)) {
                franjas.add(franja);
            }
        }
        java.util.Collections.sort(franjas);   // "HH:mm - HH:mm" se ordena bien como texto

        String[] titulos = new String[ultimoDia + 1];
        titulos[0] = "Hora";
        for (int d = 1; d <= ultimoDia; d++) {
            titulos[d] = DIAS[d - 1];
        }
        DefaultTableModel modelo = new DefaultTableModel(null, titulos);
        for (String franja : franjas) {
            Object[] fila = new Object[ultimoDia + 1];
            fila[0] = franja;
            for (int d = 1; d <= ultimoDia; d++) {
                fila[d] = "";
            }
            for (Horario h : horarios) {
                String f = hora(h.getHoraInicio()) + " - " + hora(h.getHoraFin());
                if (f.equals(franja)) {
                    String aula = h.getAula() == null || h.getAula().isEmpty() ? "" : "\nAula " + h.getAula();
                    String texto = h.getAsignaturaNombre() + " (" + h.getParalelo() + ")" + aula;
                    String actual = String.valueOf(fila[h.getDiaSemana()]);
                    fila[h.getDiaSemana()] = actual.isEmpty() ? texto : actual + "\n" + texto;
                }
            }
            modelo.addRow(fila);
        }
        jtblSemana.setModel(modelo);
        jtblSemana.setRowHeight(64);
        if (jtblSemana.getColumnCount() > 0) {
            jtblSemana.getColumnModel().getColumn(0).setPreferredWidth(110);
            jtblSemana.getColumnModel().getColumn(0).setMaxWidth(130);
        }
    }

    // Muestra varias líneas dentro de una celda
    private static class RenderizadorMultilinea extends JTextArea implements TableCellRenderer {

        RenderizadorMultilinea() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 5, 3, 5));
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                boolean foco, int fila, int columna) {
            setText(valor == null ? "" : valor.toString());
            boolean conClase = columna > 0 && !getText().isEmpty();
            if (columna == 0) {
                setBackground(new java.awt.Color(235, 235, 235));
                setFont(tabla.getFont().deriveFont(java.awt.Font.BOLD));
            } else {
                setBackground(conClase ? new java.awt.Color(214, 234, 248) : java.awt.Color.WHITE);
                setFont(tabla.getFont());
            }
            setForeground(java.awt.Color.BLACK);
            return this;
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
        jlblNombre = new javax.swing.JLabel();
        jlblPeriodo = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblSemana = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblLista = new javax.swing.JTable();
        jbtnActualizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("MI HORARIO");

        jlblNombre.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jtblSemana.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Hora", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes"
            }
        ));
        jScrollPane1.setViewportView(jtblSemana);

        jTabbedPane1.addTab("Semana", jScrollPane1);

        jtblLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Día", "Inicio", "Fin", "Asignatura", "Paralelo", "Aula"
            }
        ));
        jScrollPane2.setViewportView(jtblLista);

        jTabbedPane1.addTab("Lista", jScrollPane2);

        jbtnActualizar.setText("Actualizar");
        jbtnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnActualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jlblNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblPeriodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtnActualizar)))
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblNombre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblPeriodo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnActualizar)
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnActualizarActionPerformed
        cargarHorario();
    }//GEN-LAST:event_jbtnActualizarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton jbtnActualizar;
    private javax.swing.JLabel jlblNombre;
    private javax.swing.JLabel jlblPeriodo;
    private javax.swing.JTable jtblLista;
    private javax.swing.JTable jtblSemana;
    // End of variables declaration//GEN-END:variables
}
