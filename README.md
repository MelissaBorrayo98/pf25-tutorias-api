# API de Gestión de Tutorías Académicas (pf25tutorias-api)

## Datos del Estudiante
- **Nombre completo:** Melissa Betzabeh Borrayo Mejía
- **Número de carné:** 9941-25-28099
- **Curso:** Programación / Sistemas de Información
- **Universidad:** Universidad Mariano Gálvez de Guatemala (Sede Portales)

---

## Descripción del Proyecto
El sistema permite administrar de forma eficiente la programación, control y seguimiento de sesiones de tutoría académica asociadas a distintas materias. Cuenta con un **backend en Spring Boot** y una **interfaz gráfica web (Frontend)** integrada, resolviendo la necesidad de evitar cruces de horarios, calcular automáticamente los costos totales en función de la tarifa por hora de cada materia, gestionar estados operativos y proveer indicadores financieros y reportes.

---

## Tecnologías Utilizadas
- **Lenguaje:** Java 17+
- **Framework:** Spring Boot 3.x
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de datos:** PostgreSQL
- **Frontend:** HTML5, CSS3, JavaScript (Vanilla JS)
- **Control de versiones:** Git y GitHub (MelissaBorrayo98)
- **Herramientas de gestión:** Maven, IntelliJ IDEA

---

## Estructura General del Sistema
El proyecto sigue una arquitectura en capas estándar de Spring Boot:
```text
src/main/java/com/example/pf25tutorias/
├── controller/   # Controladores REST que exponen los endpoints de la API
├── dto/          # Objetos de Transferencia de Datos (Request y Response)
├── exception/    # Manejador global de excepciones y errores
├── model/        # Entidades JPA mapeadas a las tablas de la base de datos
├── repository/   # Interfaces de Spring Data JPA para acceso a datos
└── service/      # Lógica de negocio, reglas operativas y cálculos automáticos
```
Y la estructura de archivos estáticos para la interfaz visual:
```text
src/main/resources/static/
├── index.html    # Panel visual interactivo de la aplicación
├── app.js        # Lógica de consumo de la API desde el navegador
└── style.css     # Estilos y diseño responsivo del dashboard
```

---

## Requisitos para Ejecutar el Proyecto
1. Java Development Kit (JDK) 17 o superior instalado.
2. Apache Maven instalado y configurado en las variables de entorno.
3. PostgreSQL instalado y ejecutándose localmente.
4. Un IDE compatible (IntelliJ IDEA recomendado).

---

## Configuración de PostgreSQL
1. Abre tu gestor de base de datos PostgreSQL y crea la base de datos:
   ```sql
   CREATE DATABASE pf25tutorias;
   ```
2. Configura tus credenciales en el archivo `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/pf25tutorias
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```

---

## Instrucciones para Iniciar la Aplicación
1. Clona o abre el repositorio del proyecto en tu IDE.
2. Ejecuta el siguiente comando con Maven para compilar y arrancar la aplicación:
   ```bash
   mvn spring-boot:run
   ```
3. El servidor se iniciará por defecto en el puerto `8080`.

---

## Accesos y Endpoints del Sistema

### 1. Interfaz Web (Frontend)
Una vez encendida la aplicación, puedes acceder al panel interactivo directamente desde tu navegador:
- **Dashboard Web:** [http://localhost:8080/index.html](http://localhost:8080/index.html)

### 2. API REST (Endpoints)
Las peticiones se realizan hacia la ruta base `http://localhost:8080/api/...`:

- **Materias (`/api/materias`)**
  - `GET /api/materias`: Lista todas las materias registradas.
  - `GET /api/materias/{id}`: Consulta una materia específica por su ID.
  - `POST /api/materias`: Registra una nueva materia.
  - `PUT /api/materias/{id}`: Actualiza la información de una materia.
  - `DELETE /api/materias/{id}`: Elimina una materia.

- **Sesiones de Tutoría (`/api/sesiones`)**
  - `GET /api/sesiones`: Lista todas las sesiones programadas.
  - `GET /api/sesiones/{id}`: Consulta una sesión específica por su ID.
  - `POST /api/sesiones/materia/{materiaId}`: Registra una nueva sesión asociada a una materia, extrayendo el nombre y calculando costos.
  - `GET /api/sesiones/materia/{materiaId}`: Filtro - Obtiene sesiones por materia.
  - `GET /api/sesiones/pendientes`: Filtro - Consulta sesiones con estado "PENDIENTE".
  - `GET /api/sesiones/fecha?inicio=...&fin=...`: Filtro - Consulta sesiones dentro de un rango de fechas.
  - `GET /api/sesiones/total-dia?inicio=...&fin=...`: Indicador - Calcula el total esperado de ingresos en un rango de fecha/hora.
  - `DELETE /api/sesiones/{id}`: Elimina una sesión de tutoría.

---

## Reglas Principales del Negocio
1. **Validación de Duración:** La duración de cada sesión debe ser mayor a 0 horas.
2. **Prevención de Choque de Horarios:** Valida que no exista otra sesión programada exactamente en la misma fecha y hora.
3. **Cálculo Automático de Costos:** El `costoTotal` se calcula automáticamente multiplicando la duración en horas por la `tarifaHora` definida en la materia seleccionada.
4. **Mapeo de Nombre de Materia:** Asigna y persiste automáticamente el nombre de la materia en los registros de las sesiones para su correcta visualización tanto en backend como en frontend.
5. **Estado por Defecto:** Si no se especifica un estado al registrar una sesión, el sistema asigna por defecto "PENDIENTE".
