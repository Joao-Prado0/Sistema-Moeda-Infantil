package com.example.moeda_estudantil.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de resposta de vantagem para gestão da empresa parceira.
 * Expõe os campos no formato nativo da entidade (titulo, fotoUrl, custoMoedas).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VantagemDetalheDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private String fotoUrl;
    private Double custoMoedas;
}

