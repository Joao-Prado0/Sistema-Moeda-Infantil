package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.dto.EnvioMoedasRequestDTO;
import com.example.moeda_estudantil.dto.EnvioMoedasResponseDTO;
import com.example.moeda_estudantil.dto.ExtratoProfessorItemDTO;
import com.example.moeda_estudantil.dto.ProfessorDashboardDTO;
import com.example.moeda_estudantil.exception.RecursoNaoEncontradoException;
import com.example.moeda_estudantil.exception.RegraNegocioException;
import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.Professor;
import com.example.moeda_estudantil.model.Transacao;
import com.example.moeda_estudantil.model.enums.TipoTransacao;
import com.example.moeda_estudantil.repository.AlunoRepository;
import com.example.moeda_estudantil.repository.ProfessorRepository;
import com.example.moeda_estudantil.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessorService {

    /** Crédito semestral padrão em moedas. */
    public static final double CREDITO_SEMESTRAL = 1000.0;

    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final TransacaoRepository transacaoRepository;
    private final EmailService emailService;

    public ProfessorService(ProfessorRepository professorRepository,
                            AlunoRepository alunoRepository,
                            TransacaoRepository transacaoRepository,
                            EmailService emailService) {
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.transacaoRepository = transacaoRepository;
        this.emailService = emailService;
    }

    // ── Dashboard ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ProfessorDashboardDTO obterDashboard(Long professorId) {
        Professor professor = buscarProfessorOuLancar(professorId);

        List<Transacao> transacoes = transacaoRepository.findByProfessorId(professorId);

        double totalEnviado = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.ENVIO)
                .mapToDouble(Transacao::getQuantidadeMoedas)
                .sum();

        List<ExtratoProfessorItemDTO> extrato = transacoes.stream()
                .sorted(Comparator.comparing(Transacao::getDataHora).reversed())
                .map(this::toExtratoProfessorItem)
                .collect(Collectors.toList());

        return ProfessorDashboardDTO.builder()
                .nome(professor.getNome())
                .cpf(professor.getCpf())
                .departamentoNome(professor.getDepartamento() != null ? professor.getDepartamento().getNome() : null)
                .instituicaoNome(professor.getInstituicao() != null ? professor.getInstituicao().getNome() : null)
                .saldoDisponivel(professor.getSaldo())
                .creditoSemestral(CREDITO_SEMESTRAL)
                .totalEnviado(totalEnviado)
                .extrato(extrato)
                .build();
    }

    // ── Envio de moedas ──────────────────────────────────────────────────────

    @Transactional
    public EnvioMoedasResponseDTO enviarMoedas(Long professorId, EnvioMoedasRequestDTO dto) {
        // Validações de negócio além das anotações Bean Validation
        if (dto.getMotivo() == null || dto.getMotivo().isBlank()) {
            throw new RegraNegocioException("O motivo do envio é obrigatório e não pode ser vazio.");
        }
        if (dto.getQuantidade() == null || dto.getQuantidade() <= 0) {
            throw new RegraNegocioException("A quantidade de moedas deve ser maior que zero.");
        }

        Professor professor = buscarProfessorOuLancar(professorId);

        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aluno não encontrado com id: " + dto.getAlunoId()));

        double quantidade = dto.getQuantidade();

        if (professor.getSaldo() < quantidade) {
            throw new RegraNegocioException(
                    "Saldo insuficiente para realizar o envio. " +
                    "Saldo disponível: " + professor.getSaldo() + " moedas. " +
                    "Tentativa de envio: " + quantidade + " moedas.");
        }

        // Debitar do professor
        professor.setSaldo(professor.getSaldo() - quantidade);
        professorRepository.save(professor);

        // Creditar no aluno
        aluno.setSaldo(aluno.getSaldo() + quantidade);
        alunoRepository.save(aluno);

        LocalDateTime agora = LocalDateTime.now();

        // Transação do professor (débito)
        Transacao transacaoProfessor = Transacao.builder()
                .quantidadeMoedas(quantidade)
                .dataHora(agora)
                .tipo(TipoTransacao.ENVIO)
                .motivo(dto.getMotivo())
                .professor(professor)
                .build();
        Transacao salva = transacaoRepository.save(transacaoProfessor);

        // Transação do aluno (crédito por bonificação)
        Transacao transacaoAluno = Transacao.builder()
                .quantidadeMoedas(quantidade)
                .dataHora(agora)
                .tipo(TipoTransacao.BONIFICACAO)
                .motivo(dto.getMotivo())
                .aluno(aluno)
                .professor(professor)
                .build();
        transacaoRepository.save(transacaoAluno);

        // Notificar aluno por e-mail (mock)
        emailService.enviarNotificacaoRecebimento(
                aluno.getEmail(),
                aluno.getNome(),
                professor.getNome(),
                quantidade,
                dto.getMotivo()
        );

        return EnvioMoedasResponseDTO.builder()
                .transacaoId(salva.getId())
                .quantidade(quantidade)
                .motivo(dto.getMotivo())
                .tipo(TipoTransacao.ENVIO)
                .dataHora(salva.getDataHora())
                .nomeAluno(aluno.getNome())
                .saldoRestanteProfessor(professor.getSaldo())
                .build();
    }

    // ── Crédito semestral ────────────────────────────────────────────────────

    /**
     * Adiciona {@value #CREDITO_SEMESTRAL} moedas ao saldo do professor
     * e registra uma {@link TipoTransacao#CREDITO_SEMESTRAL} no histórico.
     */
    @Transactional
    public void aplicarCreditoSemestral(Long professorId) {
        Professor professor = buscarProfessorOuLancar(professorId);

        professor.setSaldo(professor.getSaldo() + CREDITO_SEMESTRAL);
        professorRepository.save(professor);

        transacaoRepository.save(Transacao.builder()
                .quantidadeMoedas(CREDITO_SEMESTRAL)
                .dataHora(LocalDateTime.now())
                .tipo(TipoTransacao.CREDITO_SEMESTRAL)
                .motivo("Crédito semestral")
                .professor(professor)
                .build());
    }

    /**
     * Aplica o crédito semestral acumulável para todos os professores cadastrados.
     */
    @Transactional
    public List<Professor> aplicarCreditoSemestralEmLote() {
        List<Professor> professores = professorRepository.findAll();
        for (Professor prof : professores) {
            aplicarCreditoSemestral(prof.getId());
        }
        return professores;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Professor buscarProfessorOuLancar(Long professorId) {
        return professorRepository.findById(professorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Professor não encontrado com id: " + professorId));
    }

    private ExtratoProfessorItemDTO toExtratoProfessorItem(Transacao t) {
        boolean ehCredito = t.getTipo() == TipoTransacao.CREDITO_SEMESTRAL;
        double valor = ehCredito ? t.getQuantidadeMoedas() : -t.getQuantidadeMoedas();
        String nomeAluno = (t.getAluno() != null) ? t.getAluno().getNome() : null;

        return ExtratoProfessorItemDTO.builder()
                .data(t.getDataHora())
                .tipo(t.getTipo())
                .nomeAluno(nomeAluno)
                .motivo(t.getMotivo())
                .valor(valor)
                .build();
    }
}
