# Control de asistencia con lector RFID

Sistema de escritorio para registrar la asistencia de estudiantes por clase, usando un lector RFID USB (125 kHz) que funciona como teclado. Proyecto de la asignatura **Computación Visual**, Carrera de Software, FISEI, Universidad Técnica de Ambato.

Desarrollado en **Java con Swing**, **JDBC** (patrón DAO) y **MySQL** (XAMPP). Sin frameworks.

## Requisitos

- JDK 25
- Apache NetBeans
- XAMPP (módulos **Apache** y **MySQL**)
- Git
- JasperReports (diseño de reportes con iReport o Jaspersoft Studio; los .jar van en src/plugins)

El conector de MySQL (`mysql-connector-j`) ya está incluido en el repositorio, en `src/plugins`. No hay que descargarlo.

## Cómo probar el proyecto

1. Clonar el repositorio:
   ```
   git clone https://github.com/bethrmz13/Control-de-asistencia-con-lector-RFID.git
   ```
2. Abrir XAMPP e iniciar **Apache** y **MySQL**.
3. Entrar a `http://localhost/phpmyadmin` y **crear la base de datos** importando `db/schema.sql` (pestaña *Importar*). Si ya existía una versión anterior, borrar antes la BD `asistencia_rfid`.
4. En NetBeans: **File → Open Project** y elegir la carpeta clonada.
5. Clic derecho sobre el proyecto → **Clean and Build** → debe decir `BUILD SUCCESSFUL`.
6. Clic derecho sobre el proyecto → **Run**.

### Conexión a la base de datos

Definida en `asistenciarfid.db.Conexion`: `jdbc:mysql://localhost/asistencia_rfid`, usuario `root`, sin contraseña (configuración por defecto de XAMPP).

### Probar sin lector RFID

El lector escribe el código de la tarjeta y presiona Enter, igual que un teclado. Quien no tenga lector puede **escribir el código a mano** en el campo de lectura y pulsar Enter.

## Estructura del proyecto

```
src/asistenciarfid/
├── modelo/     Clases de datos (Estudiante, Asignatura, Periodo...)
├── db/         Conexion
├── dao/        Acceso a datos (interfaces y su implementación con JDBC)
├── servicio/   Reglas de negocio (asistencia, matrícula, reportes)
├── ui/         Ventanas Swing (login, principal, ventanas internas)
└── util/       Parámetros y utilidades
db/             Script de la base de datos (schema.sql)
```

## Reglas de negocio

### Asistencia

- Cada clase es un **horario** semanal (día, hora de inicio y fin) de un grupo (materia + paralelo + periodo).
- La tarjeta registra asistencia solo si el estudiante está matriculado en esa clase y pasa la tarjeta **desde la hora de inicio hasta 15 minutos después**. Nada antes del inicio.
- Resultados posibles: `REGISTRADO`, `YA_REGISTRADO`, `NO_ENCONTRADO`, `SIN_CLASES_HOY`, `FUERA_DE_HORARIO`.

### Matrícula

- La abre y la cierra el administrador por periodo.
- El estudiante se matricula en **máximo 5 materias** por periodo, eligiendo materia y paralelo.
- Solo ve materias de su carrera, de su nivel o inferiores, con estado `AUN_NO CURSA` o `REPROBADA`. No puede tomar dos horarios que se choquen.

### Notas y niveles

- El docente registra las notas de sus grupos (el administrador, las de todos). Escala de 0 a 10.
- Al cerrar el periodo, **7 o más** pasa a `APROBADA` y menos de 7 a `REPROBADA`.
- Si el estudiante aprobó todo lo que matriculó, sube de nivel. Si reprobó algo, el administrador define su nivel.

## Perfiles

| Perfil | Qué puede hacer |
|---|---|
| ADMIN | Todo: usuarios, estudiantes, carreras, periodos, matrículas, cierre de periodo |
| COORDINADOR | Gestiona estudiantes, materias y horarios **solo de su carrera** |
| DOCENTE | Registra notas y ve reportes de sus grupos |
| REGISTRO | Solo la pantalla de lectura de tarjetas |
| ESTUDIANTE | Se matricula cuando el periodo está abierto |

## Flujo de trabajo en Git

- `main`: versión estable. No se trabaja directamente aquí.
- `develop`: integración. **Todos los Pull Request apuntan a `develop`.**
- Cada tarea se hace en su propia rama: `feature/nombre-de-la-tarea`.

```
git checkout develop
git pull
git checkout -b feature/nombre-de-la-tarea
```

Antes de empezar cada día: `git pull` en `develop` y mezclar `develop` en tu rama. Commits cortos y en español (ej. `Agrega validación de código vacío`). Cada PR usa la plantilla de `.github/pull_request_template.md` y necesita al menos una aprobación.

**Regla de oro:** cada persona trabaja en sus archivos. Si necesitas cambiar una clase de otra persona o la base de datos, avisa en el grupo antes.

## Estado del proyecto

- [x] Estructura del proyecto y paquetes
- [x] Conexión a la base de datos
- [ ] Clases del modelo (en progreso)
- [ ] DAO y servicios
- [ ] Ventanas (login, principal y ventanas internas)
- [ ] Pruebas con el lector RFID real

## Integrantes y reparto de tareas

| Integrante | Rol | Archivos propios |
|---|---|---|
| Ali | Líder e integración | `AsistenciaRFID`, `VentanaLogin`, `VentanaPrincipal`, `UsuarioDAO`, `FrmUsuarios`, `Parametros`, `db/schema.sql` |
| Damaris | Lógica central | `HorarioDAO`, `AsistenciaDAO`, `MatriculaDAO`, `AsistenciaServicio`, `MatriculaServicio`, `CierrePeriodoServicio` |
| Evelyn | Estudiantes | `EstudianteDAO`, `RegistroAcademicoDAO`, `FrmEstudiantes` |
| Tatiana | Pantallas de uso diario | `FrmRegistro`, `FrmMatricula` |
| Brayan | Gestión académica | `CarreraDAO`, `PeriodoDAO`, `AsignaturaDAO`, `GrupoDAO`, `FrmAsignaturas`, `FrmGrupos`, `FrmPeriodos` |
| Victoria | Notas y reportes | `FrmNotas`, `ReporteServicio`, `FrmReportes`, reportes de JasperReports, `docs/` |

### Revisión de código

Todos programan y todos revisan. Cada PR tiene un revisor principal y uno de respaldo.

| Autor | Revisor principal | Respaldo |
|---|---|---|
| Ali | Victoria | Brayan |
| Damaris | Ali | Victoria |
| Evelyn | Damaris | Ali |
| Tatiana | Evelyn | Damaris |
| Brayan | Tatiana | Evelyn |
| Victoria | Brayan | Tatiana |

- Nadie aprueba ni mezcla su propio PR.
- El revisor principal tiene unas 3 horas de trabajo para revisar. Si no puede, avisa en el grupo y revisa el respaldo. Si tampoco, cualquier otro integrante.
- Quien aprueba el PR y comprueba que compila y funciona, hace el merge a `develop`.
- Un PR debe ser pequeño: una sola funcionalidad.

## Cómo abrir un Pull Request

1. Sube tu rama: `git push -u origin feature/nombre-de-la-tarea`
2. En GitHub aparece el botón **Compare & pull request**. Pulsa ahí.
3. Verifica que diga **base: `develop`** ← **compare: tu rama**. Nunca `main`.
4. Llena la plantilla (sobre todo "Cómo probarlo").
5. En el panel derecho, **Reviewers**, elige a tu revisor principal y avisa en el grupo.
6. Si te piden cambios, haz commit y push a la misma rama: el PR se actualiza solo.

## Cómo revisar un Pull Request

Revisar significa bajar la rama de tu compañero, ejecutarla y leer el código. Para **no arruinar tu propio trabajo**, usa una **segunda copia del repositorio solo para revisar**. Esa copia nunca se edita.

### Preparación (una sola vez)

Abre Git Bash en la carpeta donde guardas tus repos (por ejemplo `Documentos\GitHub`) y ejecuta:

```
git clone https://github.com/bethrmz13/Control-de-asistencia-con-lector-RFID.git RevisionPR
```

### Para cada PR

1. **Lee el PR en GitHub:** la descripción y la pestaña **Files changed**. El nombre de la rama aparece arriba (`... from feature/xxx`).
2. **Baja la rama** (Git Bash dentro de `RevisionPR`):
   ```
   git fetch origin
   git checkout feature/xxx
   git pull
   ```
3. **Ábrela en NetBeans:** cierra tu proyecto actual (clic derecho → Close) y abre `RevisionPR` con **File → Open Project**. Ejecuta **Clean and Build** y **Run**, y sigue los pasos de "Cómo probarlo" del PR. Si el PR cambió `db/schema.sql`, reimporta la BD.
4. **Revisa con esta lista:**
   - [ ] Compila sin errores
   - [ ] Hace lo que dice el PR
   - [ ] Solo toca los archivos de su autor
   - [ ] No cambia las firmas del contrato (DAO y servicios)
   - [ ] Usa `PreparedStatement` y `try-with-resources`
   - [ ] No hay contraseñas ni rutas como `C:\Users\...`
   - [ ] No sube `build/`, `dist/` ni `nbproject/private/`
   - [ ] El autor puede explicar su código
5. **Deja tu opinión en GitHub:** en **Files changed**, el `+` junto a una línea permite comentarla. Luego pulsa **Review changes** y elige **Approve** o **Request changes**.
6. **Si aprobaste:** pulsa **Merge pull request** y confirma.
7. **Vuelve a lo tuyo:** en `RevisionPR` ejecuta `git checkout develop` y `git pull`, cierra ese proyecto en NetBeans y reabre el tuyo.

Si encuentras algo por arreglar, **no lo corrijas en `RevisionPR`**: coméntalo en el PR para que lo corrija el autor.

### Alternativa sin segunda copia

Si prefieres revisar en tu misma carpeta, primero guarda tu trabajo sin terminar:

```
git stash push -u -m "mi trabajo"
git fetch origin
git checkout feature/xxx
```

Al terminar de revisar:

```
git checkout feature/mi-rama
git stash pop
```

El `-u` es importante: sin él, Git no guarda los archivos nuevos que aún no has agregado. Con la segunda copia no hay ese riesgo, por eso la recomendamos.
