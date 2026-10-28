package com.example.moeda_estudantil.repository;

import com.example.moeda_estudantil.model.Cupom;
import com.example.moeda_estudantil.model.enums.StatusCupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /** Verifica se há cupons vinculados a uma vantagem (usado antes de excluir). */
    boolean existsByVantagemId(Long vantagemId);

    /**
     * Lista todos os cupons das vantagens de uma empresa parceira.
     * Necessário para GET /api/empresas/me/cupons.
     */
    @Query("SELECT c FROM Cupom c " +
           "JOIN c.vantagem v " +
           "WHERE v.empresa.id = :empresaId")
    List<Cupom> findByVantagemEmpresaId(@Param("empresaId") Long empresaId);
}
