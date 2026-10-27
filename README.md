# API de Gestión de Tutorías Académicas (pf25tutorias-api)

## Datos del Estudiante
- **Nombre completo:** Melissa Betzabeh Borrayo Mejía
- **Número de carné:** 9941-25-28099
- **Curso:** Programación / Sistemas de Información
- **Universidad:** Universidad Mariano Gálvez de Guatemala (Sede Portales)

---

## Descripción del Proyecto
El sistema administra la programación, el control y el seguimiento de sesiones de tutoría académica asociadas a distintas materias. Evita cruces de horarios, calcula automáticamente el costo total de cada sesión a partir de la tarifa por hora de la materia, gestiona estados operativos y ofrece un indicador del total esperado de ingresos por rango de fechas.

Incluye un **backend REST en Spring Boot** y una **interfaz web** (HTML, CSS y JavaScript) servida por la misma aplicación.

## Tecnologías Utilizadas
- **Lenguaje:** Java 17+
- **Framework:** Spring Boot 3.x (Web, Data JPA, Validation)
- **Base de datos:** PostgreSQL
- **Frontend:** HTML5, CSS3, JavaScript (Vanilla JS)
- **Pruebas:** JUnit 5, Mockito, MockMvc
- **Herramientas:** Maven, Git y GitHub (MelissaBorrayo98), Postman, IntelliJ IDEA

## Estructura General del Sistema
```
src/main/java/com/example/pf25tutorias/
├── controller/   # Controladores REST (endpoints)
├── dto/          # DTO de entrada/salida con validaciones (@Valid)
├── exception/    # Manejador global de errores (404, 400, 409)
├── model/        # Entidades JPA: Materia y SesionTutoria
├── repository/   # Interfaces Spring Data JPA
└── service/      # Lógica de negocio y reglas operativas
src/main/resources/static/   # Frontend: index.html, app.js, style.css
src/test/java/...            # Pruebas unitarias y de la capa web
DATABASE/                    # Scripts SQL
EVIDENCIAS/                  # Evidencias y colección de Postman
```

## Requisitos
1. JDK 17 o superior.
2. Apache Maven (o el wrapper `mvnw` incluido).
3. PostgreSQL en ejecución.
4. Un IDE compatible (IntelliJ IDEA recomendado).

## Configuración de PostgreSQL
1. Crear la base de datos (conectado a `postgres`):
   ```sql
   CREATE DATABASE pf25_tutorias_db;
   ```
   (o ejecutar `DATABASE/01_crear_base_de_datos.sql`)
2. Conectado a `pf25_tutorias_db`, ejecutar `DATABASE/02_tablas_y_datos_de_prueba.sql`. Crea las tablas `materia` y `sesion_tutoria` y carga datos de prueba. **Borra y recrea las tablas.**
3. Indicar las credenciales en `src/main/resources/application.properties`, o definir las variables de entorno `DB_USER` y `DB_PASSWORD`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/pf25_tutorias_db
   spring.datasource.username=${DB_USER:postgres}
   spring.datasource.password=${DB_PASSWORD:cambiar_password}
   spring.jpa.hibernate.ddl-auto=update
   ```

## Cómo iniciar la aplicación
```
mvn spring-boot:run
```
El servidor inicia en `http://localhost:8080`.

- **Interfaz web:** http://localhost:8080/index.html
- **API REST:** `http://localhost:8080/api/...`

## Cómo ejecutar las pruebas
```
mvn test
```
Las pruebas unitarias y de la capa web no necesitan base de datos. La prueba `contextLoads` (arranque completo) está desactivada con `@Disabled` porque sí la necesita.

---

## Endpoints

### Materias (`/api/materias`)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/materias` | Lista todas las materias (con sus sesiones) |
| GET | `/api/materias/{id}` | Consulta una materia |
| POST | `/api/materias` | Registra una materia (validada con DTO) |
| PUT | `/api/materias/{id}` | Actualiza una materia |
| DELETE | `/api/materias/{id}` | Elimina una materia y sus sesiones |

Ejemplo de cuerpo (POST/PUT):
```json
{ "nombre": "Bases de Datos I", "descripcion": "Modelado relacional", "tarifaHora": 200.0 }
```

### Sesiones de tutoría (`/api/sesiones`)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/sesiones` | Lista todas las sesiones |
| GET | `/api/sesiones/{id}` | Consulta una sesión |
| POST | `/api/sesiones/materia/{materiaId}` | Registra una sesión de una materia |
| PUT | `/api/sesiones/{id}` | Actualiza una sesión (recalcula el costo) |
| DELETE | `/api/sesiones/{id}` | Elimina una sesión |
| GET | `/api/sesiones/materia/{materiaId}` | Filtro: sesiones de una materia |
| GET | `/api/sesiones/pendientes` | Filtro: sesiones con estado PENDIENTE |
| GET | `/api/sesiones/fecha?inicio=...&fin=...` | Filtro: sesiones en un rango de fecha y hora |
| GET | `/api/sesiones/total-dia?inicio=...&fin=...` | Indicador: total esperado de ingresos en el rango |

Fechas en formato ISO, por ejemplo `2026-11-10T16:00:00`.

Ejemplo de cuerpo (POST/PUT). **No se envía el costo:** lo calcula el sistema.
```json
{
  "fechaHora": "2026-11-10T16:00:00",
  "duracion": 2.0,
  "estudiante": "Juan Pérez",
  "estado": "PENDIENTE"
}
```
Respuesta (201 Created):
```json
{
  "id": 4, "fechaHora": "2026-11-10T16:00:00", "duracion": 2.0,
  "estudiante": "Juan Pérez", "estado": "PENDIENTE",
  "costoTotal": 300.0, "materiaId": 1, "materiaNombre": "Matemáticas Discretas"
}
```
Estados permitidos: `PENDIENTE`, `PROGRAMADA`, `CONFIRMADA`, `COMPLETADA`, `CANCELADA`.

---

## Reglas de Negocio
1. **Duración mayor a 0:** `duracion` debe ser mayor a 0 horas (anotación `@Positive` en el DTO y validación en el servicio). Si no, responde `400`.
2. **Sin choque de horarios:** no se permite otra sesión en la misma fecha y hora, tanto al crear como al actualizar. Si existe, responde `409`.
3. **Cálculo automático del costo:** `costoTotal = duracion × tarifaHora` de la materia. El cliente no lo envía y se recalcula al actualizar.
4. **Estado por defecto:** si no se indica estado al registrar, se asigna `PENDIENTE`.
5. **Total esperado:** el indicador `total-dia` suma el costo de las sesiones del rango, sin contar las `CANCELADA`.
6. **Nombre de materia único:** no se permiten dos materias con el mismo nombre (`409`).

## Manejo de Errores
Todas las respuestas de error usan el mismo formato:
```json
{ "status": 400, "error": "Los datos enviados no son válidos.", "errores": { "duracion": "La duración debe ser mayor a 0 horas." } }
```
| Código | Cuándo ocurre |
|---|---|
| 400 | Datos inválidos, JSON o fecha mal formados, rango de fechas invertido |
| 404 | La sesión o la materia no existe |
| 409 | Horario ocupado, nombre de materia duplicado o restricción de base de datos |

## Pruebas con Postman
La colección `EVIDENCIAS/PF-25_Tutorias_v2.postman_collection.json` incluye los casos correctos y los de error (duración negativa, horario duplicado, materia inexistente, sesión inexistente).
