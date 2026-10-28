package com.example.moeda_estudantil.dto;

import com.example.moeda_estudantil.model.enums.StatusCupom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de listagem de cupons do aluno (GET /api/alunos/me/cupons).
 * Não expõe saldoRestante, pois não faz sentido no contexto de listagem.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CupomListagemDTO {

    private Long id;
    private String codigo;
    private String nomeVantagem;
    private String nomeEmpresa;
    private Double custo;
    private StatusCupom status;
    private LocalDateTime dataResgate;
}

