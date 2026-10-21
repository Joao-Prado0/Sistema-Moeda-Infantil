package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.dto.EmpresaAtualizacaoDTO;
import com.example.moeda_estudantil.dto.EmpresaCadastroDTO;
import com.example.moeda_estudantil.dto.EmpresaResponseDTO;
import com.example.moeda_estudantil.exception.RecursoNaoEncontradoException;
import com.example.moeda_estudantil.exception.RegraNegocioException;
import com.example.moeda_estudantil.model.EmpresaParceira;
import com.example.moeda_estudantil.model.enums.PerfilUsuario;
import com.example.moeda_estudantil.repository.EmpresaParceiraRepository;
import com.example.moeda_estudantil.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaParceiraService {

    private final EmpresaParceiraRepository empresaParceiraRepository;
    private final UsuarioRepository usuarioRepository;

    public EmpresaParceiraService(EmpresaParceiraRepository empresaParceiraRepository,
                                 UsuarioRepository usuarioRepository) {
        this.empresaParceiraRepository = empresaParceiraRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponseDTO> listarTodas() {
        return empresaParceiraRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpresaResponseDTO buscarPorId(Long id) {
        EmpresaParceira empresa = empresaParceiraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa parceira não encontrada com id: " + id));
        return toResponseDTO(empresa);
    }

    @Transactional
    public EmpresaResponseDTO cadastrar(EmpresaCadastroDTO dto) {
        // Validação de unicidade de email e cnpj (US13)
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("E-mail já cadastrado no sistema: " + dto.getEmail());
        }
        if (empresaParceiraRepository.existsByCnpj(dto.getCnpj())) {
            throw new RegraNegocioException("CNPJ já cadastrado no sistema: " + dto.getCnpj());
        }

        EmpresaParceira empresa = new EmpresaParceira(
                null,
                dto.getEmail(),
                dto.getSenha(),
                PerfilUsuario.EMPRESA,
                LocalDateTime.now(),
                dto.getNomeFantasia(),
                dto.getCnpj()
        );

        EmpresaParceira salva = empresaParceiraRepository.save(empresa);
        return toResponseDTO(salva);
    }

    @Transactional
    public EmpresaResponseDTO atualizar(Long id, EmpresaAtualizacaoDTO dto) {
        EmpresaParceira empresa = empresaParceiraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa parceira não encontrada com id: " + id));

        // Validação de unicidade de email se for alterado
        if (!empresa.getEmail().equalsIgnoreCase(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("E-mail já está em uso por outro usuário: " + dto.getEmail());
        }

        // Validação de unicidade de cnpj se for alterado
        if (!empresa.getCnpj().equals(dto.getCnpj()) && empresaParceiraRepository.existsByCnpj(dto.getCnpj())) {
            throw new RegraNegocioException("CNPJ já cadastrado para outra empresa: " + dto.getCnpj());
        }

        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setEmail(dto.getEmail());
        empresa.setCnpj(dto.getCnpj());

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            empresa.setSenha(dto.getSenha());
        }

        EmpresaParceira atualizada = empresaParceiraRepository.save(empresa);
        return toResponseDTO(atualizada);
    }

    @Transactional
    public void deletar(Long id) {
        EmpresaParceira empresa = empresaParceiraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa parceira não encontrada com id: " + id));
        empresaParceiraRepository.delete(empresa);
    }

    private EmpresaResponseDTO toResponseDTO(EmpresaParceira empresa) {
        return EmpresaResponseDTO.builder()
                .id(empresa.getId())
                .email(empresa.getEmail())
                .perfil(empresa.getPerfil())
                .dataCriacao(empresa.getDataCriacao())
                .nomeFantasia(empresa.getNomeFantasia())
                .cnpj(empresa.getCnpj())
                .build();
    }
}

