package com.example.moeda_estudantil.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VantagemResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private String nomeEmpresa;
    private Double custoEmMoedas;
    private String urlFoto;
}

