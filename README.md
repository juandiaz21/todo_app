# todo_app

# Todo App - Spring Boot + MariaDB + JWT

Esta es una TODO_APP para una prueba técnica construida con Spring Boot, JWT para autenticación y Swagger para documentación de la API.

# Requisitos

Antes de iniciar, asegúrate de tener instalado:

- Java 17+

- Maven 3.6+

- MariaDB/MySQL

- Navegador web y/o cliente REST (Postman, etc.)

# Configuración

## Base de Datos:

Crea una base de datos llamada todo_db en tu instancia de MariaDB:

```sql
CREATE DATABASE todo_db;
```

Archivo application.properties:

El archivo ya incluye la configuración para conectarse a MariaDB con el usuario root y contraseña root:

```properties
spring.datasource.url=jdbc:mariadb://127.0.0.1:3306/todo_db
spring.datasource.username=root
spring.datasource.password=root
```

Asegúrate de que el usuario root tenga acceso a esa base de datos o cambia las credenciales según tu entorno. Ajusta si es necessario.

# Ejecución

Desde la raíz del proyecto, ejecuta:

```bash
./mvnw spring-boot:run
```

O si estás en Windows:

```bash
mvnw.cmd spring-boot:run
```

La aplicación arrancará en: http://localhost:8080

# Autenticación

La aplicación utiliza JWT para proteger los endpoints. No se usa autenticación básica ni credenciales por defecto.

## Registro y Login

- Registro: POST /api/users/register

- Login: POST /api/users/login

# Swagger (API REST)

Puedes probar la API desde la documentación Swagger en:

- http://localhost:8080/swagger-ui-custom.html