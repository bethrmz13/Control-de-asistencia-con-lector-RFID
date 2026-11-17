## ¿Qué hice?
(Descripción breve de lo que se agregó o corrigió)

## Archivos que toqué
-

## Revisor principal
@usuario-de-github (según la tabla de revisión del README)

## Cómo probarlo
1. Iniciar **Apache** y **MySQL** en XAMPP.
2. Si cambió `db/schema.sql`, borrar la BD `asistencia_rfid` e importarlo de nuevo.
3. En NetBeans: **Clean and Build** y **Run** con esta rama.
4. Pasos concretos para ver mi cambio (ej.: entrar con `admin` / `admin123`, abrir el menú Estudiantes y crear un estudiante):
5. Resultado esperado:

## Checklist
- [ ] Compila sin errores (`BUILD SUCCESSFUL`)
- [ ] Solo modifiqué mis archivos (o avisé al grupo si toqué otros)
- [ ] No cambié el contrato (modelo, interfaces DAO ni firmas de los controladores) sin avisar a Ali
- [ ] Usé `java.sql.Date` y `java.sql.Time` (no `java.time`) y solo lo visto en clase
- [ ] Usé `PreparedStatement` y cerré la conexión (`cc.close()`)
- [ ] Si hice una ventana, subí el `.java` **y** el `.form`
- [ ] Si cambié la BD, actualicé `db/schema.sql` y avisé para reimportar
- [ ] No subí `build/`, `dist/` ni `nbproject/private/`, ni rutas como `C:\Users\...`
- [ ] Probé con el lector o escribiendo el código a mano (si aplica)
- [ ] Mis commits siguen el formato `tipo(donde): descripción`
- [ ] Puedo explicar mi código