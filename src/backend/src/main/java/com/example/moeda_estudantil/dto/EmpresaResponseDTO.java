package com.example.moeda_estudantil.dto;

import com.example.moeda_estudantil.model.enums.PerfilUsuario;
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
public class EmpresaResponseDTO {

    private Long id;
    private String email;
    private PerfilUsuario perfil;
    private LocalDateTime dataCriacao;
    private String nomeFantasia;
    private String cnpj;
}

