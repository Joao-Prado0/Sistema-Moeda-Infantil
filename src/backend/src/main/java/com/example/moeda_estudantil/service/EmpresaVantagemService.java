package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.dto.ConferirCupomRequestDTO;
import com.example.moeda_estudantil.dto.CupomEmpresaDTO;
import com.example.moeda_estudantil.dto.VantagemDetalheDTO;
import com.example.moeda_estudantil.dto.VantagemEmpresaRequestDTO;
import com.example.moeda_estudantil.exception.RecursoNaoEncontradoException;
import com.example.moeda_estudantil.exception.RegraNegocioException;
import com.example.moeda_estudantil.model.Cupom;
import com.example.moeda_estudantil.model.EmpresaParceira;
import com.example.moeda_estudantil.model.Vantagem;
import com.example.moeda_estudantil.model.enums.StatusCupom;
import com.example.moeda_estudantil.repository.CupomRepository;
import com.example.moeda_estudantil.repository.EmpresaParceiraRepository;
import com.example.moeda_estudantil.repository.VantagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmpresaVantagemService {

    private final EmpresaParceiraRepository empresaParceiraRepository;
    private final VantagemRepository vantagemRepository;
    private final CupomRepository cupomRepository;

    public EmpresaVantagemService(EmpresaParceiraRepository empresaParceiraRepository,
                                  VantagemRepository vantagemRepository,
                                  CupomRepository cupomRepository) {
        this.empresaParceiraRepository = empresaParceiraRepository;
        this.vantagemRepository = vantagemRepository;
        this.cupomRepository = cupomRepository;
    }

    // ── Vantagens ─────────────────────────────────────────────────────────────

    /**
     * Lista as vantagens da empresa logada.
     */
    @Transactional(readOnly = true)
    public List<VantagemDetalheDTO> listarVantagensDaEmpresa(Long empresaId) {
        buscarEmpresaOuLancar(empresaId);
        return vantagemRepository.findByEmpresaId(empresaId)
                .stream()
                .map(this::toVantagemDetalheDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cria uma nova vantagem vinculada à empresa logada.
     */
    @Transactional
    public VantagemDetalheDTO criarVantagem(Long empresaId, VantagemEmpresaRequestDTO dto) {
        EmpresaParceira empresa = buscarEmpresaOuLancar(empresaId);

        Vantagem vantagem = Vantagem.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .fotoUrl(dto.getFotoUrl())
                .custoMoedas(dto.getCustoMoedas())
                .empresa(empresa)
                .build();

        return toVantagemDetalheDTO(vantagemRepository.save(vantagem));
    }

    /**
     * Edita uma vantagem, garantindo que pertence à empresa logada.
     * Retorna 404 se a vantagem não existir ou não pertencer à empresa.
     */
    @Transactional
    public VantagemDetalheDTO editarVantagem(Long empresaId, Long vantagemId,
                                             VantagemEmpresaRequestDTO dto) {
        buscarEmpresaOuLancar(empresaId);
        Vantagem vantagem = buscarVantagemDaEmpresaOuLancar(empresaId, vantagemId);

        vantagem.setTitulo(dto.getTitulo());
        vantagem.setDescricao(dto.getDescricao());
        vantagem.setFotoUrl(dto.getFotoUrl());
        vantagem.setCustoMoedas(dto.getCustoMoedas());

        return toVantagemDetalheDTO(vantagemRepository.save(vantagem));
    }

    /**
     * Exclui uma vantagem da empresa logada.
     * Se houver cupons vinculados, lança {@link RegraNegocioException}.
     */
    @Transactional
    public void excluirVantagem(Long empresaId, Long vantagemId) {
        buscarEmpresaOuLancar(empresaId);
        Vantagem vantagem = buscarVantagemDaEmpresaOuLancar(empresaId, vantagemId);

        if (cupomRepository.existsByVantagemId(vantagem.getId())) {
            throw new RegraNegocioException(
                    "Não é possível excluir a vantagem \"" + vantagem.getTitulo() +
                    "\" porque já existem cupons emitidos vinculados a ela. " +
                    "Desative a vantagem em vez de excluí-la.");
        }

        vantagemRepository.delete(vantagem);
    }

    // ── Cupons ────────────────────────────────────────────────────────────────

    /**
     * Lista os cupons de todas as vantagens da empresa logada.
     */
    @Transactional(readOnly = true)
    public List<CupomEmpresaDTO> listarCuponsDaEmpresa(Long empresaId) {
        buscarEmpresaOuLancar(empresaId);
        return cupomRepository.findByVantagemEmpresaId(empresaId)
                .stream()
                .map(this::toCupomEmpresaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Confere (valida e marca como UTILIZADO) um cupom pelo código.
     * Regras:
     * - cupom deve existir
     * - deve pertencer a uma vantagem da empresa logada
     * - status deve ser GERADO; se UTILIZADO, lança erro claro
     */
    @Transactional
    public CupomEmpresaDTO conferirCupom(Long empresaId, ConferirCupomRequestDTO dto) {
        buscarEmpresaOuLancar(empresaId);

        UUID uuid;
        try {
            uuid = UUID.fromString(dto.getCodigo());
        } catch (IllegalArgumentException e) {
            throw new RegraNegocioException("Código de cupom inválido: \"" + dto.getCodigo() + "\".");
        }

        Cupom cupom = cupomRepository.findByCodigo(uuid)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cupom não encontrado com o código: " + dto.getCodigo()));

        // Verifica se pertence a uma vantagem desta empresa
        if (cupom.getVantagem() == null ||
                cupom.getVantagem().getEmpresa() == null ||
                !cupom.getVantagem().getEmpresa().getId().equals(empresaId)) {
            throw new RecursoNaoEncontradoException(
                    "Cupom não encontrado com o código: " + dto.getCodigo());
        }

        if (cupom.getStatus() == StatusCupom.UTILIZADO) {
            throw new RegraNegocioException(
                    "Este cupom já foi utilizado em " + cupom.getDataResgate() + ".");
        }

        if (cupom.getStatus() == StatusCupom.CANCELADO) {
            throw new RegraNegocioException("Este cupom está cancelado e não pode ser conferido.");
        }

        cupom.setStatus(StatusCupom.UTILIZADO);
        return toCupomEmpresaDTO(cupomRepository.save(cupom));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private EmpresaParceira buscarEmpresaOuLancar(Long empresaId) {
        return empresaParceiraRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Empresa parceira não encontrada com id: " + empresaId));
    }

    /**
     * Busca a vantagem e verifica que pertence à empresa.
     * Retorna 404 em ambos os casos para não vazar informação sobre
     * vantagens de outras empresas.
     */
    private Vantagem buscarVantagemDaEmpresaOuLancar(Long empresaId, Long vantagemId) {
        Vantagem vantagem = vantagemRepository.findById(vantagemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Vantagem não encontrada com id: " + vantagemId));

        if (vantagem.getEmpresa() == null ||
                !vantagem.getEmpresa().getId().equals(empresaId)) {
            throw new RecursoNaoEncontradoException(
                    "Vantagem não encontrada com id: " + vantagemId);
        }

        return vantagem;
    }

    private VantagemDetalheDTO toVantagemDetalheDTO(Vantagem v) {
        return VantagemDetalheDTO.builder()
                .id(v.getId())
                .titulo(v.getTitulo())
                .descricao(v.getDescricao())
                .fotoUrl(v.getFotoUrl())
                .custoMoedas(v.getCustoMoedas())
                .build();
    }

    private CupomEmpresaDTO toCupomEmpresaDTO(Cupom c) {
        String nomeAluno = c.getAluno() != null ? c.getAluno().getNome() : null;
        String tituloVantagem = c.getVantagem() != null ? c.getVantagem().getTitulo() : null;

        return CupomEmpresaDTO.builder()
                .id(c.getId())
                .codigo(c.getCodigo().toString())
                .nomeAluno(nomeAluno)
                .tituloVantagem(tituloVantagem)
                .dataResgate(c.getDataResgate())
                .status(c.getStatus())
                .build();
    }
}

