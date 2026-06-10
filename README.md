# Sistema de Gestión Hotelera

Sistema web desarrollado para la administración de un hotel, permitiendo la gestión de usuarios, clientes, habitaciones, categorías, pisos, recepciones, ventas y productos.

## Integrantes

* Grupo 7
* Cibertec

## Tecnologías Utilizadas

### Frontend

* Angular
* Bootstrap 5
* TypeScript
* SweetAlert2

### Backend

* Spring Boot
* Spring Security
* JWT
* JPA / Hibernate
* Maven

### Base de Datos

* PostgreSQL

## Funcionalidades

* Autenticación mediante JWT
* Gestión de usuarios
* Gestión de clientes
* Gestión de habitaciones
* Gestión de categorías
* Gestión de pisos
* Registro de recepciones
* Registro de ventas
* Control de habitaciones ocupadas
* Gestión de productos
* Registro de salida de habitaciones

## Estructura del Proyecto

```text
Proyecto Ht/
│
├── FrontendHotel/
├── BackendHotel/
└── hotel.sql
```

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/Yax-CalleCas/Sistema-de-Gesti-n-Hotelera.git
```

### 2. Base de Datos

Crear una base de datos PostgreSQL y ejecutar el script:

```sql
hotel.sql
```

### 3. Configurar Backend

Editar el archivo:

```properties
src/main/resources/application.properties
```

Configurar:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/nombre_bd
spring.datasource.username=postgres
spring.datasource.password=tu_password
```

### 4. Ejecutar Backend

```bash
mvn clean install
mvn spring-boot:run
```

Servidor:

```text
http://localhost:8081
```

### 5. Ejecutar Frontend

Ingresar a la carpeta del frontend:

```bash
cd hotel
```

Instalar dependencias:

```bash
npm install
```

Ejecutar Angular:

```bash
ng serve
```

Aplicación:

```text
http://localhost:4200
```

## Credenciales de Acceso

Registrar usuarios desde el sistema o utilizar los registros existentes en la base de datos.

## Repositorio

https://github.com/Yax-CalleCas/Sistema-de-Gesti-n-Hotelera

## Licencia

Proyecto académico desarrollado para fines educativos en Cibertec.
