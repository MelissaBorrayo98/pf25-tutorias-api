# Tarea 9 — Spring Boot REST API (Sistema de Gestión de Despensa)

## 📋 Datos del Estudiante
* **Nombre completo:** Melissa Betzabeh Borrayo Mejía
* **Número de carné:** 9941-25-28099
* **Curso / Universidad:** Ingeniería en Sistemas de Información — Universidad Mariano Gálvez (Sede Portales)

---

## 📌 Descripción del Problema
Desarrollo de una API REST utilizando Spring Boot para la gestión y control de productos de una despensa doméstica. El sistema permite realizar consultas parametrizadas, filtrados por categoría y stock, cálculo de productos de mayor valor y obtención de un resumen global del inventario con validaciones exactas de cantidades y precios monetarios.

---

## 🛠 Tecnologías Utilizadas
* **Java** (Versión 17 o superior)
* **Spring Boot** (Spring Web)
* **Maven** (Gestor de dependencias)
* **IntelliJ IDEA** (Entorno de desarrollo)
* **Git / GitHub** (Control de versiones)

---

## ⚙️ Requisitos para Ejecutar el Proyecto
1. Tener instalado **Java JDK 17** (o superior).
2. Tener instalado **Maven** (o utilizar el wrapper incluido `mvnw`).
3. Un entorno de desarrollo como **IntelliJ IDEA** o cualquier editor compatible con proyectos Spring Boot.
4. Navegador web o cliente HTTP (como Postman) para probar los endpoints.

---

## 📁 Estructura Principal del Proyecto
```text
proyectodespensa/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/com/tuproyecto/...
│   │   └── resources/application.properties
│   └── test/
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
└── evidencias/
```

---

## 🧩 Explicación de las Clases
* **`Producto` (Modelo/Entidad):** Define la estructura de los datos del producto con atributos como `id`, `nombre`, `categoria`, `cantidad` y `precioUnitario`.
* **`ProductoController` (Controlador REST):** Contiene los endpoints mapeados bajo la ruta base `/api/productos` para atender las peticiones HTTP y retornar las respuestas en formato JSON.
* **`ProductoService` (Servicio / Lógica de negocio):** Implementa las reglas para el manejo de la lista de productos en memoria, filtros por stock, búsquedas por ID y categoría, y los cálculos de resumen y mayor valor.

---

## 🔗 Tabla de Endpoints

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/productos` | Lista todos los productos registrados en la despensa. |
| `GET` | `/api/productos/{id}` | Busca un producto por su identificador único (devuelve 404 si no existe). |
| `GET` | `/api/productos/categoria/{categoria}` | Filtra y devuelve los productos que pertenecen a una categoría específica. |
| `GET` | `/api/productos/stock-bajo` | Devuelve los productos cuya cantidad sea igual o menor a 3. |
| `GET` | `/api/productos/mayor-valor` | Devuelve el producto con mayor valor total en el inventario. |
| `GET` | `/api/productos/resumen` | Retorna un resumen global con el total de productos, unidades físicas y valor monetario. |

---

## 🚀 Instrucciones para Ejecutar la Aplicación
1. Clona el repositorio o descarga el proyecto en tu computadora.
2. Abre el proyecto en **IntelliJ IDEA**.
3. Espera a que Maven descargue e indexe todas las dependencias del archivo `pom.xml`.
4. Ejecuta la clase principal del proyecto (la que contiene la anotación `@SpringBootApplication`).
5. Abre tu navegador web o cliente HTTP e ingresa a `http://localhost:8080/api/productos` para comenzar a interactuar con la API.

---

## 📄 Ejemplos de Respuestas JSON

### 1. Consultar un producto por ID (`GET /api/productos/3`)
```json
{
  "id": 3,
  "nombre": "Arroz Blanco",
  "categoria": "Granos",
  "cantidad": 10,
  "precioUnitario": 8.5
}
```

### 2. Resumen del inventario (`GET /api/productos/resumen`)
```json
{
  "totalProductos": 6,
  "totalUnidades": 25,
  "valorMonetarioTotal": 448.0
}
```