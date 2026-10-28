package com.example.moeda_estudantil.dto;

import com.example.moeda_estudantil.model.enums.PerfilUsuario;
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
public class LoginResponseDTO {

    private Long id;
    private String email;
    private String nome;
    private PerfilUsuario perfil;
    private Double saldo;
    private String token;
}

