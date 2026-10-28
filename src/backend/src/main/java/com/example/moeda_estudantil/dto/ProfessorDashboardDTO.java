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
public class ProfessorDashboardDTO {

    private String nome;
    private String cpf;
    private String departamentoNome;
    private String instituicaoNome;
    private Double saldoDisponivel;
    /** Crédito semestral fixo: 1000 moedas. */
    private Double creditoSemestral;
    private Double totalEnviado;
    private List<ExtratoProfessorItemDTO> extrato;
}
