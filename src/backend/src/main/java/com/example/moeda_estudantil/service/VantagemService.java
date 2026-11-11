package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.dto.CupomResponseDTO;
import com.example.moeda_estudantil.dto.VantagemResponseDTO;
import com.example.moeda_estudantil.exception.RecursoNaoEncontradoException;
import com.example.moeda_estudantil.exception.RegraNegocioException;
import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.Cupom;
import com.example.moeda_estudantil.model.Transacao;
import com.example.moeda_estudantil.model.Vantagem;
import com.example.moeda_estudantil.model.enums.StatusCupom;
import com.example.moeda_estudantil.model.enums.TipoTransacao;
import com.example.moeda_estudantil.repository.AlunoRepository;
import com.example.moeda_estudantil.repository.CupomRepository;
import com.example.moeda_estudantil.repository.TransacaoRepository;
import com.example.moeda_estudantil.repository.VantagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VantagemService {

    private final VantagemRepository vantagemRepository;
    private final AlunoRepository alunoRepository;
    private final TransacaoRepository transacaoRepository;
    private final CupomRepository cupomRepository;
    private final EmailService emailService;

    public VantagemService(VantagemRepository vantagemRepository,
                           AlunoRepository alunoRepository,
                           TransacaoRepository transacaoRepository,
                           CupomRepository cupomRepository,
                           EmailService emailService) {
        this.vantagemRepository = vantagemRepository;
        this.alunoRepository = alunoRepository;
        this.transacaoRepository = transacaoRepository;
        this.cupomRepository = cupomRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<VantagemResponseDTO> listarCatalogo() {
        return vantagemRepository.findAll()
                .stream()
                .map(this::toVantagemResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CupomResponseDTO resgatar(Long vantagemId, Long alunoId) {
        Vantagem vantagem = vantagemRepository.findById(vantagemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Vantagem não encontrada com id: " + vantagemId));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aluno não encontrado com id: " + alunoId));

        double custo = vantagem.getCustoMoedas();

        if (aluno.getSaldo() < custo) {
            throw new RegraNegocioException(
                    "Saldo insuficiente para resgatar a vantagem. Saldo atual: " +
                    aluno.getSaldo() + " moedas. Custo da vantagem: " + custo + " moedas.");
        }

        // Debitar saldo
        aluno.setSaldo(aluno.getSaldo() - custo);
        alunoRepository.save(aluno);

        // Criar transação de troca
        Transacao transacao = Transacao.builder()
                .quantidadeMoedas(custo)
                .dataHora(LocalDateTime.now())
                .tipo(TipoTransacao.RESGATE_VANTAGEM)
                .motivo("Resgate: " + vantagem.getTitulo())
                .aluno(aluno)
                .build();
        transacaoRepository.save(transacao);

        // Gerar cupom
        Cupom cupom = Cupom.builder()
                .status(StatusCupom.GERADO)
                .aluno(aluno)
                .vantagem(vantagem)
                .transacao(transacao)
                .build();
        // O @PrePersist vai gerar o codigo UUID e dataResgate automaticamente
        Cupom cupomSalvo = cupomRepository.save(cupom);

        final String nomeEmpresa = vantagem.getEmpresa() != null
                ? vantagem.getEmpresa().getNomeFantasia()
                : "";
        final String emailEmpresa = vantagem.getEmpresa() != null ? vantagem.getEmpresa().getEmail() : "";
        final String emailAluno = aluno.getEmail();
        final String nomeAluno = aluno.getNome();
        final String cpfAluno = aluno.getCpf();
        final String tituloVantagem = vantagem.getTitulo();
        final String codigoCupom = cupomSalvo.getCodigo().toString();

        // Enviar e-mails após o commit da transação de forma assíncrona (US08 e US17)
        executarAposCommit(() -> {
            try {
                emailService.enviarCupomAluno(emailAluno, codigoCupom, tituloVantagem, nomeEmpresa);
                if (emailEmpresa != null && !emailEmpresa.isBlank()) {
                    emailService.enviarAvisoResgateEmpresa(
                            emailEmpresa,
                            codigoCupom,
                            tituloVantagem,
                            nomeAluno,
                            mascararCpf(cpfAluno)
                    );
                }
                emailService.enviarCupom(
                        emailAluno,
                        emailEmpresa,
                        nomeAluno,
                        tituloVantagem,
                        codigoCupom
                );
            } catch (Exception e) {
                // Falha no envio nunca desfaz o resgate ou o cupom
            }
        });

        return CupomResponseDTO.builder()
                .id(cupomSalvo.getId())
                .codigo(cupomSalvo.getCodigo().toString())
                .nomeVantagem(vantagem.getTitulo())
                .nomeEmpresa(nomeEmpresa)
                .custo(custo)
                .saldoRestante(aluno.getSaldo())
                .status(cupomSalvo.getStatus())
                .dataResgate(cupomSalvo.getDataResgate())
                .build();
    }

    private void executarAposCommit(Runnable acao) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    acao.run();
                }
            });
        } else {
            acao.run();
        }
    }

    private String mascararCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return "***";
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() == 11) {
            return "***." + digits.substring(3, 6) + "." + digits.substring(6, 9) + "-**";
        }
        return "***";
    }

    private VantagemResponseDTO toVantagemResponseDTO(Vantagem v) {
        String nomeEmpresa = v.getEmpresa() != null ? v.getEmpresa().getNomeFantasia() : null;
        return VantagemResponseDTO.builder()
                .id(v.getId())
                .nome(v.getTitulo())
                .descricao(v.getDescricao())
                .nomeEmpresa(nomeEmpresa)
                .custoEmMoedas(v.getCustoMoedas())
                .urlFoto(v.getFotoUrl())
                .build();
    }
}

