package com.example.moeda_estudantil.controller;

import com.example.moeda_estudantil.model.Curso;
import com.example.moeda_estudantil.model.Instituicao;
import com.example.moeda_estudantil.repository.CursoRepository;
import com.example.moeda_estudantil.repository.InstituicaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instituicoes")
@CrossOrigin(origins = "*")
public class InstituicaoController {

    private final InstituicaoRepository instituicaoRepository;
    private final CursoRepository cursoRepository;

    public InstituicaoController(InstituicaoRepository instituicaoRepository, CursoRepository cursoRepository) {
        this.instituicaoRepository = instituicaoRepository;
        this.cursoRepository = cursoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Instituicao>> listarInstituicoes() {
        return ResponseEntity.ok(instituicaoRepository.findAll());
    }

    @GetMapping("/cursos")
    public ResponseEntity<List<Curso>> listarCursos() {
        return ResponseEntity.ok(cursoRepository.findAll());
    }
}

