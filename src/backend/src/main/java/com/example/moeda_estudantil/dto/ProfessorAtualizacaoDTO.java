package com.example.moeda_estudantil.dto;

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
public class ProfessorAtualizacaoDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotNull(message = "Instituição de ensino é obrigatória")
    private Long instituicaoId;

    @NotNull(message = "Departamento é obrigatório")
    private Long departamentoId;
}

