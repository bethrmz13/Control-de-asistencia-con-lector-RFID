## ¿Qué hice?
(Descripción breve de lo que se agregó o corrigió)

## Archivos que toqué
-

## Cómo probarlo
1. Abrir XAMPP e iniciar **Apache** y **MySQL**.
2. En phpMyAdmin, importar `db/schema.sql` (si cambió, borrar antes la BD `asistencia_rfid`).
3. En NetBeans: `git checkout` de esta rama → **Clean and Build** → **Run**.
4. Pasos concretos para ver mi cambio (ej.: ingresar con `admin` / `admin123`, abrir el menú Estudiantes, crear uno nuevo):
5. Resultado esperado:

## Checklist
- [ ] Compila sin errores (BUILD SUCCESSFUL)
- [ ] Solo modifiqué mis archivos (o avisé al grupo si toqué otros)
- [ ] No cambié las firmas de DAO ni del servicio sin avisar al líder
- [ ] Si cambié la BD, actualicé `db/schema.sql` y avisé para reimportar
- [ ] No subí `build/`, `dist/` ni `nbproject/private/`
- [ ] Probé con el lector o escribiendo el código a mano