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
public class AlunoResponseDTO {

    private Long id;
    private String email;
    private PerfilUsuario perfil;
    private LocalDateTime dataCriacao;
    private String nome;
    private String cpf;
    private String rg;
    private Double saldo;
    private EnderecoDTO endereco;
    private Long instituicaoId;
    private String instituicaoNome;
    private Long cursoId;
    private String cursoNome;
}

