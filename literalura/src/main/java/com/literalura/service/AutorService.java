package com.literalura.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.literalura.model.Autor;
import com.literalura.repository.AutorRepository;

@Service
public class AutorService {
    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> listarAutores() {
        return autorRepository.findAllConLibros();
    }

    public List<Autor> listarAutoresVivosEnAno(int ano) {
        return autorRepository.findAutoresVivosEnAnoConLibros(ano);
    }

    public Autor crearAutor(Autor autor) {
        return autorRepository.save(autor);
    }

    public Optional<Autor> buscarAutorPorId(long id) {
        return autorRepository.findById(id);
    }

    public Optional<Autor> buscarAutorPorNombre(String nombre) {
        return autorRepository.findByNombre(nombre);
    }

    public Autor actualizarAutor(long id, Autor autorDetalles) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el autor con el ID " + id));
        autor.setNombre(autorDetalles.getNombre());
        autor.setAnoNacimiento(autorDetalles.getAnoNacimiento());
        autor.setAnoFallecimiento(autorDetalles.getAnoFallecimiento());
        autor.setLibros(autorDetalles.getLibros());
        return autorRepository.save(autor);
    }

    public void EliminarAutor(long id) {
        autorRepository.deleteById(id);
    }
}
