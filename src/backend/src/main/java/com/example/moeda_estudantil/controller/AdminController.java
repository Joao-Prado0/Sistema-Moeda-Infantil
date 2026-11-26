package com.example.moeda_estudantil.controller;

import com.example.moeda_estudantil.dto.CreditoSemestralRequestDTO;
import com.example.moeda_estudantil.exception.AcessoNegadoException;
import com.example.moeda_estudantil.service.CreditoSemestralService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final CreditoSemestralService creditoSemestralService;
    private final String adminKey;

    public AdminController(CreditoSemestralService creditoSemestralService,
                           @Value("${app.admin.key:${ADMIN_KEY:admin-secret-key}}") String adminKey) {
        this.creditoSemestralService = creditoSemestralService;
        this.adminKey = adminKey;
    }

    /**
     * Dispara o crédito semestral acumulável de 1.000 moedas para cada professor cadastrado.
     * Protegido pelo header X-Admin-Key.
     */
    @PostMapping("/creditos-semestrais")
    public ResponseEntity<Map<String, Object>> aplicarCreditosSemestrais(
            @RequestHeader(value = "X-Admin-Key", required = false) String chaveRecebida,
            @RequestBody(required = false) CreditoSemestralRequestDTO dto) {

        if (chaveRecebida == null || !chaveRecebida.equals(this.adminKey)) {
            throw new AcessoNegadoException("Chave administrativa inválida ou não fornecida no cabeçalho X-Admin-Key.");
        }

        String semestre = (dto != null && dto.getSemestre() != null && !dto.getSemestre().isBlank())
                ? dto.getSemestre().trim()
                : creditoSemestralService.semestreAtual();

        int totalCreditados = creditoSemestralService.aplicarParaTodos(semestre);

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("semestre", semestre);
        resposta.put("professoresCreditados", totalCreditados);
        if (totalCreditados == 0) {
            resposta.put("mensagem", "Nenhum professor cadastrado no sistema para creditar.");
        } else {
            resposta.put("mensagem", String.format(
                    "Crédito semestral de 1.000 moedas aplicado com sucesso a %d professor(es) referente ao semestre %s.",
                    totalCreditados, semestre));
        }

        return ResponseEntity.ok(resposta);
    }
}
