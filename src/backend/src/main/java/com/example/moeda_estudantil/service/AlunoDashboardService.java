package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.dto.AlunoDashboardDTO;
import com.example.moeda_estudantil.dto.CupomListagemDTO;
import com.example.moeda_estudantil.dto.ExtratoItemDTO;
import com.example.moeda_estudantil.exception.RecursoNaoEncontradoException;
import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.Cupom;
import com.example.moeda_estudantil.model.Transacao;
import com.example.moeda_estudantil.model.enums.TipoTransacao;
import com.example.moeda_estudantil.repository.AlunoRepository;
import com.example.moeda_estudantil.repository.CupomRepository;
import com.example.moeda_estudantil.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlunoDashboardService {

    private final AlunoRepository alunoRepository;
    private final TransacaoRepository transacaoRepository;
    private final CupomRepository cupomRepository;

    public AlunoDashboardService(AlunoRepository alunoRepository,
                                 TransacaoRepository transacaoRepository,
                                 CupomRepository cupomRepository) {
        this.alunoRepository = alunoRepository;
        this.transacaoRepository = transacaoRepository;
        this.cupomRepository = cupomRepository;
    }

    @Transactional(readOnly = true)
    public AlunoDashboardDTO obterDashboard(Long alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado com id: " + alunoId));

        List<Transacao> transacoes = transacaoRepository.findByAlunoId(alunoId);

        double totalRecebido = transacoes.stream()
                .filter(t -> isRecebimento(t.getTipo()))
                .mapToDouble(Transacao::getQuantidadeMoedas)
                .sum();

        double totalTrocado = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RESGATE_VANTAGEM)
                .mapToDouble(Transacao::getQuantidadeMoedas)
                .sum();

        List<ExtratoItemDTO> extrato = transacoes.stream()
                .sorted(Comparator.comparing(Transacao::getDataHora).reversed())
                .map(this::toExtratoItem)
                .collect(Collectors.toList());

        return AlunoDashboardDTO.builder()
                .nome(aluno.getNome())
                .saldoAtual(aluno.getSaldo())
                .totalRecebido(totalRecebido)
                .totalTrocado(totalTrocado)
                .extrato(extrato)
                .build();
    }

    @Transactional(readOnly = true)
    public List<CupomListagemDTO> listarMeusCupons(Long alunoId) {
        alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado com id: " + alunoId));

        return cupomRepository.findByAlunoId(alunoId)
                .stream()
                .map(this::toCupomListagemDTO)
                .collect(Collectors.toList());
    }

    private boolean isRecebimento(TipoTransacao tipo) {
        return tipo == TipoTransacao.BONIFICACAO || tipo == TipoTransacao.CREDITO_SEMESTRAL;
    }

    private ExtratoItemDTO toExtratoItem(Transacao t) {
        double valor = isRecebimento(t.getTipo())
                ? t.getQuantidadeMoedas()
                : -t.getQuantidadeMoedas();

        return ExtratoItemDTO.builder()
                .data(t.getDataHora())
                .tipo(t.getTipo())
                .descricao(t.getMotivo())
                .valor(valor)
                .build();
    }

    private CupomListagemDTO toCupomListagemDTO(Cupom cupom) {
        String nomeVantagem = cupom.getVantagem() != null ? cupom.getVantagem().getTitulo() : null;
        String nomeEmpresa = (cupom.getVantagem() != null && cupom.getVantagem().getEmpresa() != null)
                ? cupom.getVantagem().getEmpresa().getNomeFantasia()
                : null;
        Double custo = cupom.getVantagem() != null ? cupom.getVantagem().getCustoMoedas() : null;

        return CupomListagemDTO.builder()
                .id(cupom.getId())
                .codigo(cupom.getCodigo().toString())
                .nomeVantagem(nomeVantagem)
                .nomeEmpresa(nomeEmpresa)
                .custo(custo)
                .status(cupom.getStatus())
                .dataResgate(cupom.getDataResgate())
                .build();
    }
}
