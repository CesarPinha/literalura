package com.literalura.principal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.literalura.dto.AutorDTO;
import com.literalura.dto.LibroDTO;
import com.literalura.dto.RespuestaLibrosDTO;
import com.literalura.model.Autor;
import com.literalura.model.Libro;
import com.literalura.service.AutorService;
import com.literalura.service.ConsumoAPI;
import com.literalura.service.ConvierteDatos;
import com.literalura.service.LibroService;

@Component
public class Principal {

    @Autowired private LibroService libroService;
    @Autowired private AutorService autorService;
    @Autowired private ConsumoAPI consumoAPI;
    @Autowired private ConvierteDatos convierteDatos;
    private static final String BASE_URL = "https://gutendex.com/books/";

    public void mostrarMenu() {
        try (Scanner sc = new Scanner(System.in)) {
            int opcion;
            do {
                imprimirMenu();
                opcion = sc.nextInt();
                sc.nextLine(); // consumir nueva línea
                switch (opcion) {
                    case 1 -> buscarLibroPorTitulo(sc);
                    case 2 -> listarLibrosRegistrados();
                    case 3 -> listarAutoresRegistrados();
                    case 4 -> listarAutoresVivosEnAno(sc);
                    case 5 -> listarLibrosPorIdioma(sc);
                    case 0 -> System.out.println("Hasta luego");
                    default -> System.out.println("Opción no válida, intente de nuevo");
                }
            } while (opcion != 0);
        }
    }

    private void imprimirMenu() {
        System.out.println("~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
        System.out.println("         🌟  Bienvenido a LITERALURA 🌟        ");
        System.out.println("~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
        System.out.println("1. Buscar libros por título");
        System.out.println("2. Listar libros registrados");
        System.out.println("3. Listar autores registrados");
        System.out.println("4. Autores vivos en un año");
        System.out.println("5. Listar libros por idioma");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private void buscarLibroPorTitulo(Scanner sc) {
        System.out.println("Ingrese el título del libro:");
        String titulo = sc.nextLine();
        try {
            String encodedTitulo = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
            String json = consumoAPI.obtenerDatos(BASE_URL + "?search=" + encodedTitulo);
            RespuestaLibrosDTO respuesta = convierteDatos.obtenerDatos(json, RespuestaLibrosDTO.class);
            List<LibroDTO> librosDTO = respuesta.getLibros();

            if (librosDTO.isEmpty()) {
                System.out.println("No se encontraron libros con el título ingresado");
                return;
            }

            boolean encontrado = false;
            for (LibroDTO libroDTO : librosDTO) {
                if (libroDTO.getTitulo().equalsIgnoreCase(titulo)) {
                    Optional<Libro> libroExistente = libroService.buscarLibroPorTitulo(titulo);
                    if (libroExistente.isPresent()) {
                        tratarLibroExistente(libroExistente.get(), sc);
                    } else {
                        registrarNuevoLibro(libroDTO, sc);
                    }
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                System.out.println("No se encontró un libro con el título ingresado");
            }
        } catch (Exception e) {
            System.out.println("Error al buscar libros: " + e.getMessage());
        }
    }

    private void tratarLibroExistente(Libro libro, Scanner sc) {
        System.out.println("El libro ya está registrado");
        System.out.println("¿Desea ver los detalles? (s/n)");
        String respuesta = sc.nextLine();
        if ("s".equalsIgnoreCase(respuesta)) {
            System.out.println(libro);
        }
        System.out.println("No se puede registrar un libro que ya existe");
    }

    private void registrarNuevoLibro(LibroDTO libroDTO, Scanner sc) {
        Libro libro = new Libro();
        libro.setTitulo(libroDTO.getTitulo());
        libro.setIdioma(libroDTO.getIdiomas().get(0));
        libro.setNumeroDescargas(libroDTO.getNumeroDescargas());

        AutorDTO primerAutorDTO = libroDTO.getAutores().get(0);
        Autor autor = autorService.buscarAutorPorNombre(primerAutorDTO.getNombre())
                .orElseGet(() -> {
                    Autor nuevoAutor = new Autor();
                    nuevoAutor.setNombre(primerAutorDTO.getNombre());
                    nuevoAutor.setAnoNacimiento(primerAutorDTO.getAnoNacimiento());
                    nuevoAutor.setAnoFallecimiento(primerAutorDTO.getAnoFallecimiento());
                    return autorService.crearAutor(nuevoAutor);
                });

        libro.setAutor(autor);
        libroService.crearLibro(libro);
        System.out.println("Libro registrado exitosamente: " + libro.getTitulo());
        mostrarDetallesLibro(libroDTO);
    }

    private void listarLibrosRegistrados() {
        libroService.listarLibros().forEach(libro -> {
            System.out.println("-------LIBRO-------");
            System.out.printf("Título: %s%nAutor: %s%nIdioma: %s%nDescargas: %d%n",
                    libro.getTitulo(),
                    (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"),
                    libro.getIdioma(),
                    libro.getNumeroDescargas());
        });
    }

    private void listarAutoresRegistrados() {
        autorService.listarAutores().forEach(autor -> {
            System.out.println("-------AUTOR-------");
            System.out.printf("Nombre: %s%nNacimiento: %s%nFallecimiento: %s%nLibros: [ %s ]%n",
                    autor.getNombre(),
                    autor.getAnoNacimiento(),
                    autor.getAnoFallecimiento(),
                    autor.getLibros().stream().map(Libro::getTitulo).collect(Collectors.joining(", ")));
        });
    }

    private void listarAutoresVivosEnAno(Scanner sc) {
        System.out.println("Ingrese el año para buscar autores vivos:");
        int ano = sc.nextInt();
        sc.nextLine();
        List<Autor> autoresVivos = autorService.listarAutoresVivosEnAno(ano);
        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año ingresado");
        } else {
            autoresVivos.forEach(autor -> {
                System.out.println("-------AUTOR-------");
                System.out.printf("Nombre: %s%nNacimiento: %s%nFallecimiento: %s%nLibros escritos: %d%n",
                        autor.getNombre(),
                        autor.getAnoNacimiento(),
                        autor.getAnoFallecimiento(),
                        autor.getLibros().size());
            });
        }
    }

    private void listarLibrosPorIdioma(Scanner sc) {
        System.out.println("Ingrese el idioma del libro que desea buscar:");
        System.out.println("Opciones: es, en, fr, pt");
        String idioma = sc.nextLine().trim().toLowerCase();
        if (List.of("es", "en", "fr", "pt").contains(idioma)) {
            libroService.listarLibrosPorIdioma(idioma).forEach(libro -> {
                System.out.println("-------LIBRO-------");
                System.out.printf("Título: %s%nAutor: %s%nIdioma: %s%nDescargas: %d%n",
                        libro.getTitulo(),
                        (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"),
                        libro.getIdioma(),
                        libro.getNumeroDescargas());
            });
        } else {
            System.out.println("Idioma no válido");
        }
    }

    private void mostrarDetallesLibro(LibroDTO libroDTO) {
        System.out.println("-------DETALLES DEL LIBRO-------");
        System.out.printf("Título: %s%nAutores:%n", libroDTO.getTitulo());
        for (AutorDTO autor : libroDTO.getAutores()) {
            System.out.printf("Nombre: %s | Nacimiento: %s | Fallecimiento: %s%n",
                    autor.getNombre(), autor.getAnoNacimiento(), autor.getAnoFallecimiento());
        }
        System.out.printf("Idiomas: %s%nDescargas: %d%n",
                libroDTO.getIdiomas(), libroDTO.getNumeroDescargas());
    }
}
