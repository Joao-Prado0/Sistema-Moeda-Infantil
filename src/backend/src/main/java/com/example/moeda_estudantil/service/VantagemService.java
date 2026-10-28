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

        // Enviar e-mail (mock) após confirmar a transação
        String emailEmpresa = vantagem.getEmpresa() != null ? vantagem.getEmpresa().getEmail() : "";
        emailService.enviarCupom(
                aluno.getEmail(),
                emailEmpresa,
                aluno.getNome(),
                vantagem.getTitulo(),
                cupomSalvo.getCodigo().toString()
        );

        String nomeEmpresa = vantagem.getEmpresa() != null
                ? vantagem.getEmpresa().getNomeFantasia()
                : null;

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

