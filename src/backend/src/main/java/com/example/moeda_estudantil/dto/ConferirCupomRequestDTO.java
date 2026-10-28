package com.example.moeda_estudantil.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body do POST /api/empresas/me/cupons/conferir.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConferirCupomRequestDTO {

    @NotBlank(message = "O código do cupom é obrigatório")
    private String codigo;
}

