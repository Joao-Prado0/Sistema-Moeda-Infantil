package com.example.moeda_estudantil.controller;

import com.example.moeda_estudantil.dto.ConferirCupomRequestDTO;
import com.example.moeda_estudantil.dto.CupomEmpresaDTO;
import com.example.moeda_estudantil.dto.EmpresaAtualizacaoDTO;
import com.example.moeda_estudantil.dto.EmpresaCadastroDTO;
import com.example.moeda_estudantil.dto.EmpresaResponseDTO;
import com.example.moeda_estudantil.dto.VantagemDetalheDTO;
import com.example.moeda_estudantil.dto.VantagemEmpresaRequestDTO;
import com.example.moeda_estudantil.service.EmpresaParceiraService;
import com.example.moeda_estudantil.service.EmpresaVantagemService;
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
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaParceiraController {

    private final EmpresaParceiraService empresaParceiraService;
    private final EmpresaVantagemService empresaVantagemService;

    public EmpresaParceiraController(EmpresaParceiraService empresaParceiraService,
                                     EmpresaVantagemService empresaVantagemService) {
        this.empresaParceiraService = empresaParceiraService;
        this.empresaVantagemService = empresaVantagemService;
    }

    // ── Endpoints existentes (inalterados) ──────────────────────────────────

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
    public ResponseEntity<EmpresaResponseDTO> atualizar(@PathVariable Long id,
                                                         @Valid @RequestBody EmpresaAtualizacaoDTO dto) {
        EmpresaResponseDTO response = empresaParceiraService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        empresaParceiraService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ── Gestão de vantagens da empresa logada ───────────────────────────────

    /**
     * Lista as vantagens da empresa autenticada.
     * TODO: Quando JWT/session estiver disponível, extrair o id do SecurityContext.
     */
    @GetMapping("/me/vantagens")
    public ResponseEntity<List<VantagemDetalheDTO>> listarMinhasVantagens(
            @RequestHeader("X-Empresa-Id") Long empresaId) {
        return ResponseEntity.ok(empresaVantagemService.listarVantagensDaEmpresa(empresaId));
    }

    /**
     * Cria uma vantagem vinculada à empresa autenticada.
     * TODO: Quando JWT/session estiver disponível, extrair o id do SecurityContext.
     */
    @PostMapping("/me/vantagens")
    public ResponseEntity<VantagemDetalheDTO> criarVantagem(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @Valid @RequestBody VantagemEmpresaRequestDTO dto) {
        VantagemDetalheDTO criada = empresaVantagemService.criarVantagem(empresaId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    /**
     * Edita uma vantagem da empresa autenticada (404 se não pertencer à empresa).
     * TODO: Quando JWT/session estiver disponível, extrair o id do SecurityContext.
     */
    @PutMapping("/me/vantagens/{vantagemId}")
    public ResponseEntity<VantagemDetalheDTO> editarVantagem(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @PathVariable Long vantagemId,
            @Valid @RequestBody VantagemEmpresaRequestDTO dto) {
        return ResponseEntity.ok(empresaVantagemService.editarVantagem(empresaId, vantagemId, dto));
    }

    /**
     * Exclui uma vantagem da empresa autenticada.
     * Falha com 400 se houver cupons vinculados.
     * TODO: Quando JWT/session estiver disponível, extrair o id do SecurityContext.
     */
    @DeleteMapping("/me/vantagens/{vantagemId}")
    public ResponseEntity<Void> excluirVantagem(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @PathVariable Long vantagemId) {
        empresaVantagemService.excluirVantagem(empresaId, vantagemId);
        return ResponseEntity.noContent().build();
    }

    // ── Gestão de cupons da empresa logada ──────────────────────────────────

    /**
     * Lista todos os cupons das vantagens da empresa autenticada.
     * Status GERADO → "Pendente" no front. UTILIZADO → "Utilizado".
     * TODO: Quando JWT/session estiver disponível, extrair o id do SecurityContext.
     */
    @GetMapping("/me/cupons")
    public ResponseEntity<List<CupomEmpresaDTO>> listarMeusCupons(
            @RequestHeader("X-Empresa-Id") Long empresaId) {
        return ResponseEntity.ok(empresaVantagemService.listarCuponsDaEmpresa(empresaId));
    }

    /**
     * Confere (valida e marca como UTILIZADO) um cupom pelo código.
     * TODO: Quando JWT/session estiver disponível, extrair o id do SecurityContext.
     */
    @PostMapping("/me/cupons/conferir")
    public ResponseEntity<CupomEmpresaDTO> conferirCupom(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @Valid @RequestBody ConferirCupomRequestDTO dto) {
        return ResponseEntity.ok(empresaVantagemService.conferirCupom(empresaId, dto));
    }
}
