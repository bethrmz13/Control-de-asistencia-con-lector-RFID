# Verificación de usuarios y horarios

- **Fecha:** 16 de noviembre de 2026.
- **Rama:** `feature/usuarios-horarios-documentacion`.
- **Alcance:** FrmUsuarios, FrmHorarios y documentación APE.

## Condiciones iniciales

La rama coincidía con la solicitada y `git status --short` no mostraba cambios.
`HEAD` y `develop` local apuntaban a `5fad14a`. Ambos formularios estaban vacíos.
Los DAO requeridos ya tenían implementación; no se alteraron ni se trajeron
cambios de otras ramas.

Antes de editar se revisaron README, REPARTO, PROMPTS, esquema, modelos, DAO,
conexión, menú principal y formularios existentes.

## Construcción ejecutada

Se ejecutó el Ant incluido en NetBeans con los objetivos `clean jar`, usando Java
y javac 25.0.4.1. Resultado: **BUILD SUCCESSFUL**, 48 archivos Java compilados.
La construcción se realizó para los diseños y las implementaciones de ambos
formularios. Los directorios generados `build/` y `dist/` permanecen ignorados.

La advertencia existente de operaciones sin comprobación de tipos en
`FrmEstudiantes.java` no impidió construir. Ese archivo no fue modificado.

## Revisión estructural de formularios

Se interpretó cada `.form` como XML y se contrastó con su `.java`:

- Tipo `JInternalFrameFormInfo` y clase que extiende `JInternalFrame`.
- Componentes del XML declarados e inicializados en Java.
- Referencias de layout dirigidas a componentes existentes.
- Eventos XML con método y enlace `addActionListener` correspondiente.
- Constructor público que recibe `Usuario`.
- Se conservaron `initComponents()` y los marcadores `GEN-BEGIN` / `GEN-END`.
- Layout y propiedades de los componentes generados desde una definición común
  para mantener la correspondencia entre el código y los metadatos.

**Resultado:** revisión estructural satisfactoria.

**Pendiente:** abrir ambos pares en Design, comprobar el renderizado y guardar
desde NetBeans. Ni la revisión XML ni la compilación certifican esta apertura.

## Pruebas ejecutadas sin MySQL

Se compiló y ejecutó un programa temporal de verificación fuera del repositorio,
sin añadir bibliotecas. Usó reflexión para observar el estado de los componentes
privados y accionó botones y selecciones en el hilo de eventos de Swing.
Un temporizador respondió a los diálogos para verificar los caminos Sí y No.

En ese proceso se registró un controlador JDBC de prueba que resolvía las
consultas y escrituras mediante colecciones en memoria. El controlador de MySQL
quedó fuera de DriverManager únicamente durante ese proceso. Las vistas y los
DAO ejecutados fueron los del proyecto; `Conexion.java` no se modificó.
No hubo conexiones de red ni escrituras en MySQL.

Los datos de prueba existieron únicamente en memoria y no se incluyeron en los
formularios, el esquema o los commits. El código auxiliar permaneció en el
directorio temporal del entorno; no forma parte del producto.

### Usuarios: 30 comprobaciones satisfactorias

| Grupo de comprobaciones | Cantidad | Resultado observado |
|---|---:|---|
| Sesión nula y validación de permiso inicial | 2 | Administración bloqueada |
| Estados Inicio y Nuevo | 2 | Campos y botones conforme al flujo |
| Campos vacíos, perfil y carrera | 9 | Rechazo de faltantes; COORDINADOR exige carrera; otro perfil la limpia |
| Límites de usuario, nombre y clave | 3 | Rechazo de 31, 81 y 51 caracteres |
| Cancelar | 1 | Limpieza y bloqueo de campos |
| Selección, ID y clave vacía al editar | 2 | Conserva ID y permite clave vacía |
| Tabla | 1 | Sin editor de celdas ni columna de clave |
| Perfil no ADMIN | 2 | Bloqueo de operaciones, tabla y botones |
| Listado y registro mediante DAO | 2 | Tabla alimentada y recargada después de insertar |
| Carrera y estado activo | 1 | Parámetros correctos del usuario inactivo |
| Actualización con clave vacía | 1 | SQL del DAO no cambia la clave |
| Actualización con nueva clave y otro perfil | 1 | Cambia clave, limpia carrera y activa |
| Eliminación cancelada y confirmada | 2 | No escribe con No; elimina y refresca con Sí |
| DAO devuelve false al guardar | 1 | Conserva formulario para corrección |

### Horarios: 42 comprobaciones satisfactorias

| Grupo de comprobaciones | Cantidad | Resultado observado |
|---|---:|---|
| Sin sesión y coordinador sin carrera | 3 | Gestión bloqueada |
| Filtro de carrera | 1 | Solo aparece el grupo de la carrera de prueba |
| Sin grupo seleccionado | 2 | Nuevo bloqueado y validación rechazada |
| Carga de tabla y ausencia de editor | 2 | Cuatro columnas y horarios del grupo |
| Estado Nuevo | 1 | Grupo fijo, tabla bloqueada, Guardar habilitado |
| Día e inicio/fin obligatorios | 3 | Rechazo de faltantes |
| Aula vacía | 1 | Horario válido con aula opcional |
| Formatos de inicio inválidos | 6 | Rechaza 8:00, 24:00, 23:60, aa:bb, 08:00:00 y -1:00 |
| Fin inválido e intervalos incorrectos | 3 | Rechaza 24:00, horas iguales y fin anterior |
| Límites diarios | 2 | Acepta 00:00–00:01 y 23:58–23:59 |
| Longitud del aula | 2 | Rechaza 21 caracteres y acepta 20 |
| Días de la semana | 7 | Acepta cada índice del 1 al 7 |
| Registro | 1 | Envía domingo como 7 y aula vacía como NULL; recarga |
| Selección y modificación | 2 | Conserva ID, fija grupo y actualiza el horario |
| Eliminación | 2 | No escribe con No; elimina y restaura con Sí |
| Grupo deja de estar permitido | 1 | Reconsulta y rechaza escritura; limpia la selección |
| ADMIN | 1 | Recibe todos los grupos del escenario |
| Cancelar | 1 | Limpia campos y habilita selección de grupo |
| DOCENTE | 1 | Administración bloqueada |

**Salida final del programa:** `TOTAL: 72 comprobaciones sin acceso a MySQL.`

Estas pruebas no verifican el motor SQL, restricciones UNIQUE, claves foráneas,
autenticación real ni concurrencia entre sesiones. Tampoco son capturas del
funcionamiento con la base de datos.

## Pruebas manuales pendientes en la instancia descartable

| Acción | Resultado que debe comprobarse |
|---|---|
| Abrir FrmUsuarios y FrmHorarios en Design | Sin errores al cargar; componentes y eventos conservados |
| Entrar como ADMIN y abrir Usuarios | Tabla real, sin contraseñas |
| Crear usuario con cada perfil | Persistencia al cerrar y reabrir |
| Crear coordinador sin carrera | Rechazo y foco en carrera |
| Editar usuario con clave vacía | Login conserva la clave anterior |
| Editar con nueva clave | Login utiliza la nueva clave |
| Desactivar usuario | El login existente lo rechaza |
| Crear username duplicado | Error del DAO; ningún mensaje de éxito |
| Eliminar usuario referenciado | Comportamiento conforme a las claves foráneas existentes |
| Entrar como COORDINADOR | Solo grupos activos de su carrera |
| Entrar como DOCENTE o REGISTRO | Sin acceso a la administración de horarios |
| Guardar días de lunes a domingo | Valores SQL entre 1 y 7 |
| Crear, editar y eliminar horario | Persistencia real y recarga de la tabla |
| Horas inválidas o inicio igual/mayor al fin | Rechazo antes de escribir |
| Repetir grupo, día y hora inicial | Rechazo por la restricción existente |
| Cambiar de grupo tras Cancelar | Tabla y formulario correspondientes al nuevo grupo |
| Desactivar periodo o retirar grupo durante edición | Rechazo al guardar al volver a consultar los grupos |
| Fallo de conexión | Mensaje existente del DAO; sin anunciar un guardado exitoso |

La revisión del periodo y de los datos de grupos debe hacerse únicamente en el
entorno descartable por el responsable autorizado, sin modificar una base
compartida. Los DAO actuales convierten ciertos fallos de lectura en listas
vacías; la interfaz no puede distinguirlos de la ausencia de registros y no se
cambió ese contrato.

## Capturas y aceptación

No hay capturas reales disponibles. Están pendientes Usuarios, Horarios, sus
validaciones y la vista restringida del coordinador. El procedimiento y los
nombres propuestos están en [GUIA_APE.md](GUIA_APE.md#capturas-pendientes-y-procedimiento).

Los primeros cuatro commits corresponden a diseño y funcionalidad. El quinto
incorpora esta documentación y la guía. El sexto queda pendiente hasta obtener
capturas auténticas; no se creó un commit vacío.

No se hizo push ni se creó un Pull Request. La apertura en Design, las pruebas
con MySQL y las capturas constituyen las verificaciones pendientes de aceptación.
