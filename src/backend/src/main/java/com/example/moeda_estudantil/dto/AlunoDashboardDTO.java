package com.example.moeda_estudantil.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoDashboardDTO {

    private String nome;
    private Double saldoAtual;
    private Double totalRecebido;
    private Double totalTrocado;
    private List<ExtratoItemDTO> extrato;
}

