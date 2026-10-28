package com.example.moeda_estudantil.controller;

import com.example.moeda_estudantil.dto.EnvioMoedasRequestDTO;
import com.example.moeda_estudantil.dto.EnvioMoedasResponseDTO;
import com.example.moeda_estudantil.dto.ProfessorDashboardDTO;
import com.example.moeda_estudantil.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/professores")
@CrossOrigin(origins = "*")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    /**
     * Dashboard do professor autenticado.
     */
    @GetMapping("/me/dashboard")
    public ResponseEntity<ProfessorDashboardDTO> obterDashboard(
            @RequestHeader("X-Professor-Id") Long professorId) {
        return ResponseEntity.ok(professorService.obterDashboard(professorId));
    }

    /**
     * Envia moedas para um aluno.
     */
    @PostMapping("/me/envios")
    public ResponseEntity<EnvioMoedasResponseDTO> enviarMoedas(
            @RequestHeader("X-Professor-Id") Long professorId,
            @Valid @RequestBody EnvioMoedasRequestDTO dto) {
        return ResponseEntity.ok(professorService.enviarMoedas(professorId, dto));
    }

    /**
     * Dispara o crédito semestral acumulável (1.000 moedas) para todos os professores cadastrados.
     */
    @PostMapping("/credito-semestral")
    public ResponseEntity<Map<String, Object>> aplicarCreditoSemestral() {
        var professores = professorService.aplicarCreditoSemestralEmLote();
        return ResponseEntity.ok(Map.of(
                "mensagem", "Crédito semestral de 1.000 moedas aplicado com sucesso a todos os professores.",
                "totalProfessoresCreditados", professores.size()
        ));
    }
}
