package com.example.moeda_estudantil.controller;

import com.example.moeda_estudantil.dto.CupomResponseDTO;
import com.example.moeda_estudantil.dto.VantagemResponseDTO;
import com.example.moeda_estudantil.service.VantagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vantagens")
@CrossOrigin(origins = "*")
public class VantagemController {

    private final VantagemService vantagemService;

    public VantagemController(VantagemService vantagemService) {
        this.vantagemService = vantagemService;
    }

    /**
     * Catálogo público de vantagens disponíveis.
     */
    @GetMapping
    public ResponseEntity<List<VantagemResponseDTO>> listarCatalogo() {
        return ResponseEntity.ok(vantagemService.listarCatalogo());
    }

    /**
     * Resgata uma vantagem para o aluno autenticado.
     * <p>
     * TODO: Quando JWT/session estiver disponível, extrair o id do SecurityContext
     *       em vez de usar o header {@code X-Aluno-Id}.
     */
    @PostMapping("/{id}/resgate")
    public ResponseEntity<CupomResponseDTO> resgatar(
            @PathVariable Long id,
            @RequestHeader("X-Aluno-Id") Long alunoId) {
        CupomResponseDTO cupom = vantagemService.resgatar(id, alunoId);
        return ResponseEntity.ok(cupom);
    }
}

