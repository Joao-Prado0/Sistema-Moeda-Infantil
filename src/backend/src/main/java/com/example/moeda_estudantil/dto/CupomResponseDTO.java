package com.example.moeda_estudantil.dto;

import com.example.moeda_estudantil.model.enums.StatusCupom;
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
public class CupomResponseDTO {

    private Long id;
    private String codigo;
    private String nomeVantagem;
    private String nomeEmpresa;
    private Double custo;
    private Double saldoRestante;
    private StatusCupom status;
    private LocalDateTime dataResgate;
}

