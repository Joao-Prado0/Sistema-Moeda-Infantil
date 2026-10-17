package com.example.moeda_estudantil.repository;

import com.example.moeda_estudantil.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByInstituicaoId(Long instituicaoId);
}

