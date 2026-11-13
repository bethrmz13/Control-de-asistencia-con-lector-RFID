# Reparto de tareas

**Proyecto:** Control de asistencia con lector RFID · Computación Visual · FISEI, UTA
**Entrega:** martes 17 de noviembre (4 días de trabajo + 1 de colchón) · **Equipo:** Ali, Damaris, Evelyn, Tatiana, Brayan, Victoria

Todos programan y todos revisan. Este archivo dice quién hace qué, en qué orden y cómo se prueba. Los prompts para la IA están en [PROMPTS.md](PROMPTS.md).

---

## 1. Qué construimos

Una aplicación de escritorio en **Java con Swing** y **MySQL (XAMPP)** que registra la asistencia de estudiantes por clase con un lector RFID USB. El lector funciona como un teclado: escribe el código de la tarjeta y pulsa Enter.

Reglas del sistema (resumen):

- **Asistencia:** la tarjeta solo registra si el estudiante está matriculado en una clase de hoy y pasa la tarjeta **desde la hora de inicio hasta 15 minutos después**. Nada antes del inicio.
- **Matrícula:** la abre y cierra el admin. El estudiante se matricula en **máximo 5 materias** por periodo, escogiendo materia y paralelo. Solo ve materias de su carrera, de su nivel o inferiores, que estén `AUN_NO_CURSA` o `REPROBADA`. No puede tomar dos horarios que se choquen.
- **Notas y niveles:** el docente registra las notas. Al cerrar el periodo, 7 o más es `APROBADA` y menos de 7 es `REPROBADA`. Si aprobó todo lo que matriculó sube de nivel; si reprobó algo, el admin define su nivel.
- **Perfiles:** ADMIN, COORDINADOR (solo su carrera), DOCENTE, REGISTRO (solo lectura de tarjeta) y ESTUDIANTE (solo matrícula).

## 2. Reglas del equipo

1. **MVC con DAO.** `modelo` (datos), `vista` (ventanas), `controlador` (reglas), `dao` (base de datos).
2. **El contrato no se toca.** No se cambian las clases del `modelo` ni los **nombres y parámetros** de los métodos de las clases del `dao` y del `controlador`. Si necesitas un cambio, avisa en el grupo a Ali, que lo hace en `develop`.
3. **Sencillo y con lo visto en clase.** Sin frameworks, sin librerías nuevas, sin `java.time` (usamos `java.sql.Date` y `java.sql.Time`), sin `enum`, sin streams. El ingeniero pregunta lo que no enseñó.
4. **Cada quien trabaja en sus archivos.** Si necesitas tocar los de otra persona, avisa antes.
5. **Ventanas con el diseñador de NetBeans, solo `JFrame Form`**, como en clase: se crea con *New → Other → Swing GUI Forms → JFrame Form*, se le pone el nombre al archivo y no se toca nada más en la configuración. Se suben juntos el `.java` y el `.form`. **Una excepción:** un `JFrame` nuevo trae `EXIT_ON_CLOSE`, que cierra todo el programa al cerrar la ventana. Por eso cada `FrmXxx` lleva en su constructor `setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);` (se escribe en el código, no en el diseñador).
6. **Commits:** `tipo(donde): descripción`, por ejemplo `feat(login): valida campos vacíos`.
7. **Pull Request siempre a `develop`**, nunca a `main`. Se usa la plantilla de `.github/pull_request_template.md`.

## 3. Lo que ya existe en el repositorio (el contrato)

| Paquete | Contenido |
|---|---|
| `modelo` | `Perfil`, `Estado`, `EstadoMateria` (constantes de texto), `Carrera`, `Usuario`, `Periodo`, `Estudiante`, `Asignatura`, `RegistroAcademico`, `Grupo`, `Horario`, `Matricula`, `Asistencia`, `ResultadoRegistro` |
| `dao` | Clases con métodos `static`, como en clase: `CarreraDAO`, `PeriodoDAO`, `UsuarioDAO`, `EstudianteDAO`, `RegistroAcademicoDAO`, `AsignaturaDAO`, `GrupoDAO`, `HorarioDAO`, `MatriculaDAO`, `AsistenciaDAO` (cuerpo pendiente) |
| `controlador` | `AsistenciaControlador`, `MatriculaControlador`, `CierrePeriodoControlador`, también con métodos `static` y el cuerpo pendiente |
| `db` | `Conexion` |
| `util` | `Parametros` (15 minutos, 5 materias, nota 7) y `Fechas` (ayudas con fechas) |
| `vista` | Ventanas vacías (`FrmXxx`) que cada dueño reemplaza con su diseño |

El cuerpo de cada método de `dao` y de `controlador` lo escribe su dueño: se reemplaza el `return` de relleno, sin cambiar el nombre ni los parámetros. Se usan igual que en clase: `EstudianteDAO.listar(null)`, `AsistenciaControlador.registrarPorCodigo(codigo, new Date())`.

## 4. Cómo empezar (todos, hoy)

1. Instalar **XAMPP**, **Git** y **NetBeans** (JDK 25). Iniciar **Apache** y **MySQL** en XAMPP.
2. Clonar el repositorio: `git clone https://github.com/bethrmz13/Control-de-asistencia-con-lector-RFID.git`
3. En phpMyAdmin (`http://localhost/phpmyadmin`) **importar `db/schema.sql`**. Si ya tenías una versión anterior, borrar antes la base `asistencia_rfid`. Deben quedar **10 tablas**.
4. En NetBeans: **File → Open Project** y elegir la carpeta clonada. Luego **Clean and Build**: debe decir `BUILD SUCCESSFUL`.
5. Crear tu rama desde `develop`:
   ```
   git checkout develop
   git pull
   git checkout -b feature/nombre-de-tu-tarea
   ```
6. Avisar en el grupo cuando compile.

## 5. Reparto

| Integrante | Rol | Archivos propios |
|---|---|---|
| **Ali** | Líder e integración | `UsuarioDAO`, `VentanaLogin`, `VentanaPrincipal`, `FrmRegistro` (lectura de tarjeta), `FrmPeriodos`, `AsistenciaRFID` (main), `db/schema.sql` |
| **Damaris** | Lógica central | `HorarioDAO`, `AsistenciaDAO`, `MatriculaDAO`, `AsistenciaControlador`, `MatriculaControlador`, `CierrePeriodoControlador`, `PruebaLogica` |
| **Evelyn** | Estudiantes y matrícula | `EstudianteDAO`, `RegistroAcademicoDAO`, `FrmEstudiantes`, `FrmMatricula` (vista del estudiante) |
| **Tatiana** | Ventana de asignaturas | `FrmAsignaturas` |
| **Brayan** | DAO sencillos, usuarios, horarios y documentación | `CarreraDAO`, `AsignaturaDAO`, `PeriodoDAO`, `FrmUsuarios`, `FrmHorarios`, capturas y guía APE |
| **Victoria** | Grupos, notas y reportes | `GrupoDAO`, `FrmGrupos`, `FrmNotas`, `FrmReportes`, reportes de iReport (`reportes/*.jrxml`) |

### Quién necesita a quién

| Para... | Necesita que exista... |
|---|---|
| Las ventanas con datos reales | Los `DAO` de su tema (cada uno hace los suyos primero) |
| `FrmUsuarios` (Brayan) | `UsuarioDAO` (Ali) y `CarreraDAO` (Brayan) |
| `FrmAsignaturas` (Tatiana) | `AsignaturaDAO` y `CarreraDAO` (Brayan) |
| `FrmHorarios` (Brayan) | `HorarioDAO` (Damaris) y `GrupoDAO` (Victoria) |
| `FrmGrupos` (Victoria) | `GrupoDAO` (Victoria), `AsignaturaDAO` (Brayan) y `UsuarioDAO` (Ali) |
| `FrmPeriodos` (Ali) | `PeriodoDAO` (Brayan) y `CierrePeriodoControlador` (Damaris) |
| `FrmRegistro` (Ali) | `AsistenciaControlador` (Damaris) |
| `FrmMatricula` (Evelyn) | `MatriculaControlador` (Damaris) y `PeriodoDAO` (Brayan) |
| `FrmNotas` (Victoria) | `MatriculaDAO` (Damaris) y `GrupoDAO` (Victoria) |
| Los controladores (Damaris) | `EstudianteDAO` y `RegistroAcademicoDAO` (Evelyn), `PeriodoDAO` y `AsignaturaDAO` (Brayan), `GrupoDAO` (Victoria) |

**Por eso los `DAO` se suben primero**, en PR pequeños, el día 2 por la mañana. Mientras llegan los demás, cada quien diseña su ventana y la prueba con datos de ejemplo.

## 6. Detalle por persona

### Ali: líder e integración

- **`UsuarioDAO`:** `validar` busca por usuario y clave (y que esté activo) y devuelve el usuario sin la clave (o `null`). La clave se guarda tal cual en la columna `clave`.
- **`VentanaLogin`:** campos usuario y clave, botón Ingresar. Si los datos son válidos abre `VentanaPrincipal` con el usuario; si no, muestra un mensaje.
- **`VentanaPrincipal`:** ventana principal (`JFrame` con barra de menú) cuyo menú **cambia según el perfil**. Cada opción abre la ventana correspondiente con `new FrmXxx(usuario).setVisible(true)`.
- **`FrmPeriodos`:** crear y modificar periodos, activar uno, abrir y cerrar matrículas, y **Cerrar periodo**. Antes de cerrar muestra las notas que faltan (si hay, no cierra); luego pide confirmación y al terminar muestra la lista de estudiantes "por revisar".
- **`FrmRegistro`** (lectura de tarjeta): una caja de texto que siempre mantiene el foco. Al pulsar Enter llama a `AsistenciaControlador.registrarPorCodigo(codigo, new Date())`, limpia la caja y muestra nombre, asignatura, paralelo y hora, con color: **verde** si `REGISTRADO`, **amarillo** si `YA_REGISTRADO`, **rojo** en los demás casos.
  - **Modo prueba:** una casilla "Simular fecha y hora" con un campo `yyyy-MM-dd HH:mm`. Si está marcada, se usa esa hora en lugar de la actual. Sirve para mostrar el funcionamiento sin esperar a la hora de clase.
- **`AsistenciaRFID`:** el `main`, que abre el login.
- **Integración:** mezcla los PR aprobados, prueba todo junto y **prueba con el lector real**. Anota en el README qué código escribe el lector (longitud y ceros a la izquierda).

PR sugeridos: `feat(usuarios): implementa UsuarioDAO`, `feat(login): agrega ventana de login`, `feat(principal): agrega ventana principal con menú por perfil`, `feat(registro): agrega pantalla de lectura de tarjeta`, `feat(periodos): agrega ventana de periodos`.

**Debe poder explicar:** cómo se valida el login, qué ve cada perfil, qué pasa desde que se acerca la tarjeta hasta que sale el mensaje y qué pasa al cerrar un periodo.

### Damaris: lógica central

- **`HorarioDAO`, `AsistenciaDAO`, `MatriculaDAO`:** consultas con los nombres de columnas de `schema.sql`.
- **`AsistenciaControlador.registrarPorCodigo(codigo, ahora)`:**
  1. Busca al estudiante. Si no existe: `NO_ENCONTRADO`.
  2. Trae sus horarios de hoy. Si no hay: `SIN_CLASES_HOY`.
  3. Busca el horario donde `horaInicio <= ahora <= horaInicio + 15 min`. Si no hay: `FUERA_DE_HORARIO`.
  4. Si ya tiene asistencia en ese horario y fecha: `YA_REGISTRADO`.
  5. Si no, registra y devuelve `REGISTRADO`.
- **`MatriculaControlador.matricular(estudianteId, grupoId)`:** devuelve `null` si salió bien, o el mensaje del primer error. Orden de las reglas: matrícula abierta, grupo del periodo activo, materia permitida (carrera, nivel y estado), no repetida en el periodo, máximo 5 materias, sin choque de horarios. También `gruposDisponibles(estudiante)`.
- **`CierrePeriodoControlador`:** `notasFaltantes()` y `cerrarPeriodo()`. Primero revisa que no falten notas; luego, con consultas simples una tras otra: nota 7 o más pasa a `APROBADA`, menos a `REPROBADA`; sube de nivel a quien aprobó todo lo que matriculó; devuelve la lista de estudiantes "por revisar"; y al final deja el periodo inactivo y con las matrículas cerradas.
- **`PruebaLogica`** (paquete `util`, con `main`): ejecuta los casos de abajo y muestra `OK` o `FALLA`.

PR sugeridos: `feat(dao): implementa DAO de horario, asistencia y matrícula`, `feat(asistencia): implementa registro por código`, `feat(matricula): implementa reglas de matrícula`, `feat(periodo): implementa cierre de periodo`.

**Casos de asistencia** (lunes 2026-11-09, con los datos de prueba):

| Código y hora | Resultado esperado |
|---|---|
| María 08:10 | `REGISTRADO` (Cálculo) |
| María 08:12 | `YA_REGISTRADO` |
| María 15:55 | `FUERA_DE_HORARIO` (nada antes del inicio) |
| María 16:15 | `REGISTRADO` (POO) |
| María 18:05 | `REGISTRADO` (Base de Datos) |
| Luis 16:16 | `FUERA_DE_HORARIO` (pasó la tolerancia) |
| Luis 08:05 | `FUERA_DE_HORARIO` (no está matriculado en Cálculo) |
| `9999999999` | `NO_ENCONTRADO` |
| María, martes 2026-11-10 | `SIN_CLASES_HOY` |

**Casos de matrícula:**

| Acción | Resultado esperado |
|---|---|
| Luis → Ingeniería de Software | Rechazado: nivel superior al suyo |
| Luis → Cálculo | Rechazado: ya aprobada |
| María → Cálculo otra vez | Rechazado: ya matriculada |
| Luis → Sistemas Operativos | Rechazado: choque con POO (lunes 16:00 a 18:00) |
| María → Estadística y luego Redes | Se matricula (ya tiene 5) |
| María → Sistemas Operativos | Rechazado: máximo 5 materias |
| Con `matricula_abierta = 0` | Rechazado: matrículas cerradas |
| Nota 7.00 / nota 6.99 al cerrar | `APROBADA` / `REPROBADA` |

Para crear horas de prueba se usa `Fechas.crear(2026, 11, 9, 16, 10)` (lunes 9 de noviembre, 16:10).

**Debe poder explicar:** la regla de los 15 minutos, por qué la hora llega como parámetro, el orden de las reglas de matrícula y cómo se cierra un periodo paso a paso.

### Evelyn: estudiantes y matrícula

- **`EstudianteDAO`:** incluye `insertarConUsuario`, que guarda con consultas simples, en este orden: el usuario (perfil `ESTUDIANTE`), el estudiante y las filas de su tabla de materias. Antes revisa que el usuario y el código no existan, y si un paso falla borra lo que ya había guardado.
- **`RegistroAcademicoDAO`:** `generarParaCarrera` **no guarda nada**: arma la lista de materias de la carrera (nivel menor al de inicio = `APROBADA`, el resto `AUN_NO_CURSA`) para mostrarla en la ventana.
- **`FrmEstudiantes`:** formulario (código RFID, nombre, apellido, correo, carrera, nivel de inicio, usuario y clave) y tabla de estudiantes.
  - El código RFID se llena **pasando la tarjeta**: el lector escribe y pulsa Enter, y al pulsar Enter se valida que no esté vacío ni repetido.
  - Al elegir carrera y nivel se muestra la tabla de materias generada, con el estado editable en cada fila (combo con `APROBADA`, `REPROBADA`, `AUN_NO_CURSA`). El admin la corrige antes de guardar.
  - El coordinador solo ve y registra estudiantes de su carrera.
  - Validaciones: campos obligatorios, correo con formato válido, código sin espacios.
- **`FrmMatricula`** (vista del estudiante): busca al estudiante del usuario que inició sesión y muestra su nivel y un contador "Matriculadas: X de 5". Si la matrícula está cerrada, avisa y bloquea los botones. Tiene una tabla con los grupos disponibles (botón Matricular) y otra con sus materias matriculadas (botón Anular).

PR sugeridos: `feat(dao): implementa DAO de estudiantes y registro académico`, `feat(estudiantes): agrega ventana de registro de estudiantes`, `feat(matricula): agrega ventana de matrícula del estudiante`.

**Debe poder explicar:** cómo se crea un estudiante con su usuario, cómo se genera la tabla de materias, qué se hace si un paso falla (se borra lo ya guardado) y por qué la ventana de matrícula no decide las reglas (las decide el controlador).

### Tatiana: ventana de asignaturas

- **`FrmAsignaturas`:** agregar, modificar y eliminar asignaturas (nombre, nivel de 1 a 10, carrera). El coordinador solo ve la de su carrera. Es una ventana independiente: nadie necesita su resultado para avanzar, y las asignaturas también vienen del script de la base de datos.
- **Opcional:** apoyar con las pruebas (probar en `develop` los casos de la tabla de Damaris) y con las capturas de pantalla para la documentación.

PR sugerido: `feat(asignaturas): agrega ventana de asignaturas`.

**Debe poder explicar:** cómo se agrega, modifica y elimina una asignatura, y de dónde sale la lista de carreras.

### Brayan: DAO sencillos, usuarios, horarios y documentación

Tareas cortas y de una sola tabla, para que salgan rápido y se revisen fácil.

- **`CarreraDAO`, `AsignaturaDAO`, `PeriodoDAO`:** consultas sencillas, una por método. Los listados reciben `carreraId` como `Integer` (si es `null`, no filtran). Al activar un periodo, los demás quedan inactivos. **Se suben primero**, porque varias personas los usan.
- **`FrmUsuarios`:** agregar, modificar y eliminar usuarios (usuario, clave, nombre, perfil, carrera si es coordinador, activo).
- **`FrmHorarios`:** se elige un grupo del periodo activo y se ven sus horarios: día, hora de inicio, hora de fin y aula, con Agregar, Modificar y Eliminar. **Este horario es el "evento" que pide la guía.** Valida que la hora de inicio sea menor que la de fin.
- **Documentación:** capturas de pantalla y la guía APE en el formato del ingeniero (resultados, conclusiones y recomendaciones, con aportes de todos).

PR sugeridos: `feat(dao): implementa DAO de carrera, asignatura y periodo`, `feat(usuarios): agrega ventana de usuarios`, `feat(horarios): agrega ventana de horarios`, `docs(guia): agrega guía APE y capturas`.

**Debe poder explicar:** cómo se agrega, modifica y elimina un usuario o un horario.

### Victoria: grupos, notas y reportes

- **`GrupoDAO`:** los listados reciben `carreraId` y `docenteId` como `Integer` (si es `null`, no filtran). Los grupos se listan del periodo activo y traen el nombre y el nivel de la asignatura y el nombre del docente (con `JOIN`). **Se sube primero**, porque otras personas lo usan.
- **`FrmGrupos`:** tabla de grupos del periodo activo con formulario (asignatura, paralelo, docente) y Agregar, Modificar y Eliminar. Los horarios de cada grupo se manejan en `FrmHorarios` (Brayan).
- **`FrmNotas`:** el docente elige uno de sus grupos y ve la tabla de estudiantes con la nota editable. Al guardar valida que cada nota esté vacía o entre 0 y 10. El admin ve todos los grupos.
- **Reportes con iReport:** el reporte trae **su propia consulta SQL**, recibe parámetros de texto y se abre con la conexión, igual que en clase. Son dos, guardados en `reportes/`:
  1. **Asistencia de una clase en una fecha** (`P_GRUPO_ID`, `P_FECHA` con formato `yyyy-MM-dd`): lista cada estudiante matriculado como `PRESENTE` o `FALTA`.
  2. **Total de asistencias por estudiante en un grupo** (`P_GRUPO_ID`).
- **`FrmReportes`:** combo de grupos (el docente ve los suyos, el coordinador los de su carrera, el admin todos), una caja para la fecha y un botón por reporte. Abre el reporte con `JasperViewer.viewReport(imprimir, false)`, igual que en clase, y con **ruta relativa** (`reportes/archivo.jrxml`), nunca una ruta de `C:\`.
- **Documentación:** la revisión final del README (las capturas y la guía APE las lleva Brayan).
- **Día 1: probar iReport y Jasper** con un reporte mínimo en el JDK del proyecto, y avisar al grupo si funciona. Si Jasper falla, el plan de respaldo es mostrar los datos en una tabla dentro de `FrmReportes`.

Consultas de los reportes (parámetros de tipo `String`):

```sql
-- Reporte 1: asistencia de una clase en una fecha
SELECT e.apellido, e.nombre, h.hora_inicio, a.hora AS hora_llegada,
       IF(a.id IS NULL, 'FALTA', 'PRESENTE') AS estado
FROM matricula m
JOIN estudiante e ON e.id = m.estudiante_id
JOIN horario h ON h.grupo_id = m.grupo_id
LEFT JOIN asistencia a ON a.estudiante_id = e.id
                      AND a.horario_id = h.id
                      AND a.fecha = $P{P_FECHA}
WHERE m.grupo_id = $P{P_GRUPO_ID}
  AND h.dia_semana = WEEKDAY($P{P_FECHA}) + 1
ORDER BY e.apellido, e.nombre

-- Reporte 2: total de asistencias por estudiante en un grupo
SELECT e.apellido, e.nombre, COUNT(a.id) AS asistencias
FROM matricula m
JOIN estudiante e ON e.id = m.estudiante_id
LEFT JOIN horario h ON h.grupo_id = m.grupo_id
LEFT JOIN asistencia a ON a.horario_id = h.id AND a.estudiante_id = e.id
WHERE m.grupo_id = $P{P_GRUPO_ID}
GROUP BY e.id, e.apellido, e.nombre
ORDER BY e.apellido
```

PR sugeridos: `feat(dao): implementa DAO de grupos`, `feat(grupos): agrega ventana de grupos`, `feat(notas): agrega ventana de notas`, `feat(reportes): agrega reportes de asistencia con iReport`.

**Debe poder explicar:** cómo se pasa un parámetro a un reporte de iReport, cómo se registra una nota y cómo se crea un grupo.

## 7. Orden de trabajo (4 días + colchón)

| Día | Qué pasa |
|---|---|
| **1** (jueves 12) | Todos clonan, importan la base y compilan. Cada quien crea su rama y empieza su primer `DAO` o su diseño de ventana. Victoria prueba iReport y Jasper. Ali revisa que el contrato compile para todos. |
| **2** (viernes 13, mañana) | Cada quien sube sus `DAO` en **PR pequeños**. Se revisan y se mezclan antes del mediodía. |
| **2** (viernes 13, tarde) | Ventanas con datos reales y controladores de Damaris (ahora que los DAO ya están en `develop`: `git pull` en `develop` y mezclarlo en tu rama). |
| **3** (sábado 14) | Controladores terminados, ventanas conectadas, Ali integra el menú. **Pruebas con el lector real.** Revisiones cruzadas. |
| **4** (domingo 15) | Corrección de errores, pruebas de todos los casos, README final, capturas, guía APE, merge de `develop` a `main` y ensayo de la presentación. |
| **Colchón** (lunes 16) | **Solo se corrigen errores y se ensaya.** No se agregan funciones nuevas. Si algo del plan quedó atrasado, se termina aquí. |
| **Entrega** (martes 17) | Última prueba en limpio (clonar el repo en otra carpeta y compilar) y entrega. |

Las fechas suponen que el equipo empieza el jueves 12. Si el equipo empieza más tarde, se corre todo un día y el colchón se acorta.

## 8. Revisión de código

Todos programan y todos revisan. Nadie aprueba ni mezcla su propio PR.

| Autor | Revisor principal | Respaldo |
|---|---|---|
| Ali | Victoria | Brayan |
| Damaris | Ali | Victoria |
| Evelyn | Damaris | Ali |
| Tatiana | Evelyn | Damaris |
| Brayan | Victoria | Evelyn |
| Victoria | Brayan | Tatiana |

El revisor principal tiene unas 3 horas para revisar. Si no puede, avisa en el grupo y entra el respaldo. Quien aprueba hace el merge a `develop`. La guía paso a paso está en el README.

### Issues y dependencias

Cada entrega (un `DAO`, una ventana, un controlador) tiene su **issue** en GitHub, asignado a su dueño, con la línea **Depende de**. Cómo se usan está en el README, sección *Issues y dependencias*.

## 9. Usuarios y datos de prueba

| Usuario | Clave | Perfil |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `coordsw` | `coord123` | COORDINADOR (Software) |
| `docente1` | `docente123` | DOCENTE |
| `registro` | `registro123` | REGISTRO |
| `maria` | `est123` | ESTUDIANTE (nivel 2, repite Cálculo) |
| `luis` | `est123` | ESTUDIANTE (nivel 2) |

Tarjetas de prueba (de mentira): María `0001234567`, Luis `0007654321`. Se cambian por los códigos de las tarjetas reales.

## 10. Cómo cubre la guía del ingeniero

| La guía pide | Dónde está |
|---|---|
| Ventana de login | `VentanaLogin` (Ali) |
| Ventana principal con menú | `VentanaPrincipal` (Ali), con menú según el perfil |
| Ventanas necesarias para manejar el sistema | `FrmXxx`, todas `JFrame Form`, que se abren desde el menú de la ventana principal |
| Datos guardados en la base de datos | MySQL con DAO |
| Componentes según el tipo de dato | `JSpinner` (nivel), `JComboBox` (carrera, perfil, día), `JCheckBox` (activo), `JPasswordField` (clave), `JTable`, `JTextField` con validación |
| Perfiles | 5 perfiles con menús distintos |
| Agregar, modificar, eliminar y actualizar un evento | Horarios de clase en `FrmHorarios` (Brayan) |
| Reporte con iReport, MVC, DAO, login | `FrmReportes`, estructura de paquetes, `dao`, `VentanaLogin` |

## 11. Entrega final (día 4, antes del colchón)

- [ ] `develop` compila (`BUILD SUCCESSFUL`) y se probaron todos los casos de Damaris
- [ ] Se probó con el lector RFID real y se anotó su formato en el README
- [ ] Cada perfil entra y ve solo lo suyo
- [ ] Los reportes abren con ruta relativa en cualquier PC
- [ ] La base `db/schema.sql` está al día
- [ ] README final, capturas y guía APE listos
- [ ] `develop` mezclado en `main`
- [ ] Cada integrante puede explicar su parte
