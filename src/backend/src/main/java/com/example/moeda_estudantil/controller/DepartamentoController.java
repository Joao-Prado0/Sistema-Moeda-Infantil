package com.example.moeda_estudantil.controller;

import com.example.moeda_estudantil.model.Departamento;
import com.example.moeda_estudantil.repository.DepartamentoRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@CrossOrigin(origins = "*")
public class DepartamentoController {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoController(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    @GetMapping
    public List<DepartamentoItem> listar() {
        return departamentoRepository.findAll().stream()
                .map(d -> new DepartamentoItem(d.getId(), d.getNome()))
                .toList();
    }

    public record DepartamentoItem(Long id, String nome) {}
}