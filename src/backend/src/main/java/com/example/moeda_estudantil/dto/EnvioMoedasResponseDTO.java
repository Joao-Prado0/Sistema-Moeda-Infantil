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
public class EnvioMoedasResponseDTO {

    private Long transacaoId;
    private Double quantidade;
    private String motivo;
    private TipoTransacao tipo;
    private LocalDateTime dataHora;
    private String nomeAluno;
    private Double saldoRestanteProfessor;
}

