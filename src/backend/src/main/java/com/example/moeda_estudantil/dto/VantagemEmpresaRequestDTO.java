package com.example.moeda_estudantil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de criação e edição de vantagem pela empresa parceira.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VantagemEmpresaRequestDTO {

    @NotBlank(message = "O título da vantagem é obrigatório")
    private String titulo;

    @NotBlank(message = "A descrição da vantagem é obrigatória")
    private String descricao;

    /** URL da foto (opcional por enquanto; upload real fica para depois). */
    private String fotoUrl;

    @NotNull(message = "O custo em moedas é obrigatório")
    @Positive(message = "O custo em moedas deve ser maior que zero")
    private Double custoMoedas;
}

