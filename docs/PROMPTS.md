# Prompts para la IA

Cada quien usa la IA que prefiera. La IA no ve el proyecto, así que **hay que pegarle el contexto**: sin eso inventa nombres de clases y de columnas, y el código no compila.

El ingeniero califica que sea **sencillo y con lo visto en clase**, y pregunta lo que no enseñó. Por eso los prompts piden código simple, en el estilo de clase, y una explicación de cada método. **Lee el código, entiéndelo y pruébalo antes de subirlo**: tienes que poder explicarlo.

---

## 1. Cómo usarlos (en este orden)

1. Abre un **chat nuevo** en tu IA.
2. Pega el **bloque fijo** (sección 3), completo.
3. Pega los **archivos del contrato** (sección 2), cada uno con su nombre arriba, por ejemplo `// ===== dao/EstudianteDAO.java =====`.
4. Si tu tarea es una **ventana**, primero dibújala en el diseñador de NetBeans y escribe la lista de tus componentes (sección 4).
5. Pega **tu tarea** (sección 5). Pídele **una tarea a la vez**: "Haz solo la TAREA 1".
6. Copia el código a NetBeans y haz **Clean and Build**.
7. Si hay errores, usa la plantilla de la sección 6.
8. Antes de abrir el PR, pídele que te explique tu código (sección 6).

Si la IA propone **cambiar una clase del contrato** (un modelo, el nombre o los parámetros de un método de un DAO o controlador), **se ignora** y se avisa a Ali.

## 2. Archivos del contrato que se pegan (todos, los mismos)

Abre cada uno en NetBeans, copia todo su contenido y pégalo:

- `db/schema.sql`
- `db/Conexion.java`
- Todo el paquete `modelo`
- Todas las clases del paquete `dao`
- Todo el paquete `controlador`
- `util/Parametros.java` y `util/Fechas.java`

Son pocas líneas, y evita que la IA invente un método.

## 3. Bloque fijo (igual para todos)

```text
Soy estudiante de Ingeniería de Software y trabajo en equipo en un proyecto
universitario: un sistema de control de asistencia de escritorio con lector RFID
(el lector funciona como teclado: escribe el código de la tarjeta y pulsa Enter).

ENTORNO (respétalo exactamente):
- Java 25 con Swing. Proyecto Ant de NetBeans (NO Maven, NO Gradle).
- Base de datos MySQL (XAMPP) con JDBC puro.
- Arquitectura MVC con DAO. Paquetes: asistenciarfid.modelo, asistenciarfid.vista,
  asistenciarfid.controlador, asistenciarfid.dao, asistenciarfid.db, asistenciarfid.util.
- Los DAO son clases con métodos static, como en clase (por ejemplo EstudianteDAO.listar(null)).
  NO hay interfaces ni clases Impl. Los controladores también tienen métodos static
  (por ejemplo AsistenciaControlador.registrarPorCodigo(codigo, new java.util.Date())).
- Prohibido usar frameworks, Hibernate, Lombok o librerías externas.

ESTILO DE CLASE (el ingeniero califica que sea sencillo y con lo que enseñó):
- La conexión se obtiene así: Conexion cn = new Conexion(); Connection cc = cn.conectar();
  Si falla, conectar() muestra un JOptionPane y devuelve null: revisa eso antes de usarla.
- Consultas con PreparedStatement y ResultSet. Los errores se manejan con try/catch y
  JOptionPane.showMessageDialog(null, ex). La conexión se cierra con cc.close() al terminar.
- Las tablas (JTable) se llenan como en clase: la ventana pide la lista al DAO y arma el modelo.
  Ejemplo: String titulos[] = {"cedula","nombre"};
  DefaultTableModel modelo = new DefaultTableModel(null, titulos);
  List<Estudiante> lista = EstudianteDAO.listar(null);
  for (Estudiante est : lista) { modelo.addRow(new Object[]{est.getCedula(), est.getNombre()}); }
  jtbEstudiantes.setModel(modelo);
- Al seleccionar una fila se llenan las cajas de texto, como en clase:
  jtbXxx.getSelectionModel().addListSelectionListener(new ListSelectionListener(){ ... }) y
  getValueAt(fila, columna).
- Para guardar: se arma el objeto con los datos de las cajas (con los setters), se llama al DAO
  (EstudianteDAO.insertar(est)), se muestra un JOptionPane con el resultado y se recarga la tabla.
- FECHAS Y HORAS: se usa java.sql.Date y java.sql.Time (NO java.time, NO LocalDate).
  Se guardan con ps.setDate(...) y ps.setTime(...) y se leen con rs.getDate(...) y rs.getTime(...).
  Para convertir texto: java.sql.Date.valueOf("2026-11-09") y java.sql.Time.valueOf("16:00:00").
  La clase asistenciarfid.util.Fechas ya tiene ayudas (día de la semana, minutos del día).
- Perfiles y estados son constantes String (clases Perfil, Estado, EstadoMateria).
  Se comparan con .equals(...). NO uses enums ni records.
- NO uses streams, lambdas complejas, var, try-with-resources ni genéricos avanzados.
- Código de nivel estudiante universitario: nombres en español, comentarios breves y
  naturales (no excesivos).
- NO uses transacciones (setAutoCommit, commit, rollback): todo se hace con consultas simples,
  una tras otra, como en clase. Si un paso falla, se borra lo que ya se había guardado.

VENTANAS (diseñador de NetBeans):
- Todas las ventanas son JFrame Form (NO uses JInternalFrame, JDialog, JDesktopPane ni
  CardLayout). Las ventanas del sistema se abren desde el menú de la ventana principal con
  new FrmXxx(usuario).setVisible(true).
- Cada FrmXxx tiene dos constructores: el que genera NetBeans y otro que recibe el Usuario, que llama a initComponents(), guarda el usuario, y luego setLocationRelativeTo(null) y setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE) (así cerrar la ventana no cierra todo el programa).
- La ventana la dibujo YO arrastrando componentes. TÚ NO escribes initComponents(), ni el
  bloque gris "Generated Code", ni el diseño de la ventana.
- Te daré la lista de mis componentes con su nombre y tipo. Usa EXACTAMENTE esos nombres.
- Tú solo me das: (1) los métodos que van FUERA del bloque gris (cargarXxx(), limpiar(),
  validar()...), y (2) qué línea debo escribir dentro de cada evento (por ejemplo dentro de
  jbtnGuardarActionPerformed).

REGLAS DE TRABAJO:
- Las clases que te pego abajo YA EXISTEN y NO puedes modificarlas, renombrarlas ni
  cambiar sus firmas. Si necesitas algo que no existe, dímelo en lugar de inventarlo.
- Entrégame cada archivo COMPLETO (con la línea package y los imports), un archivo por
  bloque de código, con su nombre. Nunca fragmentos sueltos.
- Después de cada archivo, explícame en pocas líneas qué hace cada método.
- Hazme UNA tarea a la vez y espera a que te pida la siguiente.

ARCHIVOS DEL PROYECTO (ya existen):
[PEGAR AQUÍ los archivos del contrato]

MI TAREA:
[PEGAR AQUÍ la tarea de mi persona]
```

## 4. Plantilla para describir una ventana

Después de dibujar tu ventana en el diseñador, dale a cada componente un nombre con prefijo (clic derecho → *Change Variable Name*): `jtxt` (cajas de texto), `jbtn` (botones), `jtb` (tablas), `jcmb` (combos), `jlbl` (etiquetas), `jchk` (casillas), `jspn` (spinner), `jpwd` (clave).

Y pega algo así, junto a tu tarea:

```text
La ventana se llama FrmEstudiantes (JFrame Form). Su constructor recibe el Usuario
que inició sesión: public FrmEstudiantes(Usuario usuario).

Mis componentes:
- jtxtCodigo (JTextField): código RFID, aquí se pasa la tarjeta
- jtxtNombre (JTextField)
- jcmbCarrera (JComboBox)
- jspnNivel (JSpinner)
- jtbEstudiantes (JTable): lista de estudiantes
- jbtnGuardar (JButton), jbtnLimpiar (JButton)
```

## 5. Tareas por persona

Cada tarea se pega en `MI TAREA:` del bloque fijo. Si una tarea sale muy larga, la IA hace un punto a la vez.

### Ali

```text
Haz estas tareas, una por una, en este orden:

TAREA 1 - UsuarioDAO (ya existe con el cuerpo pendiente: reemplaza el cuerpo de cada método, sin cambiar su nombre ni sus parámetros):
- validar(username, passwordPlano): WHERE username=? AND clave=? AND activo=1.
  Devuelve un Usuario SIN la contraseña, o null si no coincide.
- listar(), listarPorPerfil(perfil).
- insertar(u, passwordPlano): la clave se guarda tal cual en la columna clave.
- actualizar(u, passwordPlano): si passwordPlano es null o vacío, NO cambia la clave.
- eliminar(id).

TAREA 2 - VentanaLogin (JFrame con el diseñador). Componentes: jtxtUsuario, jpwdClave
(JPasswordField), jbtnIngresar. Método ingresar(): valida que no haya campos vacíos, llama a
UsuarioDAO.validar; si da un usuario abre new VentanaPrincipal(usuario).setVisible(true)
y cierra el login con dispose(); si no, muestra un JOptionPane.

TAREA 3 - VentanaPrincipal (JFrame con una barra de menú, sin JDesktopPane). Su constructor
recibe el Usuario. Método configurarMenuSegunPerfil() que muestra u oculta las opciones:
- ADMIN: Usuarios, Estudiantes, Periodos, Asignaturas, Grupos, Horarios, Notas,
  Registro de asistencia, Reportes.
- COORDINADOR: Estudiantes, Asignaturas, Grupos, Horarios, Reportes.
- DOCENTE: Notas, Reportes.
- REGISTRO: solo Registro de asistencia.
- ESTUDIANTE: solo Matrícula.
Cada opción del menú abre su ventana con new FrmXxx(usuario).setVisible(true). Incluye
"Cerrar sesión" (hace dispose() y abre de nuevo el login) y "Salir".
Las ventanas FrmUsuarios, FrmEstudiantes, FrmAsignaturas, FrmGrupos, FrmHorarios, FrmPeriodos,
FrmNotas, FrmRegistro, FrmReportes y FrmMatricula ya existen vacías en el paquete vista.

TAREA 4 - FrmRegistro (JFrame, constructor con el Usuario), la pantalla de lectura de
tarjeta. Dibuja la ventana en el diseñador y dame la lista de componentes. Funciones:
- Una caja de texto grande que SIEMPRE mantiene el foco (llama a requestFocus() al terminar
  cada lectura y cuando la ventana se activa).
- Al pulsar Enter, llama a AsistenciaControlador.registrarPorCodigo(codigo, new
  java.util.Date()), limpia la caja y muestra el resultado en letras grandes: nombre del
  estudiante, asignatura, paralelo y hora.
- El panel del resultado cambia de color: verde si REGISTRADO, amarillo si YA_REGISTRADO, rojo
  para NO_ENCONTRADO, SIN_CLASES_HOY y FUERA_DE_HORARIO. El mensaje queda en pantalla hasta la
  siguiente lectura.
- Modo prueba: una casilla "Simular fecha y hora" y un campo con formato yyyy-MM-dd HH:mm. Si la
  casilla está marcada, se pasa esa fecha (convertida con SimpleDateFormat en un try/catch) en
  lugar de la actual. Si el formato es inválido, muestra un mensaje.

TAREA 5 - FrmPeriodos (JFrame, constructor con el Usuario). Dibuja la ventana en el
diseñador y dame la lista de componentes. Tabla de periodos y formulario (nombre, fecha de
inicio y de fin como texto yyyy-MM-dd, que se convierte con java.sql.Date.valueOf). Botones:
Crear, Modificar, Activar periodo, Abrir matrícula, Cerrar matrícula y Cerrar periodo. Cerrar
periodo primero llama a CierrePeriodoControlador.notasFaltantes(): si hay faltantes muestra la
lista y no continúa; si no, pide confirmación, llama a cerrarPeriodo() y muestra la lista de
estudiantes "por revisar".

TAREA 6 - AsistenciaRFID (clase con main): abre VentanaLogin, como en el ejemplo de clase.
```

### Damaris

```text
Haz estas tareas, una por una, en este orden:

TAREA 1 - HorarioDAO, AsistenciaDAO y MatriculaDAO (ya existe con el cuerpo pendiente: reemplaza el cuerpo de cada método, sin cambiar su nombre ni sus parámetros). Usa los nombres de columnas exactos de schema.sql.
- HorarioDAO.listarDeEstudianteEnDia(estudianteId, diaSemana): horarios de los grupos donde el
  estudiante está matriculado, del periodo con activo=1, en ese día. Cada Horario debe traer
  asignaturaNombre y paralelo (con JOIN).
- MatriculaDAO: los listados traen también el nombre del estudiante, la asignatura y el
  paralelo, y la nota (null si está vacía).

TAREA 2 - AsistenciaControlador.registrarPorCodigo(String codigo, java.util.Date ahora).
Algoritmo:
a) Busca el estudiante por código. Si no existe: Estado.NO_ENCONTRADO.
b) Trae sus horarios de hoy con Fechas.diaSemana(ahora). Si no hay: Estado.SIN_CLASES_HOY.
c) Elige el horario donde minutosAhora >= inicio y minutosAhora <= inicio +
   Parametros.TOLERANCIA_MINUTOS, usando Fechas.minutosDelDia(ahora) y
   Fechas.minutosDelDia(horario.getHoraInicio()). Nada antes del inicio. Si ninguno cumple:
   Estado.FUERA_DE_HORARIO.
d) Si ya existe asistencia (estudiante, horario, fecha): Estado.YA_REGISTRADO.
e) Si no, registra y devuelve Estado.REGISTRADO. Para guardar usa
   new java.sql.Date(ahora.getTime()) y new java.sql.Time(ahora.getTime()).
Devuelve un ResultadoRegistro con estado, estudiante, horario y mensaje.

TAREA 3 - MatriculaControlador.matricular(estudianteId, grupoId): devuelve null si todo está
bien o el mensaje del primer error, revisando en este orden:
1. El periodo debe tener la matrícula abierta.
2. El grupo debe ser del periodo activo.
3. La asignatura debe ser de la carrera del estudiante, de nivel menor o igual al suyo, y su
   estado en registro_academico debe ser AUN_NO_CURSA o REPROBADA.
4. No estar ya matriculado en esa asignatura en el periodo.
5. Máximo Parametros.MAX_MATERIAS (5) matrículas en el periodo.
6. Que ningún horario del grupo choque (mismo día y horas que se cruzan) con los de sus otros grupos.
También gruposDisponibles(Estudiante): los grupos del periodo activo que cumplen 3 y 4.

TAREA 4 - CierrePeriodoControlador. notasFaltantes() devuelve las matrículas del periodo activo
sin nota. cerrarPeriodo() hace los cambios con consultas simples, una tras otra (SIN transacciones):
- Primero revisa notasFaltantes(): si falta alguna nota, no cambia nada y devuelve null.
- Recorre los grupos del periodo activo (GrupoDAO.listar(null, null)) y las matrículas de cada
  grupo (MatriculaDAO.listarPorGrupo). Si nota >= Parametros.NOTA_APROBACION, el
  registro_academico de esa asignatura pasa a APROBADA; si no, a REPROBADA
  (RegistroAcademicoDAO.actualizarEstado).
- Para cada estudiante que tenía matrículas: si aprobó TODO lo que matriculó, su nivel sube 1
  (sin pasar del nivel más alto de las asignaturas de su carrera, que se saca con
  AsignaturaDAO.listar(carreraId)); se guarda con EstudianteDAO.buscarPorId y
  EstudianteDAO.actualizar. Si reprobó algo, su nivel no cambia y se agrega a la lista
  "por revisar" que devuelve el método.
- Al final de todo, el periodo queda con activo=0 y matricula_abierta=0
  (PeriodoDAO.obtenerActivo y PeriodoDAO.actualizar).
Si algo falla a la mitad, muestra el error con un JOptionPane; el admin corrige el nivel desde
FrmEstudiantes.

TAREA 5 - PruebaLogica (paquete util, con main). Con los datos de prueba de schema.sql, ejecuta
estos casos y muestra OK o FALLA comparando con lo esperado. Las horas se crean con
Fechas.crear(2026, 11, 9, 16, 10) (lunes 9 de noviembre, 16:10):
María 08:10 REGISTRADO; María 08:12 YA_REGISTRADO; María 15:55 FUERA_DE_HORARIO;
María 16:15 REGISTRADO; Luis 16:16 FUERA_DE_HORARIO; Luis 08:05 FUERA_DE_HORARIO;
código 9999999999 NO_ENCONTRADO; María el martes 2026-11-10 SIN_CLASES_HOY.
```

### Evelyn

```text
Haz estas tareas, una por una, en este orden:

TAREA 1 - EstudianteDAO y RegistroAcademicoDAO (ya existe con el cuerpo pendiente: reemplaza el cuerpo de cada método, sin cambiar su nombre ni sus parámetros).
- Los listados reciben carreraId como Integer: si es null no filtran por carrera.
- RegistroAcademicoDAO.generarParaCarrera(carreraId, nivelInicio) NO guarda nada: devuelve la
  lista de RegistroAcademico de las asignaturas de esa carrera, con estado APROBADA si el nivel
  de la asignatura es menor al nivel de inicio, y AUN_NO_CURSA en los demás casos. Cada uno trae
  asignaturaNombre y asignaturaNivel.
- EstudianteDAO.buscarPorId(id): devuelve el Estudiante o null.
- EstudianteDAO.insertarConUsuario(...) guarda con consultas simples, una tras otra (SIN
  transacciones), en este orden:
  (a) antes de guardar nada, revisa que el username y el código RFID no existan ya; si existen,
      devuelve false;
  (b) INSERT del usuario con perfil Perfil.ESTUDIANTE (la clave va tal cual en la columna clave) y
      luego SELECT id FROM usuario WHERE username=? para saber su id;
  (c) INSERT del estudiante con ese usuario_id y luego SELECT id FROM estudiante WHERE
      codigo_rfid=? para saber su id;
  (d) INSERT de cada fila de registro_academico de la lista (con un ciclo for).
  Si un paso falla, borra lo que ya se había guardado (DELETE del estudiante y luego del
  usuario) y devuelve false.
- EstudianteDAO.eliminar(id) también elimina su usuario.

TAREA 2 - FrmEstudiantes (JFrame, constructor con el Usuario). Dibuja la ventana en el
diseñador y dame la lista de componentes. Funciones:
- El campo del código RFID es donde se pasa la tarjeta: el lector escribe y pulsa Enter. Al
  pulsar Enter se valida que no esté vacío ni repetido (existeCodigo) y se pasa el foco al
  siguiente campo.
- Al elegir carrera y nivel de inicio, se llena una segunda tabla con las materias generadas
  (generarParaCarrera). El estado de cada fila se puede editar con un combo (APROBADA,
  REPROBADA, AUN_NO_CURSA) para que el admin lo corrija antes de guardar.
- Guardar usa insertarConUsuario con esa lista.
- Modificar carga al estudiante seleccionado, con su tabla de materias (listarPorEstudiante), y
  guarda los cambios de nivel y de estados (actualizar y actualizarEstado).
- Eliminar con confirmación (JOptionPane.showConfirmDialog).
- Validaciones: campos obligatorios, correo con formato válido, código sin espacios.
- Si el usuario es COORDINADOR, solo ve y registra estudiantes de su carrera
  (usuario.getCarreraId()) y el combo de carrera queda bloqueado.

TAREA 3 - FrmMatricula (JFrame, constructor con el Usuario), la vista del ESTUDIANTE.
Dibuja la ventana en el diseñador y dame la lista de componentes. Funciones:
- Al abrirse busca el Estudiante del usuario que inició sesión (buscarPorUsuarioId) y muestra su
  nombre, su nivel y un contador "Matriculadas: X de 5" (MatriculaDAO.contarEnPeriodoActivo).
- Si el periodo no tiene la matrícula abierta (PeriodoDAO.obtenerActivo), muestra un aviso y
  bloquea los botones.
- Tabla 1: grupos disponibles (MatriculaControlador.gruposDisponibles), con asignatura, nivel y
  paralelo. Botón Matricular: llama a MatriculaControlador.matricular y muestra con JOptionPane
  el mensaje de error si lo hay (null significa que salió bien).
- Tabla 2: sus materias matriculadas en el periodo (listarPorEstudiante), con botón Anular.
- Después de cada acción se actualizan las dos tablas y el contador.
```

### Tatiana

```text
Haz esta tarea:

TAREA 1 - FrmAsignaturas (JFrame, constructor con el Usuario). Dibuja la ventana en el
diseñador y dame la lista de componentes. Tabla y formulario (nombre, nivel con JSpinner de 1 a
10, carrera en combo) con Agregar, Modificar y Eliminar. Valida que el nombre no esté vacío. El
COORDINADOR solo ve su carrera (usuario.getCarreraId()).
```

### Brayan

```text
Haz estas tareas, una por una, en este orden. Son sencillas: una consulta por método.

TAREA 1 - CarreraDAO, AsignaturaDAO y PeriodoDAO (ya existe con el cuerpo pendiente: reemplaza el cuerpo de cada método, sin cambiar su nombre ni sus parámetros).
- CarreraDAO: listar() y buscarPorId(id).
- AsignaturaDAO: listar(carreraId) recibe un Integer: si es null trae todas las asignaturas; si no,
  solo las de esa carrera. También buscarPorId, insertar, actualizar y eliminar.
- PeriodoDAO: obtenerActivo() (WHERE activo=1), listar(), insertar, actualizar,
  cambiarMatricula(id, abierta) y activar(id): deja solo ese periodo con activo=1 y los demás
  con 0 (dos UPDATE seguidos).

TAREA 2 - FrmUsuarios (JFrame, constructor con el Usuario): tabla de usuarios y formulario con usuario, clave,
nombre, perfil (combo con las constantes de Perfil), carrera (combo, solo se habilita si el
perfil es COORDINADOR) y activo (casilla). Botones Nuevo, Guardar, Modificar, Eliminar.
Las carreras salen de CarreraDAO. Valida campos obligatorios.

TAREA 3 - FrmHorarios (JFrame, constructor con el Usuario). Dibuja la ventana en el diseñador y
dame la lista de componentes. Arriba un combo con los grupos del periodo activo (GrupoDAO.listar;
el COORDINADOR solo ve los de su carrera con usuario.getCarreraId()). Al elegir un grupo se llena
la tabla con sus horarios (HorarioDAO.listarPorGrupo). Formulario: día (combo de Lunes a Domingo
que guarda 1 a 7), hora de inicio y fin (texto "HH:mm", se convierte con
java.sql.Time.valueOf(texto + ":00") dentro de un try/catch) y aula. Botones Agregar, Modificar y
Eliminar. Valida que la hora de inicio sea menor que la de fin. Este horario es el "evento" que
pide la guía del ingeniero.
```

### Victoria

```text
Haz estas tareas, una por una, en este orden:

TAREA 1 - GrupoDAO (ya existe con el cuerpo pendiente: reemplaza el cuerpo de cada método, sin cambiar su nombre ni sus parámetros). Los listados de grupos reciben carreraId y
docenteId como Integer: si es null no filtran. Los grupos se listan del periodo activo y traen
asignaturaNombre, asignaturaNivel y el nombre del docente (con JOIN). insertar usa el periodo
activo (PeriodoDAO.obtenerActivo) como periodo_id.

TAREA 2 - FrmGrupos (JFrame, constructor con el Usuario). Dibuja la ventana en el diseñador y dame
la lista de componentes. Tabla de grupos del periodo activo (GrupoDAO.listar) y formulario:
asignatura (combo, de AsignaturaDAO.listar), paralelo (texto corto) y docente (combo de usuarios
con perfil DOCENTE, UsuarioDAO.listarPorPerfil). Botones Agregar, Modificar y Eliminar. Valida que
el paralelo no esté vacío y que no se repita la misma asignatura con el mismo paralelo. El
COORDINADOR solo ve y crea grupos de las asignaturas de su carrera (usuario.getCarreraId()).

TAREA 3 - FrmNotas (JFrame, constructor con el Usuario). Dibuja la ventana en el
diseñador y dame la lista de componentes. El docente elige uno de sus grupos en un combo
(GrupoDAO.listar(null, usuario.getId()) si es DOCENTE; todos los grupos si es ADMIN). Se
llena una tabla con los estudiantes matriculados (MatriculaDAO.listarPorGrupo) y su nota. La
columna Nota es editable. Botón Guardar: valida que cada nota esté vacía o entre 0 y 10, y llama
a MatriculaDAO.actualizarNota (nota null si está vacía). Guarda una lista paralela de las
matrículas para saber a qué estudiante corresponde cada fila.

TAREA 4 - FrmReportes (JFrame, constructor con el Usuario). Dibuja la ventana en el
diseñador y dame la lista de componentes. Tiene un combo de grupos (el DOCENTE ve los suyos, el
COORDINADOR los de su carrera con GrupoDAO.listar(usuario.getCarreraId(), null), el ADMIN
todos), una caja de fecha (texto yyyy-MM-dd, con la de hoy por defecto) y dos botones: "Asistencia
de la clase" y "Total por estudiante". Cada botón abre su reporte de iReport. Este es el código
que hicimos en clase para abrir un reporte; adáptalo (nombre del archivo, parámetros
P_GRUPO_ID y P_FECHA como String, y una RUTA RELATIVA como "reportes/asistencia_clase.jrxml",
nunca una ruta de C:\):

[PEGAR AQUÍ el método generarReporte() del ejemplo de clase]

Si Jasper falla por incompatibilidad con Java 25, no cambies de librería: dime el error.
```

(Los dos reportes se diseñan en iReport, sin IA. Las consultas SQL están en [REPARTO.md](REPARTO.md).)

## 6. Plantillas para cuando algo falla

**Error al compilar** (en el mismo chat):

```text
Al compilar me sale este error. Corrige SOLO ese problema, sin cambiar nada de las clases
existentes, y devuélveme el archivo completo.
[PEGAR EL ERROR TAL COMO SALE]
```

**Para entender tu código** (antes de abrir el PR):

```text
Explícame este código como si tuviera que exponerlo al ingeniero: qué hace cada método, qué
consulta SQL ejecuta y por qué. Dime también qué pregunta me podría hacer sobre él y cómo
responderla con palabras sencillas.
```

**Si la IA usa algo que no vimos en clase:**

```text
Reescribe ese método usando solo lo que vimos en clase: PreparedStatement, ResultSet,
try/catch con JOptionPane, java.sql.Date, java.sql.Time y DefaultTableModel. Sin streams, sin
lambdas, sin var y sin try-with-resources.
```
