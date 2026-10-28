package com.example.moeda_estudantil.dto;

import com.example.moeda_estudantil.model.enums.StatusCupom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de cupom visto pela empresa parceira.
 * Exibe código, nome do aluno, título da vantagem, data e status.
 * O front interpreta GERADO como "Pendente" e UTILIZADO como "Utilizado".
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CupomEmpresaDTO {

    private Long id;
    private String codigo;
    private String nomeAluno;
    private String tituloVantagem;
    private LocalDateTime dataResgate;
    private StatusCupom status;
}

