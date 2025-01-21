# Literalura
_Challenge Spring Boot Literalura de la especialización BackEnd del programa de One Oracle Next Education_

## Objetivo

Este proyecto tiene como objetivo desarrollar un catálogo de libros interactivo a través de consola. Los libros se obtienen mediante una API específica. La aplicación permite realizar búsquedas y ver información detallada sobre libros y autores.

## Funcionalidades

1. **Buscar libro por título**: Permite buscar libros ingresando su título.
2. **Listar libros registrados**: Muestra todos los libros registrados sin repeticiones.
3. **Listar autores registrados**: Muestra todos los autores registrados en la base de datos.
4. **Listar autores vivos en un determinado año**: Permite consultar los autores que estaban vivos en un año específico.
5. **Listar libros por idioma**: Filtra y muestra libros según el idioma seleccionado. Los idiomas disponibles son español, inglés y francés
6. **Salir**: Cierra la aplicación.

## Tecnologías

- **Spring Boot**: Para el desarrollo de la aplicación.
- **PostgreSQL**: Base de datos relacional.
- **HikariCP**: Pool de conexiones para la base de datos.
- **JPA (Hibernate)**: Para interactuar con la base de datos.
- **API externa de libros**: Se utilizó Gutendex

## Instrucciones

1. Clone este repositorio.
2. Configure su base de datos en el archivo `application.properties`.
3. Ejecute la aplicación con `mvn spring-boot:run`.
4. Interactúa con el catálogo de libros a través de la consola.
