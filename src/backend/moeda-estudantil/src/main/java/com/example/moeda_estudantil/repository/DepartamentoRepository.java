package com.example.moeda_estudantil.repository;

import com.example.moeda_estudantil.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    List<Departamento> findByInstituicaoId(Long instituicaoId);
}

