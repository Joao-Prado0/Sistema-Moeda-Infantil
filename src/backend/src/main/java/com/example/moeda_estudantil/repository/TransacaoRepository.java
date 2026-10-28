package com.example.moeda_estudantil.repository;

import com.example.moeda_estudantil.model.Transacao;
import com.example.moeda_estudantil.model.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByAlunoId(Long alunoId);
    List<Transacao> findByProfessorId(Long professorId);
    List<Transacao> findByTipo(TipoTransacao tipo);

    @Query("SELECT COALESCE(SUM(t.quantidadeMoedas), 0) FROM Transacao t " +
           "WHERE t.aluno.id = :alunoId AND t.tipo IN :tipos")
    Double sumByAlunoIdAndTipoIn(@Param("alunoId") Long alunoId,
                                  @Param("tipos") List<TipoTransacao> tipos);
}
