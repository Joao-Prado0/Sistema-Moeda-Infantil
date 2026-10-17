package com.example.moeda_estudantil.repository;

import com.example.moeda_estudantil.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    Optional<Aluno> findByEmail(String email);
    boolean existsByEmail(String email);
}

