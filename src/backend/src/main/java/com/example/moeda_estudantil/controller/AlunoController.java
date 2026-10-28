package com.example.moeda_estudantil.controller;

import com.example.moeda_estudantil.dto.AlunoDashboardDTO;
import com.example.moeda_estudantil.dto.AlunoAtualizacaoDTO;
import com.example.moeda_estudantil.dto.AlunoCadastroDTO;
import com.example.moeda_estudantil.dto.AlunoResponseDTO;
import com.example.moeda_estudantil.dto.CupomListagemDTO;
import com.example.moeda_estudantil.service.AlunoDashboardService;
import com.example.moeda_estudantil.service.AlunoService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*")
public class AlunoController {

    private final AlunoService alunoService;
    private final AlunoDashboardService alunoDashboardService;

    public AlunoController(AlunoService alunoService,
                           AlunoDashboardService alunoDashboardService) {
        this.alunoService = alunoService;
        this.alunoDashboardService = alunoDashboardService;
    }

    // ── Endpoints existentes ────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<AlunoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(alunoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AlunoResponseDTO> cadastrar(@Valid @RequestBody AlunoCadastroDTO dto) {
        AlunoResponseDTO response = alunoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody AlunoAtualizacaoDTO dto) {
        AlunoResponseDTO response = alunoService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        alunoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ── Novos endpoints: aluno logado ───────────────────────────────────────

    /**
     * Retorna o dashboard do aluno autenticado.
     * <p>
     * Como a autenticação por token ainda não está implementada, o id do aluno
     * é informado via header {@code X-Aluno-Id}.
     * TODO: Quando JWT/session estiver disponível, extrair o id do SecurityContext.
     */
    @GetMapping("/me/dashboard")
    public ResponseEntity<AlunoDashboardDTO> obterDashboard(
            @RequestHeader("X-Aluno-Id") Long alunoId) {
        return ResponseEntity.ok(alunoDashboardService.obterDashboard(alunoId));
    }

    /**
     * Lista os cupons do aluno autenticado.
     * <p>
     * TODO: Quando JWT/session estiver disponível, extrair o id do SecurityContext.
     */
    @GetMapping("/me/cupons")
    public ResponseEntity<List<CupomListagemDTO>> listarMeusCupons(
            @RequestHeader("X-Aluno-Id") Long alunoId) {
        return ResponseEntity.ok(alunoDashboardService.listarMeusCupons(alunoId));
    }
}
