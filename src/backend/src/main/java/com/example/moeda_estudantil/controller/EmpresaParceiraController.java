package com.example.moeda_estudantil.controller;

import com.example.moeda_estudantil.dto.EmpresaAtualizacaoDTO;
import com.example.moeda_estudantil.dto.EmpresaCadastroDTO;
import com.example.moeda_estudantil.dto.EmpresaResponseDTO;
import com.example.moeda_estudantil.service.EmpresaParceiraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaParceiraController {

    private final EmpresaParceiraService empresaParceiraService;

    public EmpresaParceiraController(EmpresaParceiraService empresaParceiraService) {
        this.empresaParceiraService = empresaParceiraService;
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(empresaParceiraService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empresaParceiraService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> cadastrar(@Valid @RequestBody EmpresaCadastroDTO dto) {
        EmpresaResponseDTO response = empresaParceiraService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaAtualizacaoDTO dto) {
        EmpresaResponseDTO response = empresaParceiraService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        empresaParceiraService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

