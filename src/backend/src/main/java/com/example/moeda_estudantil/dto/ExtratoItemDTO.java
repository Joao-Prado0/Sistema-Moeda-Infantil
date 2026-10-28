package com.example.moeda_estudantil.dto;

import com.example.moeda_estudantil.model.enums.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtratoItemDTO {

    private LocalDateTime data;
    private TipoTransacao tipo;
    private String descricao;
    /**
     * Positivo para recebimentos (BONIFICACAO, CREDITO_SEMESTRAL),
     * negativo para resgates (RESGATE_VANTAGEM).
     */
    private Double valor;
}

