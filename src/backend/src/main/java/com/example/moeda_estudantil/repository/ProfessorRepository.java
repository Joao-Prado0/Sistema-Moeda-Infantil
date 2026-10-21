package com.example.moeda_estudantil.repository;

import com.example.moeda_estudantil.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    Optional<Professor> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Professor> findByInstituicaoId(Long instituicaoId);
    List<Professor> findByDepartamentoId(Long departamentoId);
}

