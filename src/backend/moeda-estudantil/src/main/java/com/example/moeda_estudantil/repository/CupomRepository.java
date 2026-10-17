package com.example.moeda_estudantil.repository;

import com.example.moeda_estudantil.model.Cupom;
import com.example.moeda_estudantil.model.enums.StatusCupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, Long> {
    Optional<Cupom> findByCodigo(UUID codigo);
    List<Cupom> findByAlunoId(Long alunoId);
    List<Cupom> findByVantagemId(Long vantagemId);
    List<Cupom> findByStatus(StatusCupom status);
}

