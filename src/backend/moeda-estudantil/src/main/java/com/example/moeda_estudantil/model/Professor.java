package com.example.moeda_estudantil.model;

import com.example.moeda_estudantil.model.enums.PerfilUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "professores")
@Getter
@Setter
public class Professor extends Usuario {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private Double saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id")
    private Instituicao instituicao;

    public Professor() {
        super();
    }

    public Professor(Long id, String email, String senha, PerfilUsuario perfil, LocalDateTime dataCriacao,
                     String nome, String cpf, Double saldo, Departamento departamento, Instituicao instituicao) {
        super(id, email, senha, perfil, dataCriacao);
        this.nome = nome;
        this.cpf = cpf;
        this.saldo = saldo;
        this.departamento = departamento;
        this.instituicao = instituicao;
    }
}

