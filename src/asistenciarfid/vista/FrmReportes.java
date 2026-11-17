/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package asistenciarfid.vista;
import asistenciarfid.dao.AsignaturaDAO;
import asistenciarfid.dao.CarreraDAO;
import asistenciarfid.dao.GrupoDAO;
import asistenciarfid.dao.HorarioDAO;
import asistenciarfid.db.Conexion;
import asistenciarfid.modelo.Asignatura;
import asistenciarfid.modelo.Carrera;
import asistenciarfid.modelo.Grupo;
import asistenciarfid.modelo.Horario;
import asistenciarfid.modelo.Perfil;
import asistenciarfid.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author USER
 */
public class FrmReportes extends javax.swing.JInternalFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmReportes.class.getName());

    /**
     * Creates new form FrmReportes
     */
    public FrmReportes() {
        initComponents();
    }
    
    private Usuario usuario;
        private List<Grupo> listaGrupos = new ArrayList<>();   
    private int[] cadena = {4, 0, 1, 2, 3};                
    private Map<Integer, String> carreraDeAsignatura = new HashMap<>();   
    private boolean cargando = false;                      
    private Grupo grupoElegido = null;                     

    public FrmReportes(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        cargarGrupos();
        cargarGrupos();
        if (usuario != null && Perfil.DOCENTE.equals(usuario.getPerfil())) {
           
            jlblDocente.setVisible(false);
            jcbxDocente.setVisible(false);
            cadena = new int[]{4, 0, 1, 2};
        } else if (usuario != null && Perfil.COORDINADOR.equals(usuario.getPerfil())) {
          
            jlblCarrera.setVisible(false);
            jcbxCarrera.setVisible(false);
            cadena = new int[]{0, 1, 2, 3};
        }
        jcbxCarrera.addActionListener(e -> filtroCambio(4));
        jcbxNivel.addActionListener(e -> filtroCambio(0));
        jcbxAsignatura.addActionListener(e -> filtroCambio(1));
        jcbxParalelo.addActionListener(e -> filtroCambio(2));
        jcbxDocente.addActionListener(e -> filtroCambio(3));
        jrbtnAsistencia.addActionListener(e -> cargarFechas());
        jrbtnTotal.addActionListener(e -> cargarFechas());
        reconstruir(0);
        actualizarGrupo();


    }

    private void cargarGrupos() {
       
        carreraDeAsignatura.clear();
        Map<Integer, String> carreras = new HashMap<>();
        for (Carrera c : CarreraDAO.listar()) {
            carreras.put(c.getId(), c.getNombre());
        }
        for (Asignatura a : AsignaturaDAO.listar(null)) {
            carreraDeAsignatura.put(a.getId(), carreras.get(a.getCarreraId()));
        }
        if (usuario != null && Perfil.DOCENTE.equals(usuario.getPerfil())) {
            listaGrupos = GrupoDAO.listar(null, usuario.getId());
        } else if (usuario != null && Perfil.COORDINADOR.equals(usuario.getPerfil())) {
            listaGrupos = GrupoDAO.listar(usuario.getCarreraId(), null);
        } else {
            listaGrupos = GrupoDAO.listar(null, null);
        }
    }

    
    private JComboBox<String> comboDe(int campo) {
        if (campo == 4) {
            return jcbxCarrera;
        }
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

    private int posicionDe(int campo) {
        for (int i = 0; i < cadena.length; i++) {
            if (cadena[i] == campo) {
                return i;
            }
        }
        return -1;
    }

    private String textoCampo(Grupo g, int campo) {
        if (campo == 4) {
            String carrera = carreraDeAsignatura.get(g.getAsignaturaId());
            if (carrera == null) {
                carrera = "Sin carrera";
            }
            return carrera;
        }
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

    private String elegido(int campo) {
        JComboBox<String> combo = comboDe(campo);
        if (combo.getSelectedIndex() <= 0) {
            return null;
        }
        return String.valueOf(combo.getSelectedItem());
    }

    private boolean coincide(Grupo g, int hasta) {
        for (int pos = 0; pos < hasta; pos++) {
            String valor = elegido(cadena[pos]);
            if (valor != null && !valor.equals(textoCampo(g, cadena[pos]))) {
                return false;
            }
        }
        return true;
    }

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
            Collections.sort(lista);
        }
        return lista;
    }

   
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

    private void filtroCambio(int campo) {
        if (cargando) {
            return;
        }
        reconstruir(posicionDe(campo) + 1);
        actualizarGrupo();
    }

    
    private void actualizarGrupo() {
        grupoElegido = null;
        jlblHorario.setText(" ");
        if (elegido(1) != null && elegido(2) != null) {
            List<Grupo> coinciden = new ArrayList<>();
            for (Grupo g : listaGrupos) {
                if (coincide(g, cadena.length)) {
                    coinciden.add(g);
                }
            }
            if (coinciden.size() == 1) {
                grupoElegido = coinciden.get(0);
            } else if (coinciden.size() > 1) {
                jlblHorario.setText("Hay más de un grupo con esa combinación: elija también el docente");
            }
        }
        cargarFechas();
    }

    
    private void cargarFechas() {
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Seleccione");
        if (grupoElegido != null) {
            List<Horario> horarios = HorarioDAO.listarPorGrupo(grupoElegido.getId());
            String clases = "";
            for (Horario h : horarios) {
                if (!clases.isEmpty()) {
                    clases = clases + ", ";
                }
                clases = clases + h.getNombreDia() + " " + h.getHoraInicio().toString().substring(0, 5)
                        + "-" + h.getHoraFin().toString().substring(0, 5);
            }
            if (clases.isEmpty()) {
                jlblHorario.setText("Este grupo todavía no tiene horarios");
            } else {
                jlblHorario.setText("Clases: " + clases);
            }
            List<String> fechas = fechasConAsistencia(grupoElegido.getId());
            for (String fecha : fechas) {
                modelo.addElement(fecha);
            }
            if (fechas.isEmpty()) {
                jlblHorario.setText(jlblHorario.getText() + "  (todavía no hay asistencias registradas)");
            }
        }
        jcbxFecha.setModel(modelo);
        jcbxFecha.setEnabled(grupoElegido != null && jrbtnAsistencia.isSelected());
    }

    
    private List<String> fechasConAsistencia(int grupoId) {
        List<String> fechas = new ArrayList<>();
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return fechas;
        }
        try {
            String sql = "SELECT DISTINCT a.fecha FROM asistencia a "
                    + "JOIN horario h ON h.id = a.horario_id "
                    + "WHERE h.grupo_id = ? ORDER BY a.fecha DESC";
            PreparedStatement ps = cc.prepareStatement(sql);
            ps.setInt(1, grupoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                fechas.add(rs.getDate("fecha").toString());
            }
            rs.close();
            ps.close();
            cc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las fechas: " + ex);
        }
        return fechas;
    }

    private boolean validarCampos() {
        if (grupoElegido == null) {
            if (elegido(1) != null && elegido(2) != null) {
                JOptionPane.showMessageDialog(this, "Hay más de un grupo con esa combinación. Elija también el docente.");
            } else {
                JOptionPane.showMessageDialog(this, "Elija el grupo con los filtros (hasta el paralelo)");
            }
            return false;
        }
        if (jrbtnAsistencia.isSelected() && jcbxFecha.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Elija la fecha de la clase");
            jcbxFecha.requestFocus();
            return false;
        }
        return true;
    }

    
    private JasperPrint armarReporte() {
        JasperPrint imprimir = null;
        Connection cc = new Conexion().conectar();
        if (cc == null) {
            return null;
        }
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("P_GRUPO_ID", String.valueOf(grupoElegido.getId()));
            String archivo = "src/reportes/total_asistencias.jrxml";
            if (jrbtnAsistencia.isSelected()) {
                archivo = "src/reportes/asistencia_clase.jrxml";
                parametros.put("P_FECHA", String.valueOf(jcbxFecha.getSelectedItem()));
            }
            JasperReport reporte = JasperCompileManager.compileReport(archivo);
            imprimir = JasperFillManager.fillReport(reporte, parametros, cc);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el reporte: " + ex);
        } finally {
            try {
                cc.close();
            } catch (Exception ex) {
                
            }
        }
        return imprimir;
    }

    private void generarReporte() {
        if (!validarCampos()) {
            return;
        }
        JasperPrint imprimir = armarReporte();
        if (imprimir != null) {
            JasperViewer.viewReport(imprimir, false);   
        }
    }


    private void guardarPdf() {
        if (!validarCampos()) {
            return;
        }
        JasperPrint imprimir = armarReporte();
        if (imprimir == null) {
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new java.io.File("reporte.pdf"));
        if (selector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = selector.getSelectedFile().getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".pdf")) {
                ruta = ruta + ".pdf";
            }
            try {
                JasperExportManager.exportReportToPdfFile(imprimir, ruta);
                JOptionPane.showMessageDialog(this, "PDF guardado en:\n" + ruta);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el PDF: " + ex);
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

        bgReporte = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jlblCarrera = new javax.swing.JLabel();
        jlblNivel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jlblDocente = new javax.swing.JLabel();
        jcbxCarrera = new javax.swing.JComboBox<>();
        jcbxNivel = new javax.swing.JComboBox<>();
        jcbxAsignatura = new javax.swing.JComboBox<>();
        jcbxParalelo = new javax.swing.JComboBox<>();
        jcbxDocente = new javax.swing.JComboBox<>();
        jlblHorario = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jcbxFecha = new javax.swing.JComboBox<>();
        jrbtnAsistencia = new javax.swing.JRadioButton();
        jrbtnTotal = new javax.swing.JRadioButton();
        jbtnGenerar = new javax.swing.JButton();
        jbtnGuardarPdf = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Reportes");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jlblCarrera.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jlblCarrera.setText("Carrera");

        jlblNivel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jlblNivel.setText("Nivel");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Asignatura");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Paralelo");

        jlblDocente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jlblDocente.setText("Docente");

        jcbxCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbxNivel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbxAsignatura.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbxParalelo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbxDocente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jlblHorario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jlblHorario.setForeground(new java.awt.Color(0, 0, 153));
        jlblHorario.setText("jLabel2");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblCarrera)
                            .addComponent(jlblNivel))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbxCarrera, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jcbxNivel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jlblHorario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jlblDocente))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbxAsignatura, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jcbxParalelo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jcbxDocente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblCarrera)
                    .addComponent(jcbxCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblNivel)
                    .addComponent(jcbxNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jcbxAsignatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jcbxParalelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblDocente)
                    .addComponent(jcbxDocente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblHorario)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Reporte");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Fecha");

        jcbxFecha.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        bgReporte.add(jrbtnAsistencia);
        jrbtnAsistencia.setText("Asistencia de una clase");
        jrbtnAsistencia.addActionListener(this::jrbtnAsistenciaActionPerformed);

        bgReporte.add(jrbtnTotal);
        jrbtnTotal.setText("Total de asistencias");

        jbtnGenerar.setText("Generar Reporte");
        jbtnGenerar.addActionListener(this::jbtnGenerarActionPerformed);

        jbtnGuardarPdf.setText("Guardar PDF");
        jbtnGuardarPdf.addActionListener(this::jbtnGuardarPdfActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jrbtnAsistencia)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jrbtnTotal))
                            .addComponent(jcbxFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jbtnGenerar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnGuardarPdf)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jrbtnAsistencia)
                    .addComponent(jrbtnTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jcbxFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnGenerar)
                    .addComponent(jbtnGuardarPdf))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(352, 352, 352)
                .addComponent(jLabel1)
                .addContainerGap(367, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jrbtnAsistenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbtnAsistenciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jrbtnAsistenciaActionPerformed

    private void jbtnGuardarPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarPdfActionPerformed
        guardarPdf();
    }//GEN-LAST:event_jbtnGuardarPdfActionPerformed

    private void jbtnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGenerarActionPerformed
        generarReporte();
    }//GEN-LAST:event_jbtnGenerarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new FrmReportes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgReporte;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jbtnGenerar;
    private javax.swing.JButton jbtnGuardarPdf;
    private javax.swing.JComboBox<String> jcbxAsignatura;
    private javax.swing.JComboBox<String> jcbxCarrera;
    private javax.swing.JComboBox<String> jcbxDocente;
    private javax.swing.JComboBox<String> jcbxFecha;
    private javax.swing.JComboBox<String> jcbxNivel;
    private javax.swing.JComboBox<String> jcbxParalelo;
    private javax.swing.JLabel jlblCarrera;
    private javax.swing.JLabel jlblDocente;
    private javax.swing.JLabel jlblHorario;
    private javax.swing.JLabel jlblNivel;
    private javax.swing.JRadioButton jrbtnAsistencia;
    private javax.swing.JRadioButton jrbtnTotal;
    // End of variables declaration//GEN-END:variables
}
