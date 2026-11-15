/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package asistenciarfid.vista;

import asistenciarfid.modelo.Usuario;
import asistenciarfid.dao.CarreraDAO;
import asistenciarfid.dao.EstudianteDAO;
import asistenciarfid.dao.RegistroAcademicoDAO;
import asistenciarfid.modelo.Carrera;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.modelo.EstadoMateria;
import asistenciarfid.modelo.Perfil;
import asistenciarfid.modelo.RegistroAcademico;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USER
 */
public class FrmEstudiantes extends javax.swing.JInternalFrame {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmEstudiantes.class.getName());
    private Usuario usuario;
    private boolean modoNuevo = false;
    private boolean enEdicion = false;
    private Estudiante seleccionado;
    private List<Estudiante> listaEstudiantes = new ArrayList<>();
    private List<RegistroAcademico> listaMaterias = new ArrayList<>();
    
    /**
     * Creates new form FrmEstudiantes
     */
    public FrmEstudiantes() {
        initComponents();
    }

    public FrmEstudiantes(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        cargarCarreras();
        cargarEstudiantes();
        inicio();
    }

    //Estados de los botones 
    // Al abrir y al cancelar: cajas deshabilitadas y solo está activo Nuevo
    private void inicio() {
        modoNuevo = false;
        enEdicion = false;
        habilitarCajas(false);
        jbtnNuevo.setEnabled(true);
        jbtnGuardar.setEnabled(false);
        jbtnCancelar.setEnabled(false);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
    }

    // Nuevo: se habilitan las cajas, incluidos el usuario y la clave
    private void informacionNuevo() {
        enEdicion = true;
        habilitarCajas(true);
        jtxtUsuario.setEnabled(true);
        jpwdClave.setEnabled(true);
        jcbxCarrera.setEnabled(!esCoordinador());
        botonesEdicion();
    }

    // Editar: igual que Nuevo, pero el usuario, la clave y la carrera no se cambian
    private void informacionEditar() {
        enEdicion = true;
        habilitarCajas(true);
        jtxtUsuario.setEnabled(false);
        jpwdClave.setEnabled(false);
        jcbxCarrera.setEnabled(false);
        botonesEdicion();
    }

    private void botonesEdicion() {
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(true);
        jbtnCancelar.setEnabled(true);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
    }

    // Habilita o deshabilita las cajas comunes. El usuario, la clave y la carrera se manejan aparte
    private void habilitarCajas(boolean on) {
        jtxtCodigo.setEnabled(on);
        jtxtNombre.setEnabled(on);
        jtxtApellido.setEnabled(on);
        jtxtCorreo.setEnabled(on);
        jsldNivel.setEnabled(on);
        jtblMaterias.setEnabled(on);
        jcbxCarrera.setEnabled(false);
        jtxtUsuario.setEnabled(false);
        jpwdClave.setEnabled(false);
    }

    private boolean esCoordinador() {
        return Perfil.COORDINADOR.equals(usuario.getPerfil());
    }

    //Carga de datos 

    // Llena el combo de carreras. El coordinador solo ve la suya
    private void cargarCarreras() {
        DefaultComboBoxModel modelo = new DefaultComboBoxModel();
        modelo.addElement("Seleccione");
        for (Carrera c : CarreraDAO.listar()) {
            if (esCoordinador() && !Integer.valueOf(c.getId()).equals(usuario.getCarreraId())) {
                continue;
            }
            modelo.addElement(c);
        }
        jcbxCarrera.setModel(modelo);
        if (esCoordinador() && modelo.getSize() > 1) {
            jcbxCarrera.setSelectedIndex(1);
        }
    }

    // Llena la tabla de estudiantes (el coordinador solo ve los de su carrera)
    private void cargarEstudiantes() {
        Integer carreraId = null;
        if (esCoordinador()) {
            carreraId = usuario.getCarreraId();
        }
        listaEstudiantes = EstudianteDAO.listar(carreraId);
        String titulos[] = {"Código", "Apellido", "Nombre", "Correo", "Nivel"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (Estudiante est : listaEstudiantes) {
            modelo.addRow(new Object[]{est.getCodigoRfid(), est.getApellido(), est.getNombre(),
                est.getCorreo(), est.getNivel()});
        }
        jtblEstudiantes.setModel(modelo);
    }

    // En Nuevo: arma la lista de materias según la carrera y el nivel elegidos
    private void cargarMaterias() {
        Carrera c = carreraSeleccionada();
        if (c == null) {
            listaMaterias = new ArrayList<>();
        } else {
            listaMaterias = RegistroAcademicoDAO.generarParaCarrera(c.getId(), jsldNivel.getValue());
        }
        mostrarMaterias();
    }

    // Muestra listaMaterias en la tabla. Solo la columna Estado se puede cambiar, con un combo
    private void mostrarMaterias() {
        String titulos[] = {"Nivel", "Asignatura", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return columna == 2;
            }
        };
        for (RegistroAcademico r : listaMaterias) {
            modelo.addRow(new Object[]{r.getAsignaturaNivel(), r.getAsignaturaNombre(), r.getEstado()});
        }
        jtblMaterias.setModel(modelo);
        JComboBox combo = new JComboBox(new String[]{
            EstadoMateria.APROBADA, EstadoMateria.REPROBADA, EstadoMateria.AUN_NO_CURSA});
        jtblMaterias.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(combo));
    }

    // Pasa a listaMaterias los estados que el usuario escogió en la tabla
    private void leerEstadosMaterias() {
        if (jtblMaterias.isEditing()) {
            jtblMaterias.getCellEditor().stopCellEditing();
        }
        for (int i = 0; i < listaMaterias.size(); i++) {
            listaMaterias.get(i).setEstado((String) jtblMaterias.getValueAt(i, 2));
        }
    }

    //Ayudas 
    private Carrera carreraSeleccionada() {
        Object item = jcbxCarrera.getSelectedItem();
        if (item instanceof Carrera) {
            return (Carrera) item;
        }
        return null;
    }

    // Selecciona en el combo la carrera con ese id
    private void seleccionarCarrera(int carreraId) {
        for (int i = 0; i < jcbxCarrera.getItemCount(); i++) {
            Object item = jcbxCarrera.getItemAt(i);
            if (item instanceof Carrera && ((Carrera) item).getId() == carreraId) {
                jcbxCarrera.setSelectedIndex(i);
                return;
            }
        }
    }

    // Lee las cajas y arma el Estudiante
    private Estudiante armarEstudiante() {
        Estudiante est = new Estudiante();
        est.setCodigoRfid(jtxtCodigo.getText().trim());
        est.setNombre(jtxtNombre.getText().trim());
        est.setApellido(jtxtApellido.getText().trim());
        est.setCorreo(jtxtCorreo.getText().trim());
        est.setCarreraId(carreraSeleccionada().getId());
        est.setNivel(jsldNivel.getValue());
        return est;
    }

    private void limpiar() {
        seleccionado = null;
        jtxtCodigo.setText("");
        jtxtNombre.setText("");
        jtxtApellido.setText("");
        jtxtCorreo.setText("");
        jtxtUsuario.setText("");
        jpwdClave.setText("");
        if (!esCoordinador()) {
            jcbxCarrera.setSelectedIndex(0);
        }
        jsldNivel.setValue(1);
        listaMaterias = new ArrayList<>();
        mostrarMaterias();
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    // Avisa, da el foco al campo que está mal y devuelve false
    private boolean fallar(String texto, java.awt.Component campo) {
        JOptionPane.showMessageDialog(this, texto);
        campo.requestFocus();
        return false;
    }

    // Revisa que tenga una @ con texto antes, y un punto después
    private boolean correoValido(String correo) {
        int arroba = correo.indexOf('@');
        int punto = correo.lastIndexOf('.');
        return arroba > 0 && punto > arroba + 1 && punto < correo.length() - 1
                && correo.indexOf(' ') < 0;
    }

    //Validaciones
    private boolean validarCampos() {
        String codigo = jtxtCodigo.getText().trim();
        if (codigo.isEmpty() || codigo.contains(" ")) {
            return fallar("El código de la tarjeta es obligatorio y no puede tener espacios", jtxtCodigo);
        }
        if (jtxtNombre.getText().trim().isEmpty()) {
            return fallar("Escriba el nombre", jtxtNombre);
        }
        if (jtxtApellido.getText().trim().isEmpty()) {
            return fallar("Escriba el apellido", jtxtApellido);
        }
        String correo = jtxtCorreo.getText().trim();
        if (!correo.isEmpty() && !correoValido(correo)) {
            return fallar("El correo no tiene un formato válido", jtxtCorreo);
        }
        if (carreraSeleccionada() == null) {
            return fallar("Seleccione la carrera", jcbxCarrera);
        }
        if (modoNuevo) {
            String user = jtxtUsuario.getText().trim();
            if (user.isEmpty() || user.contains(" ")) {
                return fallar("El usuario es obligatorio y no puede tener espacios", jtxtUsuario);
            }
            if (new String(jpwdClave.getPassword()).isEmpty()) {
                return fallar("Escriba la clave", jpwdClave);
            }
        }
        // La tarjeta no puede repetirse. En Editar solo se revisa si cambió
        boolean cambio = modoNuevo || !codigo.equals(seleccionado.getCodigoRfid());
        if (cambio && EstudianteDAO.existeCodigo(codigo)) {
            return fallar("Esa tarjeta ya está registrada", jtxtCodigo);
        }
        return true;
    }

    // Lo que escribe el lector al pasar la tarjeta
    private void validarCodigoLeido() {
        if (!modoNuevo) {
            return;
        }
        String codigo = jtxtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            fallar("No se leyó ningún código", jtxtCodigo);
            return;
        }
        if (EstudianteDAO.existeCodigo(codigo)) {
            jtxtCodigo.setText("");
            fallar("Esa tarjeta ya está registrada", jtxtCodigo);
            return;
        }
        jtxtCodigo.setText(codigo);
        jtxtNombre.requestFocus();
    }

    //Acciones de los botones 

    private void accionNuevo() {
        modoNuevo = true;
        limpiar();
        informacionNuevo();
        jtxtCodigo.requestFocus();
    }

    private void accionEditar() {
        int fila = jtblEstudiantes.getSelectedRow();
        if (fila < 0) {
            return;
        }
        seleccionado = listaEstudiantes.get(fila);
        modoNuevo = false;
        jtxtCodigo.setText(seleccionado.getCodigoRfid());
        jtxtNombre.setText(seleccionado.getNombre());
        jtxtApellido.setText(seleccionado.getApellido());
        jtxtCorreo.setText(seleccionado.getCorreo() == null ? "" : seleccionado.getCorreo());
        seleccionarCarrera(seleccionado.getCarreraId());
        jsldNivel.setValue(seleccionado.getNivel());
        listaMaterias = RegistroAcademicoDAO.listarPorEstudiante(seleccionado.getId());
        mostrarMaterias();
        informacionEditar();
    }

    private void accionGuardar() {
        if (!validarCampos()) {
            return;
        }
        leerEstadosMaterias();
        Estudiante est = armarEstudiante();
        if (modoNuevo) {
            boolean ok = EstudianteDAO.insertarConUsuario(est, jtxtUsuario.getText().trim(),
                    new String(jpwdClave.getPassword()), listaMaterias);
            if (ok) {
                mensaje("Estudiante registrado");
                terminar();
            } else {
                mensaje("No se pudo registrar. Revise que el usuario no exista ya.");
            }
        } else {
            est.setId(seleccionado.getId());
            est.setUsuarioId(seleccionado.getUsuarioId());
            boolean ok = EstudianteDAO.actualizar(est);
            for (RegistroAcademico r : listaMaterias) {
                ok = RegistroAcademicoDAO.actualizarEstado(est.getId(), r.getAsignaturaId(), r.getEstado()) && ok;
            }
            if (ok) {
                mensaje("Cambios guardados");
                terminar();
            } else {
                mensaje("No se pudieron guardar todos los cambios");
            }
        }
    }

    // Después de guardar: recarga la tabla y vuelve al inicio
    private void terminar() {
        cargarEstudiantes();
        limpiar();
        inicio();
    }

    private void accionEliminar() {
        int fila = jtblEstudiantes.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Estudiante est = listaEstudiantes.get(fila);
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + est + "? También se borran sus materias, sus asistencias y su usuario.",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            if (EstudianteDAO.eliminar(est.getId())) {
                mensaje("Estudiante eliminado");
                cargarEstudiantes();
                inicio();
            } else {
                mensaje("No se pudo eliminar");
            }
        }
    }

    private void accionCancelar() {
        limpiar();
        inicio();
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
        jtxtCodigo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxtNombre = new javax.swing.JTextField();
        jtxtApellido = new javax.swing.JTextField();
        jtxtCorreo = new javax.swing.JTextField();
        jtxtUsuario = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jpwdClave = new javax.swing.JPasswordField();
        jcbxCarrera = new javax.swing.JComboBox<>();
        jsldNivel = new javax.swing.JSlider();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblMaterias = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblEstudiantes = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jbtnNuevo = new javax.swing.JButton();
        jbtnGuardar = new javax.swing.JButton();
        jbtnEditar = new javax.swing.JButton();
        jbtnEliminar = new javax.swing.JButton();
        jbtnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Registro de Estudiantes");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jtxtCodigo.addActionListener(this::jtxtCodigoActionPerformed);

        jLabel2.setText("Código RFID: ");

        jLabel3.setText("Nombre:");

        jLabel4.setText("Apellido: ");

        jLabel5.setText("Correo: ");

        jLabel6.setText("Usuario:");

        jLabel7.setText("Clave:");

        jLabel8.setText("Carrera: ");

        jLabel9.setText("Nivel: ");

        jcbxCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbxCarrera.addActionListener(this::jcbxCarreraActionPerformed);

        jsldNivel.setMajorTickSpacing(1);
        jsldNivel.setMaximum(10);
        jsldNivel.setPaintLabels(true);
        jsldNivel.setPaintTicks(true);
        jsldNivel.setValue(0);
        jsldNivel.addChangeListener(this::jsldNivelStateChanged);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Estudiantes");

        jtblMaterias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nivel", "Asignatura", "Estado"
            }
        ));
        jScrollPane1.setViewportView(jtblMaterias);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Materias del estudiante");

        jtblEstudiantes.setModel(new javax.swing.table.DefaultTableModel(
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
        jtblEstudiantes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtblEstudiantesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jtblEstudiantes);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jtxtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(53, 53, 53)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtxtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                                    .addComponent(jpwdClave)
                                    .addComponent(jcbxCarrera, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 33, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jsldNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                        .addGap(6, 6, 6))))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(299, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jtxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jpwdClave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jcbxCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9)
                    .addComponent(jsldNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(140, 140, 140)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(429, Short.MAX_VALUE)))
        );

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jbtnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                    .addComponent(jbtnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                    .addComponent(jbtnEditar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbtnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(209, 209, 209)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        accionNuevo();
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        accionGuardar();
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jbtnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditarActionPerformed
        accionEditar();
    }//GEN-LAST:event_jbtnEditarActionPerformed

    private void jbtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEliminarActionPerformed
        accionEliminar();
    }//GEN-LAST:event_jbtnEliminarActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        accionCancelar();
    }//GEN-LAST:event_jbtnCancelarActionPerformed

    private void jtxtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCodigoActionPerformed
        validarCodigoLeido();
    }//GEN-LAST:event_jtxtCodigoActionPerformed

    private void jcbxCarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbxCarreraActionPerformed
        if(modoNuevo){cargarMaterias();}
    }//GEN-LAST:event_jcbxCarreraActionPerformed

    private void jsldNivelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsldNivelStateChanged
        if(modoNuevo){cargarMaterias();}
    }//GEN-LAST:event_jsldNivelStateChanged

    private void jtblEstudiantesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtblEstudiantesMouseClicked
        if (!enEdicion) {
            boolean hayFila = jtblEstudiantes.getSelectedRow() >= 0;
            jbtnEditar.setEnabled(hayFila);
            jbtnEliminar.setEnabled(hayFila);
        }
    }//GEN-LAST:event_jtblEstudiantesMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new FrmEstudiantes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JButton jbtnEditar;
    private javax.swing.JButton jbtnEliminar;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JButton jbtnNuevo;
    private javax.swing.JComboBox<String> jcbxCarrera;
    private javax.swing.JPasswordField jpwdClave;
    private javax.swing.JSlider jsldNivel;
    private javax.swing.JTable jtblEstudiantes;
    private javax.swing.JTable jtblMaterias;
    private javax.swing.JTextField jtxtApellido;
    private javax.swing.JTextField jtxtCodigo;
    private javax.swing.JTextField jtxtCorreo;
    private javax.swing.JTextField jtxtNombre;
    private javax.swing.JTextField jtxtUsuario;
    // End of variables declaration//GEN-END:variables
}
