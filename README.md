# PF-25 — Sesiones de Tutoría Académica (Avance 1)

**Estudiante:** Melissa Betzabeh Borrayo Mejía  
**Carné:** 9941-25-28099  
**Curso / Universidad:** Ingeniería en Sistemas de Información — Universidad Mariano Gálvez (Sede Portales)

---

## 📋 Descripción del Proyecto
El proyecto **`pf25-tutorias-api`** es una API REST desarrollada con **Spring Boot** y **Java** para la gestión y control de sesiones de tutoría académica.

---

## 🛠️ Tecnologías Utilizadas
* **Backend:** Java, Spring Boot, Spring Data JPA, Hibernate.
* **Base de Datos:** PostgreSQL (`pf25_tutorias_db`).
* **Entorno:** IntelliJ IDEA.
* **Pruebas:** Postman.
* **Control de Versiones:** Git / GitHub (`MelissaBorrayo98`).

---

## 🗄️ Estructura de Entidades
1. **`Tutor`**: Almacena información de los tutores (nombre, correo, especialidad, tarifa).
2. **`SesionTutoria`**: Registra los detalles de cada tutoría agendada.

---

## 🧪 Evidencias del Avance 1

### 1. Configuración de Base de Datos
* Conexión configurada en el archivo application.properties:
  spring.datasource.url=jdbc:postgresql://localhost:5432/pf25_tutorias_db
  spring.datasource.username=postgres
  spring.datasource.password=tu_contraseña
  spring.jpa.hibernate.ddl-auto=update
* Arranque exitoso de Tomcat en el puerto `8080`.

### 2. Scripts SQL
* **Tablas:** Scripts ejecutados en pgAdmin 4 (`pf25_crear_bd.sql`, `pf25_crear_tablas.sql`).
* **Datos:** Inserción mediante `datos_prueba_avance1.sql`.

### 3. Endpoints en Postman
* **`GET /api/tutores`**: Estado `200 OK`.
* **`GET /api/sesiones`**: Estado `200 OK`.
* **`POST /api/sesiones`**: Registro exitoso de sesiones.

---
*Universidad Mariano Gálvez de Guatemala — 2026*