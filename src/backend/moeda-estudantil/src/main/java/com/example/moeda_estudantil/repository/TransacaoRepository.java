package com.example.moeda_estudantil.repository;

import com.example.moeda_estudantil.model.Transacao;
import com.example.moeda_estudantil.model.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByAlunoId(Long alunoId);
    List<Transacao> findByProfessorId(Long professorId);
    List<Transacao> findByTipo(TipoTransacao tipo);
}

