package com.example.moeda_estudantil.dto;

import com.example.moeda_estudantil.model.enums.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Item do extrato do professor.
 * tipo: ENVIO (negativo) ou CREDITO_SEMESTRAL (positivo).
 * nomeAluno: preenchido para envios, null para crédito semestral.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtratoProfessorItemDTO {

    private LocalDateTime data;
    private TipoTransacao tipo;
    /** Nome do aluno beneficiado. Null para CREDITO_SEMESTRAL. */
    private String nomeAluno;
    private String motivo;
    /** Negativo para ENVIO, positivo para CREDITO_SEMESTRAL. */
    private Double valor;
}

