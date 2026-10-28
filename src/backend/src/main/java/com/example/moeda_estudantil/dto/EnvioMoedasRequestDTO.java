package com.example.moeda_estudantil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class EnvioMoedasRequestDTO {

    @NotNull(message = "O id do aluno é obrigatório")
    private Long alunoId;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Double quantidade;

    @NotBlank(message = "O motivo é obrigatório")
    private String motivo;
}

