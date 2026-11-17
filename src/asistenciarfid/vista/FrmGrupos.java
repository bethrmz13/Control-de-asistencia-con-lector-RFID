/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package asistenciarfid.vista;
import asistenciarfid.dao.AsignaturaDAO;
import asistenciarfid.dao.GrupoDAO;
import asistenciarfid.dao.UsuarioDAO;
import asistenciarfid.modelo.Asignatura;
import asistenciarfid.modelo.Grupo;
import asistenciarfid.modelo.Perfil;
import asistenciarfid.modelo.Usuario;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import asistenciarfid.dao.CarreraDAO;
import asistenciarfid.modelo.Carrera;

/**
 *
 * @author USER
 */
public class FrmGrupos extends javax.swing.JInternalFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmGrupos.class.getName());

    /**
     * Creates new form FrmGrupos
     */
    public FrmGrupos() {
        initComponents();
    }
    
    private Usuario usuario;
   private List<Grupo> listaGrupos;                                      
    private List<Asignatura> listaAsignaturas = new ArrayList<>();         
    private List<Integer> listaNiveles = new ArrayList<>();                
    private List<Asignatura> asignaturasDelNivel = new ArrayList<>();      
    private List<Usuario> listaDocentes;                                   
    private boolean editando = false;                                      
    private boolean camposActivos = false;                                 
    private boolean cargando = false;                                      
    private int grupoSeleccionadoId = 0;                                   
    private List<Carrera> listaCarreras = new ArrayList<>();               


    public FrmGrupos(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        cargarAsignaturas();
        cargarCarreras();
        cargarNiveles();
        cargarDocentes();
        cargarTabla();
        InformacionInicio();
        BotonesInicio();
        jtblGrupos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });
        
        jcbxNivel.addActionListener(e -> alElegirNivel());
        jcbxAsignatura.addActionListener(e -> alElegirAsignatura());
        jrbtnParaleloA.addActionListener(e -> actualizarHabilitados());
        jrbtnParaleloB.addActionListener(e -> actualizarHabilitados());
        jrbtnParaleloC.addActionListener(e -> actualizarHabilitados());
        jcbxCarrera.addActionListener(e -> alElegirCarrera());
        
        if (!usaCarrera()) {
            jlblCarrera.setVisible(false);
            jcbxCarrera.setVisible(false);
        }
    }

    
    private boolean usaCarrera() {
        return !(usuario != null && Perfil.COORDINADOR.equals(usuario.getPerfil()));
    }

    private void cargarCarreras() {
        listaCarreras = CarreraDAO.listar();
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Seleccione");
        for (Carrera c : listaCarreras) {
            modelo.addElement(c.getNombre());
        }
        jcbxCarrera.setModel(modelo);
    }

    
    private Integer carreraElegidaId() {
        int indice = jcbxCarrera.getSelectedIndex();
        if (!usaCarrera() || indice <= 0) {
            return null;
        }
        return listaCarreras.get(indice - 1).getId();
    }

    
    private List<Asignatura> asignaturasDeLaCarrera() {
        List<Asignatura> lista = new ArrayList<>();
        Integer carreraId = carreraElegidaId();
        if (usaCarrera() && carreraId == null) {
            return lista;
        }
        for (Asignatura a : listaAsignaturas) {
            if (carreraId == null || a.getCarreraId() == carreraId.intValue()) {
                lista.add(a);
            }
        }
        return lista;
    }

    private String nombreCarreraDe(int asignaturaId) {
        for (Asignatura a : listaAsignaturas) {
            if (a.getId() == asignaturaId) {
                for (Carrera c : listaCarreras) {
                    if (c.getId() == a.getCarreraId()) {
                        return c.getNombre();
                    }
                }
            }
        }
        return "";
    }

    private void alElegirCarrera() {
        if (cargando) {
            return;
        }
        cargarNiveles();                 
        llenarAsignaturasDelNivel();
        bgParalelo.clearSelection();
        jcbxDocente.setSelectedIndex(0);
        actualizarHabilitados();
    }

   
    
    private Integer carreraDelUsuario() {
        if (usuario != null && Perfil.COORDINADOR.equals(usuario.getPerfil())) {
            return usuario.getCarreraId();
        }
        return null;
    }

    private void cargarAsignaturas() {
        listaAsignaturas = AsignaturaDAO.listar(carreraDelUsuario());
    }

   
    private void cargarNiveles() {
        listaNiveles = new ArrayList<>();
        for (Asignatura a : asignaturasDeLaCarrera()) {
            if (!listaNiveles.contains(a.getNivel())) {
                listaNiveles.add(a.getNivel());
            }
        }
        Collections.sort(listaNiveles);
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Seleccione");
        for (Integer nivel : listaNiveles) {
            modelo.addElement("Nivel " + nivel);
        }
        jcbxNivel.setModel(modelo);
    }


   
    private void llenarAsignaturasDelNivel() {
        asignaturasDelNivel = new ArrayList<>();
        int indice = jcbxNivel.getSelectedIndex();
        if (indice > 0) {
            int nivel = listaNiveles.get(indice - 1);
            for (Asignatura a : asignaturasDeLaCarrera()) {
                if (a.getNivel() == nivel) {
                    asignaturasDelNivel.add(a);
                }
            }
        }
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Seleccione");
        for (Asignatura a : asignaturasDelNivel) {
            modelo.addElement(a.getNombre());
        }
        jcbxAsignatura.setModel(modelo);
    }


    private void cargarDocentes() {
        listaDocentes = UsuarioDAO.listarPorPerfil(Perfil.DOCENTE);
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Sin docente");
        for (Usuario u : listaDocentes) {
            modelo.addElement(u.getNombre());
        }
        jcbxDocente.setModel(modelo);
    }

    private void cargarTabla() {
String titulos[] = {"Carrera", "Asignatura", "Nivel", "Paralelo", "Docente"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos);
        listaGrupos = GrupoDAO.listar(carreraDelUsuario(), null);
        for (Grupo g : listaGrupos) {
            String docente = g.getDocenteNombre();
            if (docente == null) {
                docente = "Sin docente";
            }
modelo.addRow(new Object[]{nombreCarreraDe(g.getAsignaturaId()), g.getAsignaturaNombre(),g.getAsignaturaNivel(), g.getParalelo(), docente});        }
        jtblGrupos.setModel(modelo);
    }

    
    private String paraleloElegido() {
        if (jrbtnParaleloA.isSelected()) {
            return "A";
        }
        if (jrbtnParaleloB.isSelected()) {
            return "B";
        }
        if (jrbtnParaleloC.isSelected()) {
            return "C";
        }
        return "";
    }

    

    private void alElegirNivel() {
        if (cargando) {
            return;
        }
        llenarAsignaturasDelNivel();     
        bgParalelo.clearSelection();     
        jcbxDocente.setSelectedIndex(0);
        actualizarHabilitados();
    }

    private void alElegirAsignatura() {
        if (cargando) {
            return;
        }
        bgParalelo.clearSelection();
        jcbxDocente.setSelectedIndex(0);
        actualizarHabilitados();
    }

       
    private void actualizarHabilitados() {
        boolean hayCarrera = !usaCarrera() || jcbxCarrera.getSelectedIndex() > 0;
        boolean hayNivel = jcbxNivel.getSelectedIndex() > 0;
        boolean hayAsignatura = jcbxAsignatura.getSelectedIndex() > 0;
        boolean hayParalelo = !paraleloElegido().isEmpty();
        boolean nuevo = camposActivos && !editando;
        jcbxCarrera.setEnabled(nuevo && usaCarrera());
        jcbxNivel.setEnabled(nuevo && hayCarrera);
        jcbxAsignatura.setEnabled(nuevo && hayCarrera && hayNivel);
        jrbtnParaleloA.setEnabled(nuevo && hayAsignatura);
        jrbtnParaleloB.setEnabled(nuevo && hayAsignatura);
        jrbtnParaleloC.setEnabled(nuevo && hayAsignatura);
        jcbxDocente.setEnabled(camposActivos && hayAsignatura && hayParalelo);
    }


    private void habilitarCampos(boolean estado) {
        camposActivos = estado;
        actualizarHabilitados();
    }

    
    private void seleccionarFila() {
        int fila = jtblGrupos.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Grupo g = listaGrupos.get(fila);
        grupoSeleccionadoId = g.getId();

        cargando = true;
        int carreraId = 0;
        for (Asignatura a : listaAsignaturas) {
            if (a.getId() == g.getAsignaturaId()) {
                carreraId = a.getCarreraId();
            }
        }
        jcbxCarrera.setSelectedIndex(0);
        for (int i = 0; i < listaCarreras.size(); i++) {
            if (listaCarreras.get(i).getId() == carreraId) {
                jcbxCarrera.setSelectedIndex(i + 1);
            }
        }
        cargarNiveles();
        jcbxNivel.setSelectedIndex(0);
        for (int i = 0; i < listaNiveles.size(); i++) {
            if (listaNiveles.get(i) == g.getAsignaturaNivel()) {
                jcbxNivel.setSelectedIndex(i + 1);
            }
        }
        llenarAsignaturasDelNivel();
        for (int i = 0; i < asignaturasDelNivel.size(); i++) {
            if (asignaturasDelNivel.get(i).getId() == g.getAsignaturaId()) {
                jcbxAsignatura.setSelectedIndex(i + 1);
            }
        }

        if (g.getParalelo().equals("A")) {
            jrbtnParaleloA.setSelected(true);
        } else if (g.getParalelo().equals("B")) {
            jrbtnParaleloB.setSelected(true);
        } else if (g.getParalelo().equals("C")) {
            jrbtnParaleloC.setSelected(true);
        } else {
            bgParalelo.clearSelection();
        }
        jcbxDocente.setSelectedIndex(0);
        if (g.getDocenteId() != null) {
            for (int i = 0; i < listaDocentes.size(); i++) {
                if (listaDocentes.get(i).getId() == g.getDocenteId()) {
                    jcbxDocente.setSelectedIndex(i + 1);
                }
            }
        }
        cargando = false;
        habilitarCampos(false);
        BotonesSeleccionado();
    }

    private void limpiarCampos() {
        cargando = true;
        jcbxCarrera.setSelectedIndex(0);
        cargarNiveles();
        jcbxNivel.setSelectedIndex(0);
        llenarAsignaturasDelNivel();
        bgParalelo.clearSelection();
        jcbxDocente.setSelectedIndex(0);
        jtblGrupos.clearSelection();
        grupoSeleccionadoId = 0;
        editando = false;
        cargando = false;
    }

    
    private void InformacionInicio() {
        limpiarCampos();
        habilitarCampos(false);
    }

    private void BotonesInicio() {
        jbtnNuevo.setEnabled(true);
        jbtnGuardar.setEnabled(false);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(false);
    }

    private void InformacionNuevo() {
        limpiarCampos();
        habilitarCampos(true);      
        jcbxNivel.requestFocus();
    }

    private void BotonesNuevo() {
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(true);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(true);
    }

    private void BotonesSeleccionado() {
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(false);
        jbtnEditar.setEnabled(true);
        jbtnEliminar.setEnabled(true);
        jbtnCancelar.setEnabled(true);
    }

    private boolean validarCampos() {
        if (usaCarrera() && jcbxCarrera.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione la carrera");
            jcbxCarrera.requestFocus();
            return false;
        }

        if (jcbxNivel.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione el nivel");
            jcbxNivel.requestFocus();
            return false;
        }
        if (jcbxAsignatura.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione la asignatura");
            jcbxAsignatura.requestFocus();
            return false;
        }
        String paralelo = paraleloElegido();
        if (paralelo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Elija el paralelo (A, B o C)");
            jrbtnParaleloA.requestFocus();
            return false;
        }
        
        int asignaturaId = asignaturasDelNivel.get(jcbxAsignatura.getSelectedIndex() - 1).getId();
        List<Grupo> existentes = GrupoDAO.listarPorAsignatura(asignaturaId);
        for (Grupo g : existentes) {
            if (g.getParalelo().equals(paralelo) && g.getId() != grupoSeleccionadoId) {
                JOptionPane.showMessageDialog(this, "Esa asignatura ya tiene el paralelo " + paralelo);
                jrbtnParaleloA.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void guardar() {
        if (!validarCampos()) {
            return;
        }
        Grupo g = new Grupo();
        g.setAsignaturaId(asignaturasDelNivel.get(jcbxAsignatura.getSelectedIndex() - 1).getId());
        g.setParalelo(paraleloElegido());
        if (jcbxDocente.getSelectedIndex() == 0) {
            g.setDocenteId(null);
        } else {
            g.setDocenteId(listaDocentes.get(jcbxDocente.getSelectedIndex() - 1).getId());
        }
        boolean ok;
        if (editando) {
            g.setId(grupoSeleccionadoId);
            ok = GrupoDAO.actualizar(g);
        } else {
            ok = GrupoDAO.insertar(g);
        }
        if (ok) {
            JOptionPane.showMessageDialog(this, "Grupo guardado correctamente");
            cargarTabla();
            InformacionInicio();
            BotonesInicio();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el grupo. Revise que exista un periodo activo.");
        }
    }

    private void eliminar() {
        if (grupoSeleccionadoId == 0) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar este grupo? También se borrarán sus horarios y matrículas.",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        if (GrupoDAO.eliminar(grupoSeleccionadoId)) {
            JOptionPane.showMessageDialog(this, "Grupo eliminado");
            cargarTabla();
            InformacionInicio();
            BotonesInicio();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el grupo");
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

        bgParalelo = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jcbxAsignatura = new javax.swing.JComboBox<>();
        jrbtnParaleloA = new javax.swing.JRadioButton();
        jrbtnParaleloB = new javax.swing.JRadioButton();
        jrbtnParaleloC = new javax.swing.JRadioButton();
        jcbxDocente = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jcbxNivel = new javax.swing.JComboBox<>();
        jlblCarrera = new javax.swing.JLabel();
        jcbxCarrera = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jbtnNuevo = new javax.swing.JButton();
        jbtnGuardar = new javax.swing.JButton();
        jbtnEditar = new javax.swing.JButton();
        jbtnEliminar = new javax.swing.JButton();
        jbtnCancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblGrupos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Asignatura");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Paralelo");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Docente");

        jcbxAsignatura.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        bgParalelo.add(jrbtnParaleloA);
        jrbtnParaleloA.setText("A");

        bgParalelo.add(jrbtnParaleloB);
        jrbtnParaleloB.setText("B");

        bgParalelo.add(jrbtnParaleloC);
        jrbtnParaleloC.setText("C");

        jcbxDocente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Nivel");

        jcbxNivel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jlblCarrera.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jlblCarrera.setText("Carrera");

        jcbxCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jlblCarrera))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jrbtnParaleloA)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrbtnParaleloB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrbtnParaleloC))
                    .addComponent(jcbxAsignatura, 0, 274, Short.MAX_VALUE)
                    .addComponent(jcbxDocente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jcbxNivel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jcbxCarrera, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(247, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblCarrera)
                    .addComponent(jcbxCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jcbxNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbxAsignatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jrbtnParaleloA)
                    .addComponent(jrbtnParaleloB)
                    .addComponent(jrbtnParaleloC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jcbxDocente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Grupos");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(jbtnNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnEditar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnEliminar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnCancelar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnNuevo)
                    .addComponent(jbtnGuardar)
                    .addComponent(jbtnEditar)
                    .addComponent(jbtnEliminar)
                    .addComponent(jbtnCancelar))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        jtblGrupos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jtblGrupos);

        jScrollPane1.setViewportView(jScrollPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(281, 281, 281)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        InformacionNuevo();
        BotonesNuevo();
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jbtnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditarActionPerformed
        editando = true;
        habilitarCampos(true);
        BotonesNuevo();
        jcbxDocente.requestFocus();
    }//GEN-LAST:event_jbtnEditarActionPerformed

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        guardar();
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jbtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEliminarActionPerformed
        eliminar();
    }//GEN-LAST:event_jbtnEliminarActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        InformacionInicio();
        BotonesInicio();
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
        java.awt.EventQueue.invokeLater(() -> new FrmGrupos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgParalelo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JButton jbtnEditar;
    private javax.swing.JButton jbtnEliminar;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JButton jbtnNuevo;
    private javax.swing.JComboBox<String> jcbxAsignatura;
    private javax.swing.JComboBox<String> jcbxCarrera;
    private javax.swing.JComboBox<String> jcbxDocente;
    private javax.swing.JComboBox<String> jcbxNivel;
    private javax.swing.JLabel jlblCarrera;
    private javax.swing.JRadioButton jrbtnParaleloA;
    private javax.swing.JRadioButton jrbtnParaleloB;
    private javax.swing.JRadioButton jrbtnParaleloC;
    private javax.swing.JTable jtblGrupos;
    // End of variables declaration//GEN-END:variables
}
