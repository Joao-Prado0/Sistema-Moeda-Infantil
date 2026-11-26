package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.exception.ConflitoException;
import com.example.moeda_estudantil.exception.RecursoNaoEncontradoException;
import com.example.moeda_estudantil.model.Professor;
import com.example.moeda_estudantil.model.Transacao;
import com.example.moeda_estudantil.model.enums.TipoTransacao;
import com.example.moeda_estudantil.repository.ProfessorRepository;
import com.example.moeda_estudantil.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Regra do crédito semestral de moedas aos professores: a cada semestre cada
 * professor recebe 1.000 moedas, acumuláveis com o saldo atual.
 *
 * <p>GRASP: Alta Coesão + Baixo Acoplamento. A regra saiu do
 * {@link ProfessorService}, que misturava dashboard, envio de moedas, CRUD e
 * crédito, e do {@code AdminController}, que calculava o semestre. Agora o
 * cálculo do semestre e o lançamento do crédito existem num único lugar.</p>
 */
@Service
public class CreditoSemestralService {

    /** Crédito semestral padrão em moedas. */
    public static final double VALOR_SEMESTRAL = 1000.0;

    private final ProfessorRepository professorRepository;
    private final TransacaoRepository transacaoRepository;

    public CreditoSemestralService(ProfessorRepository professorRepository,
                                   TransacaoRepository transacaoRepository) {
        this.professorRepository = professorRepository;
        this.transacaoRepository = transacaoRepository;
    }

    /**
     * Semestre letivo de uma data no formato "AAAA/S": janeiro a junho é 1, julho a dezembro é 2.
     */
    public static String semestreDe(LocalDate data) {
        return data.getYear() + "/" + (data.getMonthValue() <= 6 ? "1" : "2");
    }

    /** Semestre letivo da data atual. */
    public String semestreAtual() {
        return semestreDe(LocalDate.now());
    }

    /**
     * Aplica o crédito a todos os professores uma única vez por semestre (idempotente).
     *
     * @param semestre período (ex.: "2026/2"); se nulo ou vazio, usa o semestre atual
     * @return quantidade de professores creditados
     * @throws ConflitoException se o semestre já tiver sido creditado
     */
    @Transactional
    public int aplicarParaTodos(String semestre) {
        String periodo = (semestre == null || semestre.isBlank()) ? semestreAtual() : semestre;
        String motivo = "Crédito semestral " + periodo;

        if (transacaoRepository.existsByTipoAndMotivo(TipoTransacao.CREDITO_SEMESTRAL, motivo)) {
            throw new ConflitoException("O crédito semestral para o período " + periodo + " já foi aplicado anteriormente.");
        }

        List<Professor> professores = professorRepository.findAll();
        LocalDateTime agora = LocalDateTime.now();
        for (Professor professor : professores) {
            creditar(professor, motivo, agora);
        }
        return professores.size();
    }

    /**
     * Credita {@value #VALOR_SEMESTRAL} moedas a um professor e registra a transação.
     */
    @Transactional
    public void aplicarParaProfessor(Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Professor não encontrado com id: " + professorId));
        creditar(professor, "Crédito semestral", LocalDateTime.now());
    }

    /**
     * Credita todos os professores cadastrados, sem controle de semestre.
     */
    @Transactional
    public List<Professor> aplicarEmLote() {
        List<Professor> professores = professorRepository.findAll();
        LocalDateTime agora = LocalDateTime.now();
        for (Professor professor : professores) {
            creditar(professor, "Crédito semestral", agora);
        }
        return professores;
    }

    private void creditar(Professor professor, String motivo, LocalDateTime dataHora) {
        professor.setSaldo(professor.getSaldo() + VALOR_SEMESTRAL);
        professorRepository.save(professor);

        transacaoRepository.save(Transacao.builder()
                .quantidadeMoedas(VALOR_SEMESTRAL)
                .dataHora(dataHora)
                .tipo(TipoTransacao.CREDITO_SEMESTRAL)
                .motivo(motivo)
                .professor(professor)
                .build());
    }
}
