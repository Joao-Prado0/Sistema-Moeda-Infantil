package com.example.moeda_estudantil.model;

import com.example.moeda_estudantil.model.enums.PerfilUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alunos")
@Getter
@Setter
public class Aluno extends Usuario {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String rg;

    @Column(nullable = false)
    private Double saldo;

    @Embedded
    private Endereco endereco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id")
    private Instituicao instituicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    public Aluno() {
        super();
    }

    public Aluno(Long id, String email, String senha, PerfilUsuario perfil, LocalDateTime dataCriacao,
                 String nome, String cpf, String rg, Double saldo, Endereco endereco,
                 Instituicao instituicao, Curso curso) {
        super(id, email, senha, perfil, dataCriacao);
        this.nome = nome;
        this.cpf = cpf;
        this.rg = rg;
        this.saldo = saldo;
        this.endereco = endereco;
        this.instituicao = instituicao;
        this.curso = curso;
    }
}

