package com.example.moeda_estudantil.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AlunoAtualizacaoDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "RG é obrigatório")
    private String rg;

    @NotNull(message = "Endereço é obrigatório")
    @Valid
    private EnderecoDTO endereco;

    @NotNull(message = "Instituição de ensino é obrigatória")
    private Long instituicaoId;

    @NotNull(message = "Curso é obrigatório")
    private Long cursoId;
}

