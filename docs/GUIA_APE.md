# Guía APE — Control de asistencia con lector RFID

## 1. Identificación

- **Institución:** Universidad Técnica de Ambato.
- **Facultad:** Ingeniería en Sistemas, Electrónica e Industrial (FISEI).
- **Carrera:** Software.
- **Asignatura:** Computación Visual.
- **Proyecto:** Control de asistencia con lector RFID.
- **Responsable de esta documentación:** Bryan (Brayan en el reparto del repositorio).
- **Equipo:** Ali, Damaris, Evelyn, Tatiana, Brayan y Victoria.
- **Fecha de verificación:** 16 de noviembre de 2026.
- **Rama:** `feature/usuarios-horarios-documentacion`.

Esta guía desarrolla los resultados, conclusiones y recomendaciones solicitados en
[REPARTO.md](REPARTO.md), e identifica los aportes asignados a todos los integrantes.
El formato institucional original de la guía del docente no está adjunto en el
repositorio; cualquier requisito adicional de esa plantilla debe revisarse antes
de la entrega. No existía otra guía APE en `docs/` al preparar este documento.

## 2. Objetivo general

Desarrollar una aplicación de escritorio que permita organizar usuarios y clases
semanales, y registrar la asistencia de estudiantes mediante un lector RFID USB.
La administración de usuarios y horarios utiliza las clases de acceso a datos
existentes y conserva los permisos definidos por el equipo.

El horario semanal constituye el **evento** de la guía: pertenece a un grupo y
contiene día, hora de inicio, hora de finalización y aula. Su administración incluye
consulta, registro, modificación, eliminación y actualización de la tabla.

## 3. Tecnologías

| Elemento | Tecnología del proyecto |
|---|---|
| Lenguaje y plataforma | Java 25 |
| Desarrollo visual | Apache NetBeans GUI Builder |
| Construcción | Apache Ant, mediante `build.xml` |
| Interfaz | Swing, `JFrame`, `JDesktopPane` y `JInternalFrame` |
| Persistencia | JDBC y MySQL, con XAMPP como entorno de referencia |
| Organización | MVC con DAO |
| Reportes previstos por el equipo | iReport y bibliotecas JasperReports existentes |
| Control de versiones | Git |

No se añadieron dependencias, bibliotecas ni configuraciones. Para esta
verificación se utilizó JDK 25.0.4.1 y el Apache Ant incluido en NetBeans 22.
La apertura en la pestaña Design de NetBeans permanece pendiente.

## 4. Estructura del sistema

```text
src/asistenciarfid/
  AsistenciaRFID.java   Inicio: abre VentanaLogin
  modelo/              Objetos de datos y constantes de perfiles
  vista/               Login, ventana principal y formularios internos
  dao/                 Consultas y operaciones JDBC
  controlador/         Reglas de asistencia, matrícula y cierre
  db/                  Conexion
  util/                Parámetros y ayudas para fechas
src/plugins/           Dependencias existentes
db/schema.sql          Estructura y datos de demostración
docs/                  Reparto, prompts y esta guía
```

Los formularios reciben al usuario que inició sesión. `VentanaPrincipal` abre
`new FrmUsuarios(usuario)` y `new FrmHorarios(usuario)` dentro del escritorio.
Estos contratos se conservaron. Las vistas llaman a los DAO; no contienen SQL ni
crean conexiones JDBC.

## 5. Módulos y aportes del equipo

La siguiente tabla resume las responsabilidades documentadas en el reparto.
No constituye una certificación de que todos los módulos hayan sido probados.

| Integrante | Aporte asignado | Alcance de esta entrega |
|---|---|---|
| Ali | Usuarios DAO, login, principal, periodos, lectura RFID e integración | Se reutilizan UsuarioDAO y los accesos existentes del menú |
| Damaris | HorarioDAO, asistencia, matrícula y controladores | Se reutiliza HorarioDAO |
| Evelyn | Estudiantes y matrícula | Módulos externos a este cambio |
| Tatiana | Asignaturas y consulta del estudiante | Formularios usados como referencia de estilo |
| Brayan | CarreraDAO, AsignaturaDAO, PeriodoDAO, usuarios, horarios, guía y capturas | Dos formularios completos y guía; DAO previos sin cambios; capturas pendientes |
| Victoria | GrupoDAO, grupos, notas y reportes | Se reutiliza GrupoDAO y se toma FrmGrupos como referencia |

En el código actual existen login, menú por perfil, administración de estudiantes,
asignaturas y grupos, lectura RFID, matrícula y consulta académica. Sus pruebas
integrales corresponden a cada responsable y a la integración del equipo.
`FrmPeriodos`, `FrmNotas` y `FrmReportes` aún contienen formularios vacíos en
esta revisión; `ReporteControlador` conserva un cuerpo pendiente. No se presentan
como funcionalidades terminadas ni se modificaron.

## 6. Preparación y ejecución

1. Abrir este proyecto Ant desde **File → Open Project** en NetBeans.
2. Seleccionar JDK 25 en las propiedades del proyecto si el entorno todavía no lo usa.
3. Preparar una instancia **local y descartable** de MySQL para la demostración.
   Importar `db/schema.sql` solamente en esa instancia. El script utiliza la base
   `asistencia_rfid`; no ejecutarlo sobre una base compartida o con información real.
4. Revisar la configuración existente de `Conexion.java`: apunta a
   `jdbc:mysql://localhost/asistencia_rfid`. No fue modificada por esta entrega.
5. Ejecutar **Clean and Build** y comprobar el mensaje `BUILD SUCCESSFUL`.
6. Ejecutar **Run Project**. El inicio normal es `AsistenciaRFID`, que abre el login.
7. Utilizar las cuentas de demostración documentadas en el
   [README](../README.md#usuarios-de-prueba). Las credenciales no se incluyen en las capturas.
8. Desde el menú Administración abrir **Usuarios** o **Horarios**, según el perfil.

La alternativa por consola, con JDK 25 y Ant configurados, es:

```text
ant clean jar
java -jar dist/AsistenciaRFID.jar
```

Ejecutar directamente el `main` de estos formularios abre una vista sin sesión:
sus controles de administración estarán bloqueados. Para trabajar con permisos,
entrar desde el login y el menú de la aplicación.

Los grupos deben existir en el periodo activo para administrar horarios. La lista
proviene de `GrupoDAO.listar`, cuya consulta filtra `p.activo = 1`.
Estos dos formularios no necesitan invocar `PeriodoDAO.obtenerActivo()`.

## 7. Uso de la gestión de usuarios

Solo el perfil **ADMIN** puede consultar y administrar este formulario. La
restricción se comprueba al inicializar la ventana y antes de las operaciones.

### Registrar

1. Abrir Usuarios. Se muestra una tabla con ID, usuario, nombre, perfil, carrera y activo.
2. Pulsar **Nuevo** para habilitar los campos.
3. Ingresar usuario, contraseña y nombre.
4. Seleccionar uno de los perfiles de `Perfil.java`.
5. Para COORDINADOR, elegir una carrera. Para los demás perfiles, la carrera se
   limpia y queda deshabilitada.
6. Marcar o desmarcar **Activo** y pulsar **Guardar**.

Si el DAO devuelve éxito, se recarga la tabla, se limpian los campos y se restauran
los botones. Si devuelve `false`, se informa del fallo y se conservan los datos
para corregirlos. La tabla nunca muestra contraseñas.

### Modificar

Seleccionar una fila carga sus datos y conserva el ID del usuario. El campo de
contraseña aparece vacío: dejarlo así conserva la clave anterior, conforme al
contrato de `UsuarioDAO.actualizar(usuario, clave)`. Para cambiarla, escribir una
nueva antes de pulsar **Modificar**.

### Eliminar y cancelar

**Eliminar** requiere una fila seleccionada y confirmación. Si se responde No o se
cierra la confirmación, no se solicita la eliminación al DAO. **Cancelar** limpia el
formulario y restaura el estado inicial. La eliminación puede ser rechazada por las
relaciones de la base de datos; el DAO muestra el error y la vista no anuncia éxito.

Crear un usuario con perfil ESTUDIANTE en esta pantalla crea únicamente la cuenta.
El registro académico y los datos del estudiante pertenecen al módulo Estudiantes.

## 8. Uso de la gestión de horarios

**ADMIN** ve todos los grupos permitidos del periodo activo. **COORDINADOR** ve
únicamente los de su carrera. Un coordinador sin carrera y los demás perfiles
tienen bloqueada la gestión.

1. Abrir Horarios y seleccionar un grupo. La tabla presenta día, inicio, fin y aula.
2. Pulsar **Nuevo**.
3. Elegir el día de lunes a domingo; internamente se guarda un entero del 1 al 7.
4. Escribir las horas en formato `HH:mm`, por ejemplo `08:00` y `10:00`.
5. Ingresar el aula si corresponde y pulsar **Guardar**.
6. Para modificar, seleccionar una fila, editar sus datos y pulsar **Modificar**.
7. Para eliminar, seleccionar una fila y confirmar **Eliminar**.

Durante el registro o la edición, el grupo queda fijo. **Cancelar** vuelve a
habilitar su selección y limpia los campos. Después de guardar, modificar o
eliminar con éxito se recargan los horarios del grupo y se restauran los botones.

Antes de iniciar un registro, consultar horarios o escribir cambios, la ventana
vuelve a comprobar el grupo contra `GrupoDAO.listar(carreraId, null)`. Si dejó de
estar permitido, actualiza la lista y rechaza la operación. El horario seleccionado
también debe pertenecer al grupo actual.

El aula es opcional porque `schema.sql` permite `NULL`; una entrada vacía se
envía como `null`. Si se escribe, admite hasta 20 caracteres.

## 9. Validaciones y estados

| Módulo | Validación |
|---|---|
| Usuarios | Usuario obligatorio, hasta 30 caracteres |
| Usuarios | Nombre obligatorio, hasta 80 caracteres |
| Usuarios | Contraseña obligatoria al crear, hasta 50 caracteres |
| Usuarios | Contraseña vacía al editar conserva la existente |
| Usuarios | Perfil válido; carrera válida obligatoria para COORDINADOR |
| Usuarios | Selección e ID válidos al modificar o eliminar |
| Horarios | Perfil permitido y grupo del periodo activo autorizado |
| Horarios | Día seleccionado entre 1 y 7 |
| Horarios | Horas con dos dígitos de hora y minuto, entre 00:00 y 23:59 |
| Horarios | Hora inicial estrictamente menor que la final |
| Horarios | Aula opcional de hasta 20 caracteres |
| Horarios | Selección, ID y pertenencia al grupo al modificar o eliminar |
| Ambos | Mensajes de validación y foco en el campo que debe corregirse |
| Ambos | Confirmación antes de eliminar; tablas sin editor de celdas |

Las horas se validan antes de convertirlas mediante
`Time.valueOf(texto + ":00")`, dentro de bloques `try/catch`.
La validación previa evita que horas fuera de rango se normalicen silenciosamente.

El flujo mantiene los métodos `InformacionInicio`, `BotonesInicio`,
`InformacionNuevo`, `InformacionEditar`, `Cancelar` y `Limpiar`:

- Inicio: campos bloqueados y Nuevo disponible si el perfil y el grupo lo permiten.
- Nuevo: campos habilitados, Guardar y Cancelar disponibles, tabla bloqueada.
- Selección: campos cargados, Modificar, Eliminar y Cancelar disponibles.
- Cancelar o éxito: limpieza y retorno al estado inicial.

Las restricciones UNIQUE y las claves foráneas siguen a cargo de MySQL y los DAO
existentes. No se añadió una regla de prohibición de solapamientos al administrador
de horarios: esa regla de matrícula pertenece al controlador correspondiente.

## 10. DAO reutilizados

| DAO | Métodos utilizados | Uso |
|---|---|---|
| UsuarioDAO | `listar()` | Lista de usuarios sin exponer la clave |
| UsuarioDAO | `insertar(Usuario, String)` | Registro |
| UsuarioDAO | `actualizar(Usuario, String)` | Edición; clave vacía conserva la anterior |
| UsuarioDAO | `eliminar(int)` | Eliminación confirmada |
| CarreraDAO | `listar()` | Opciones de carrera |
| GrupoDAO | `listar(Integer, Integer)` | Grupos activos; filtro por carrera y sin filtro por docente |
| HorarioDAO | `listarPorGrupo(int)` | Consulta de horarios |
| HorarioDAO | `insertar(Horario)` | Registro |
| HorarioDAO | `actualizar(Horario)` | Modificación |
| HorarioDAO | `eliminar(int)` | Eliminación confirmada |

`CarreraDAO`, `AsignaturaDAO` y `PeriodoDAO` del trabajo anterior ya estaban
implementados en la rama y en `develop` local al comenzar. No se copiaron ni
incorporaron commits de otra rama. AsignaturaDAO y PeriodoDAO forman parte del
sistema, pero estos formularios no los invocan.

Los DAO conservan sus contratos y su política de persistencia. No se cambiaron
SQL, transacciones, modelos ni autenticación. El estado remoto de otros PR no se
consultó.

## 11. Resultados y evidencias disponibles

La construcción `clean jar` mediante Ant terminó con **BUILD SUCCESSFUL** bajo
JDK 25.0.4.1. Se compilaron 48 archivos fuente. Persistió una advertencia previa
por operaciones sin comprobación de tipos en `FrmEstudiantes.java`, fuera del alcance.

Se ejecutaron **72 comprobaciones aisladas**: 30 de usuarios y 42 de horarios.
El programa de verificación temporal accionó Swing en el hilo de eventos y
utilizó los DAO reales con un sustituto JDBC en memoria, sin conectarse a MySQL.
Estas pruebas verifican el flujo y los parámetros enviados; no demuestran la
persistencia, las restricciones SQL ni el comportamiento del servidor real.

La revisión XML comprobó los tipos de formulario, declaraciones, inicialización,
referencias de componentes, enlaces de eventos y constructores. Se mantienen
los marcadores de código generado, `initComponents()`, ambos constructores y
`JInternalFrame`. La validación visual en **Design sigue pendiente**.

El registro detallado y los procedimientos pendientes constan en
[VERIFICACION_USUARIOS_HORARIOS.md](VERIFICACION_USUARIOS_HORARIOS.md).

### Capturas pendientes y procedimiento

No se encontraron capturas anteriores ni se obtuvieron evidencias con una base de
demostración autorizada. No se incluyen imágenes generadas ni representaciones de
pruebas en memoria como evidencia del funcionamiento con MySQL.

Para obtener las evidencias reales:

1. Preparar la instancia descartable descrita en la sección 6.
2. Abrir ambos formularios en Design; revisar alineación, tamaños, componentes y
   eventos. Guardar los pares `.java` y `.form` si el diseñador requiere ajustes.
3. Ejecutar Clean and Build y después Run Project desde NetBeans.
4. Entrar con ADMIN y capturar Usuarios con su tabla; capturar también un mensaje
   de usuario obligatorio o de carrera requerida para COORDINADOR.
5. Abrir Horarios, elegir un grupo y capturar su tabla y una validación de hora
   inválida o de inicio igual al fin.
6. Entrar con COORDINADOR y capturar la lista de grupos restringida a su carrera.
7. Completar altas, modificaciones y eliminaciones con datos descartables y
   comprobar la persistencia cerrando y reabriendo el formulario.
8. Guardar únicamente la ventana pertinente, sin contraseñas ni información
   personal, en `docs/capturas/`, por ejemplo:
   `usuarios-admin.png`, `usuarios-validacion.png`, `horarios-admin.png`,
   `horarios-validacion.png` y `horarios-coordinador.png`.
9. Añadir aquí enlaces y pies que indiquen fecha, perfil y acción realmente
   observada. Crear el sexto commit solo cuando esas capturas existan.

La carpeta de capturas y el sexto commit no se crean mientras no haya imágenes
auténticas que incluir.

## 12. Conclusiones

La implementación incorpora los formularios de administración de usuarios y
horarios a los contratos existentes del proyecto. El uso de modelos de tabla y
combos permite conservar las identidades de los registros y presentar opciones
obtenidas por los DAO, sin introducir datos simulados en el producto.

Las comprobaciones aisladas respaldan los estados de la interfaz, las
validaciones, la conservación de claves al editar y las operaciones dirigidas a
los DAO. La compilación confirma la coherencia del código Java con los contratos
actuales. La aceptación final requiere pruebas con MySQL y revisión de los
metadatos en el diseñador de NetBeans.

El trabajo mantiene los límites de responsabilidad del equipo y deja explícitas
las evidencias que aún deben aportar los responsables de integración.

## 13. Recomendaciones

- Verificar Design y persistencia real antes de aprobar el trabajo.
- Registrar las capturas pendientes únicamente durante una ejecución real.
- Probar duplicados, claves foráneas y fallos de conexión en la instancia descartable.
- Solicitar a cada integrante sus resultados y conclusiones para la versión final
  del informe grupal; la tabla de responsabilidades no reemplaza esos aportes.
- Completar la plantilla institucional del docente y la revisión de reportes,
  periodos y notas cuando sus responsables integren esas partes.
- Explicar en la exposición la separación entre vista, modelo y DAO, el flujo de
  botones y las restricciones por perfil.

## 14. Referencias del proyecto

- [README](../README.md): ejecución, estructura y usuarios de demostración.
- [REPARTO](REPARTO.md): responsabilidades, requisitos APE y componentes.
- [PROMPTS](PROMPTS.md): estilo de programación y flujo de formularios.
- [Esquema SQL](../db/schema.sql): nombres, longitudes y relaciones.
