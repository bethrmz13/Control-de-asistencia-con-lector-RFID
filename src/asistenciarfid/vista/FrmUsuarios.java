package asistenciarfid.vista;

import asistenciarfid.modelo.Usuario;
import asistenciarfid.modelo.Carrera;
import asistenciarfid.modelo.Perfil;
import asistenciarfid.dao.UsuarioDAO;
import asistenciarfid.dao.CarreraDAO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import asistenciarfid.modelo.Estudiante;
import asistenciarfid.dao.EstudianteDAO;

public class FrmUsuarios extends javax.swing.JInternalFrame {

    private Usuario usuario;

    public FrmUsuarios() {
        this(null);
    }

    public FrmUsuarios(Usuario usuario) {
        initComponents();
        this.usuario = usuario;
        iniciarFormulario();
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }


    private List<Usuario> usuarios = new ArrayList<>();
    private int idUsuarioSeleccionado;
    private boolean nuevo;
    private Estudiante estudianteSeleccionado;
    private boolean carreraEstudianteEditable;

    private boolean tienePermiso() {
        return usuario != null && Perfil.ADMIN.equals(usuario.getPerfil());
    }

    private void iniciarFormulario() {
        DefaultComboBoxModel<Object> perfiles = new DefaultComboBoxModel<>();
        perfiles.addElement("Seleccione");
        perfiles.addElement(Perfil.ADMIN);
        perfiles.addElement(Perfil.COORDINADOR);
        perfiles.addElement(Perfil.DOCENTE);
        perfiles.addElement(Perfil.REGISTRO);
        perfiles.addElement(Perfil.ESTUDIANTE);
        jcbxPerfil.setModel(perfiles);
        jtblUsuarios.setDefaultEditor(Object.class, null);
        jtblUsuarios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtblUsuarios.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evento) {
                if (!evento.getValueIsAdjusting()) {
                    seleccionarUsuario();
                }
            }
        });
        if (tienePermiso()) {
            cargarCarreras();
            cargarUsuarios();
        } else if (usuario != null) {
            JOptionPane.showMessageDialog(this, "Solo ADMIN puede administrar usuarios.");
        }
        Cancelar();
    }

    private void cargarCarreras() {
        DefaultComboBoxModel<Object> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Seleccione");
        List<Carrera> carreras = CarreraDAO.listar();
        for (Carrera carrera : carreras) {
            modelo.addElement(carrera);
        }
        jcbxCarrera.setModel(modelo);
    }

    private String nombreCarrera(Integer id) {
        if (id != null) {
            for (int i = 1; i < jcbxCarrera.getItemCount(); i++) {
                Carrera carrera = (Carrera) jcbxCarrera.getItemAt(i);
                if (carrera.getId() == id) {
                    return carrera.getNombre();
                }
            }
            return "ID " + id;
        }
        return "";
    }

    private void cargarUsuarios() {
        if (!tienePermiso()) {
            return;
        }
        idUsuarioSeleccionado = 0;
        jtblUsuarios.clearSelection();
        usuarios = UsuarioDAO.listar();
        String[] titulos = {"ID", "Usuario", "Nombre", "Perfil", "Carrera", "Activo"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos);
        for (Usuario registrado : usuarios) {
            modelo.addRow(new Object[]{registrado.getId(), registrado.getUsername(),
                registrado.getNombre(), registrado.getPerfil(),
                nombreCarrera(registrado.getCarreraId()), registrado.isActivo() ? "Sí" : "No"});
        }
        jtblUsuarios.setModel(modelo);
    }

    private void seleccionarUsuario() {
        if (!tienePermiso() || nuevo || jtblUsuarios.getSelectedRow() < 0) {
            return;
        }
        int fila = jtblUsuarios.convertRowIndexToModel(jtblUsuarios.getSelectedRow());
        if (fila >= usuarios.size()) {
            return;
        }
        Usuario seleccionado = usuarios.get(fila);
        idUsuarioSeleccionado = seleccionado.getId();
        estudianteSeleccionado = null;
carreraEstudianteEditable = false;
if (Perfil.ESTUDIANTE.equals(seleccionado.getPerfil())) {
    estudianteSeleccionado = EstudianteDAO.buscarPorUsuarioId(seleccionado.getId());
    carreraEstudianteEditable = estudianteSeleccionado != null
            && !EstudianteDAO.tieneMatriculas(estudianteSeleccionado.getId());
}
        jtxtUsuario.setText(seleccionado.getUsername());
        jtxtNombre.setText(seleccionado.getNombre());
        jpwdClave.setText("");
        jcbxPerfil.setSelectedItem(seleccionado.getPerfil());
        jcbxCarrera.setSelectedIndex(0);
        if ((Perfil.COORDINADOR.equals(seleccionado.getPerfil())
        || Perfil.ESTUDIANTE.equals(seleccionado.getPerfil()))
        && seleccionado.getCarreraId() != null) {
            for (int i = 1; i < jcbxCarrera.getItemCount(); i++) {
                Carrera carrera = (Carrera) jcbxCarrera.getItemAt(i);
                if (carrera.getId() == seleccionado.getCarreraId()) {
                    jcbxCarrera.setSelectedIndex(i);
                    break;
                }
            }
        }
        jchkActivo.setSelected(seleccionado.isActivo());
        InformacionEditar();
    }

    private void habilitarInformacion(boolean habilitar) {
        jtxtUsuario.setEnabled(habilitar);
        jpwdClave.setEnabled(habilitar);
        jtxtNombre.setEnabled(habilitar);
        jcbxPerfil.setEnabled(habilitar);
        jchkActivo.setEnabled(habilitar);
        actualizarCarrera();
    }

    private void actualizarCarrera() {
    Object perfil = jcbxPerfil.getSelectedItem();
    boolean coordinador = Perfil.COORDINADOR.equals(perfil);
    boolean estudiante = Perfil.ESTUDIANTE.equals(perfil)
            && !nuevo && estudianteSeleccionado != null;
    boolean editable = coordinador || (estudiante && carreraEstudianteEditable);
    jcbxCarrera.setEnabled(tienePermiso() && jcbxPerfil.isEnabled() && editable);
    jcbxCarrera.setToolTipText(estudiante && !carreraEstudianteEditable
            ? "No se puede cambiar: el estudiante ya está matriculado en alguna materia" : null);
    if (!coordinador && !estudiante) {
        jcbxCarrera.setSelectedIndex(0);
    }
}

    private void InformacionInicio() {
        habilitarInformacion(false);
    }

    private void BotonesInicio() {
        jbtnNuevo.setEnabled(tienePermiso());
        jbtnGuardar.setEnabled(false);
        jbtnModificar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(false);
        jtblUsuarios.setEnabled(tienePermiso());
    }

    private void InformacionNuevo() {
        habilitarInformacion(true);
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(true);
        jbtnModificar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(true);
        jtblUsuarios.setEnabled(false);
        jtxtUsuario.requestFocus();
    }

    private void InformacionEditar() {
        habilitarInformacion(true);
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(false);
        jbtnModificar.setEnabled(true);
        jbtnEliminar.setEnabled(true);
        jbtnCancelar.setEnabled(true);
    }

    private void Limpiar() {
        idUsuarioSeleccionado = 0;
        estudianteSeleccionado = null;
carreraEstudianteEditable = false;
        jtblUsuarios.clearSelection();
        jtxtUsuario.setText("");
        jpwdClave.setText("");
        jtxtNombre.setText("");
        jcbxPerfil.setSelectedIndex(0);
        jcbxCarrera.setSelectedIndex(0);
        jchkActivo.setSelected(true);
    }

    private void Cancelar() {
        nuevo = false;
        Limpiar();
        InformacionInicio();
        BotonesInicio();
    }

    private boolean validarCampos() {
        if (!tienePermiso()) {
            JOptionPane.showMessageDialog(this, "Solo ADMIN puede administrar usuarios.");
            return false;
        }
        if (jtxtUsuario.getText().trim().isEmpty() || jtxtUsuario.getText().trim().length() > 30) {
            JOptionPane.showMessageDialog(this, "Ingrese un usuario de 1 a 30 caracteres.");
            jtxtUsuario.requestFocus();
            return false;
        }
        if (jtxtNombre.getText().trim().isEmpty() || jtxtNombre.getText().trim().length() > 80) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre de 1 a 80 caracteres.");
            jtxtNombre.requestFocus();
            return false;
        }
        char[] clave = jpwdClave.getPassword();
        boolean claveInvalida = clave.length > 50
                || (nuevo && new String(clave).trim().isEmpty());
        java.util.Arrays.fill(clave, '\0');
        if (claveInvalida) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña es obligatoria al crear y admite hasta 50 caracteres.");
            jpwdClave.requestFocus();
            return false;
        }
        Object perfil = jcbxPerfil.getSelectedItem();
        if (jcbxPerfil.getSelectedIndex() <= 0
                || !(Perfil.ADMIN.equals(perfil) || Perfil.COORDINADOR.equals(perfil)
                || Perfil.DOCENTE.equals(perfil) || Perfil.REGISTRO.equals(perfil)
                || Perfil.ESTUDIANTE.equals(perfil))) {
            JOptionPane.showMessageDialog(this, "Seleccione un perfil válido.");
            jcbxPerfil.requestFocus();
            return false;
        }
        if (Perfil.COORDINADOR.equals(perfil)
                && (jcbxCarrera.getSelectedIndex() <= 0
                || !(jcbxCarrera.getSelectedItem() instanceof Carrera)
                || ((Carrera) jcbxCarrera.getSelectedItem()).getId() <= 0)) {
            JOptionPane.showMessageDialog(this, "Seleccione una carrera para el coordinador.");
            jcbxCarrera.requestFocus();
            return false;
        }
        if (Perfil.ESTUDIANTE.equals(perfil) && jcbxCarrera.isEnabled()
        && (jcbxCarrera.getSelectedIndex() <= 0
        || !(jcbxCarrera.getSelectedItem() instanceof Carrera))) {
    JOptionPane.showMessageDialog(this, "Seleccione una carrera para el estudiante.");
    jcbxCarrera.requestFocus();
    return false;
}
        return true;
    }

    private boolean validarSeleccion() {
        if (!tienePermiso()) {
            JOptionPane.showMessageDialog(this, "Solo ADMIN puede administrar usuarios.");
            return false;
        }
        int fila = jtblUsuarios.getSelectedRow();
        if (nuevo || fila < 0 || idUsuarioSeleccionado <= 0
                || usuarios.get(jtblUsuarios.convertRowIndexToModel(fila)).getId() != idUsuarioSeleccionado) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.");
            jtblUsuarios.requestFocus();
            return false;
        }
        return true;
    }

    private Usuario obtenerDatos() {
        Usuario registrado = new Usuario();
        registrado.setId(idUsuarioSeleccionado);
        registrado.setUsername(jtxtUsuario.getText().trim());
        registrado.setNombre(jtxtNombre.getText().trim());
        registrado.setPerfil((String) jcbxPerfil.getSelectedItem());
        registrado.setActivo(jchkActivo.isSelected());
        if (Perfil.COORDINADOR.equals(registrado.getPerfil())) {
            registrado.setCarreraId(((Carrera) jcbxCarrera.getSelectedItem()).getId());
        } else {
            registrado.setCarreraId(null);
        }
        return registrado;
    }

    private void guardarUsuario(boolean modificar) {
        if (modificar) {
            if (!validarSeleccion()) {
                return;
            }
        } else if (!tienePermiso() || !nuevo) {
            return;
        }
        if (!validarCampos()) {
            return;
        }
        Usuario registrado = obtenerDatos();
        if (modificar && !cambiarCarreraEstudiante()) {
    return;
}
        char[] caracteres = jpwdClave.getPassword();
        String clave = new String(caracteres);
        java.util.Arrays.fill(caracteres, '\0');
        boolean resultado;
        if (modificar) {
            resultado = UsuarioDAO.actualizar(registrado, clave);
        } else {
            resultado = UsuarioDAO.insertar(registrado, clave);
        }
        if (resultado) {
            cargarUsuarios();
            Cancelar();
            JOptionPane.showMessageDialog(this, "Usuario " + (modificar ? "modificado." : "registrado."));
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el usuario. Revise los datos y el mensaje del DAO.");
        }
    }

    private boolean cambiarCarreraEstudiante() {
    if (estudianteSeleccionado == null || !carreraEstudianteEditable
            || !jcbxCarrera.isEnabled()
            || !Perfil.ESTUDIANTE.equals(jcbxPerfil.getSelectedItem())) {
        return true;
    }
    Carrera nueva = (Carrera) jcbxCarrera.getSelectedItem();
    if (nueva.getId() == estudianteSeleccionado.getCarreraId()) {
        return true;
    }
    int respuesta = JOptionPane.showConfirmDialog(this,
            "Se cambiará la carrera a \"" + nueva.getNombre() + "\" y se rehará el récord\n"
            + "académico del estudiante con las materias de esa carrera.\n¿Desea continuar?",
            "Cambiar carrera", JOptionPane.YES_NO_OPTION);
    if (respuesta != JOptionPane.YES_OPTION) {
        return false;
    }
    if (EstudianteDAO.cambiarCarrera(estudianteSeleccionado.getId(), nueva.getId())) {
        return true;
    }
    JOptionPane.showMessageDialog(this,
            "No se pudo cambiar la carrera. Es posible que el estudiante ya esté matriculado.");
    cargarUsuarios();
    Cancelar();
    return false;
}
    
    private void eliminarUsuario() {
        if (!validarSeleccion()) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar el usuario seleccionado?", "Eliminar usuario",
                JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        if (UsuarioDAO.eliminar(idUsuarioSeleccionado)) {
            cargarUsuarios();
            Cancelar();
            JOptionPane.showMessageDialog(this, "Usuario eliminado.");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el usuario.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlblUsuario = new javax.swing.JLabel();
        jtxtUsuario = new javax.swing.JTextField();
        jlblClave = new javax.swing.JLabel();
        jpwdClave = new javax.swing.JPasswordField();
        jlblNombre = new javax.swing.JLabel();
        jtxtNombre = new javax.swing.JTextField();
        jlblPerfil = new javax.swing.JLabel();
        jcbxPerfil = new javax.swing.JComboBox<>();
        jlblCarrera = new javax.swing.JLabel();
        jcbxCarrera = new javax.swing.JComboBox<>();
        jlblActivo = new javax.swing.JLabel();
        jchkActivo = new javax.swing.JCheckBox();
        jbtnNuevo = new javax.swing.JButton();
        jbtnGuardar = new javax.swing.JButton();
        jbtnModificar = new javax.swing.JButton();
        jbtnEliminar = new javax.swing.JButton();
        jbtnCancelar = new javax.swing.JButton();
        jscrUsuarios = new javax.swing.JScrollPane();
        jtblUsuarios = new javax.swing.JTable();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestión de usuarios");

        jlblUsuario.setText("Usuario:");
        jlblClave.setText("Contraseña:");
        jlblNombre.setText("Nombre:");
        jlblPerfil.setText("Perfil:");
        jcbxPerfil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione" }));
        jcbxPerfil.addActionListener(this::jcbxPerfilActionPerformed);
        jlblCarrera.setText("Carrera:");
        jcbxCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione" }));
        jlblActivo.setText("Estado:");
        jchkActivo.setText("Activo");
        jbtnNuevo.setText("Nuevo");
        jbtnNuevo.addActionListener(this::jbtnNuevoActionPerformed);
        jbtnGuardar.setText("Guardar");
        jbtnGuardar.addActionListener(this::jbtnGuardarActionPerformed);
        jbtnModificar.setText("Modificar");
        jbtnModificar.addActionListener(this::jbtnModificarActionPerformed);
        jbtnEliminar.setText("Eliminar");
        jbtnEliminar.addActionListener(this::jbtnEliminarActionPerformed);
        jbtnCancelar.setText("Cancelar");
        jbtnCancelar.addActionListener(this::jbtnCancelarActionPerformed);

        jtblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] { "ID", "Usuario", "Nombre", "Perfil", "Carrera", "Activo" }
        ));
        jscrUsuarios.setViewportView(jtblUsuarios);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblUsuario, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jtxtUsuario, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblClave, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jpwdClave, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblNombre, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jtxtNombre, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblPerfil, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jcbxPerfil, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblCarrera, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jcbxCarrera, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblActivo, 130, 130, 130)
                        .addGap(12)
                        .addComponent(jchkActivo, 250, 500, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbtnNuevo)
                        .addGap(8)
                        .addComponent(jbtnGuardar)
                        .addGap(8)
                        .addComponent(jbtnModificar)
                        .addGap(8)
                        .addComponent(jbtnEliminar)
                        .addGap(8)
                        .addComponent(jbtnCancelar))
                    .addComponent(jscrUsuarios, 500, 650, Short.MAX_VALUE))
                .addGap(12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblUsuario)
                    .addComponent(jtxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblClave)
                    .addComponent(jpwdClave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblNombre)
                    .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblPerfil)
                    .addComponent(jcbxPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblCarrera)
                    .addComponent(jcbxCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblActivo)
                    .addComponent(jchkActivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnNuevo)
                    .addComponent(jbtnGuardar)
                    .addComponent(jbtnModificar)
                    .addComponent(jbtnEliminar)
                    .addComponent(jbtnCancelar))
                .addGap(12)
                .addComponent(jscrUsuarios, 150, 220, Short.MAX_VALUE)
                .addGap(12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jcbxPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbxPerfilActionPerformed
        actualizarCarrera();
    }//GEN-LAST:event_jcbxPerfilActionPerformed

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        if (tienePermiso()) {
            nuevo = true;
            Limpiar();
            InformacionNuevo();
        }
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        guardarUsuario(false);
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jbtnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnModificarActionPerformed
        guardarUsuario(true);
    }//GEN-LAST:event_jbtnModificarActionPerformed

    private void jbtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEliminarActionPerformed
        eliminarUsuario();
    }//GEN-LAST:event_jbtnEliminarActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        Cancelar();
    }//GEN-LAST:event_jbtnCancelarActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FrmUsuarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jlblUsuario;
    private javax.swing.JTextField jtxtUsuario;
    private javax.swing.JLabel jlblClave;
    private javax.swing.JPasswordField jpwdClave;
    private javax.swing.JLabel jlblNombre;
    private javax.swing.JTextField jtxtNombre;
    private javax.swing.JLabel jlblPerfil;
    private javax.swing.JComboBox<Object> jcbxPerfil;
    private javax.swing.JLabel jlblCarrera;
    private javax.swing.JComboBox<Object> jcbxCarrera;
    private javax.swing.JLabel jlblActivo;
    private javax.swing.JCheckBox jchkActivo;
    private javax.swing.JButton jbtnNuevo;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JButton jbtnModificar;
    private javax.swing.JButton jbtnEliminar;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JScrollPane jscrUsuarios;
    private javax.swing.JTable jtblUsuarios;
    // End of variables declaration//GEN-END:variables
}
