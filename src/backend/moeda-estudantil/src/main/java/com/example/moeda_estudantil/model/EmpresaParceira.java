package com.example.moeda_estudantil.model;

import com.example.moeda_estudantil.model.enums.PerfilUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "empresas_parceiras")
@Getter
@Setter
public class EmpresaParceira extends Usuario {

    @Column(nullable = false)
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
    private String cnpj;

    public EmpresaParceira() {
        super();
    }

    public EmpresaParceira(Long id, String email, String senha, PerfilUsuario perfil, LocalDateTime dataCriacao,
                           String nomeFantasia, String cnpj) {
        super(id, email, senha, perfil, dataCriacao);
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
    }
}

