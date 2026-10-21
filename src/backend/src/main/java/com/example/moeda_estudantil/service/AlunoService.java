package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.dto.AlunoAtualizacaoDTO;
import com.example.moeda_estudantil.dto.AlunoCadastroDTO;
import com.example.moeda_estudantil.dto.AlunoResponseDTO;
import com.example.moeda_estudantil.dto.EnderecoDTO;
import com.example.moeda_estudantil.exception.RecursoNaoEncontradoException;
import com.example.moeda_estudantil.exception.RegraNegocioException;
import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.Curso;
import com.example.moeda_estudantil.model.Endereco;
import com.example.moeda_estudantil.model.Instituicao;
import com.example.moeda_estudantil.model.enums.PerfilUsuario;
import com.example.moeda_estudantil.repository.AlunoRepository;
import com.example.moeda_estudantil.repository.CursoRepository;
import com.example.moeda_estudantil.repository.InstituicaoRepository;
import com.example.moeda_estudantil.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final CursoRepository cursoRepository;

    public AlunoService(AlunoRepository alunoRepository,
                        UsuarioRepository usuarioRepository,
                        InstituicaoRepository instituicaoRepository,
                        CursoRepository cursoRepository) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional(readOnly = true)
    public List<AlunoResponseDTO> listarTodos() {
        return alunoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado com id: " + id));
        return toResponseDTO(aluno);
    }

    @Transactional
    public AlunoResponseDTO cadastrar(AlunoCadastroDTO dto) {
        // Validação de unicidade de email e cpf (US02)
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("E-mail já cadastrado no sistema: " + dto.getEmail());
        }
        if (alunoRepository.existsByCpf(dto.getCpf())) {
            throw new RegraNegocioException("CPF já cadastrado no sistema: " + dto.getCpf());
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instituição não encontrada com id: " + dto.getInstituicaoId()));

        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado com id: " + dto.getCursoId()));

        Endereco endereco = toEnderecoEntity(dto.getEndereco());

        Aluno aluno = new Aluno(
                null,
                dto.getEmail(),
                dto.getSenha(),
                PerfilUsuario.ALUNO,
                LocalDateTime.now(),
                dto.getNome(),
                dto.getCpf(),
                dto.getRg(),
                0.0, // Saldo inicial zerado
                endereco,
                instituicao,
                curso
        );

        Aluno salvo = alunoRepository.save(aluno);
        return toResponseDTO(salvo);
    }

    @Transactional
    public AlunoResponseDTO atualizar(Long id, AlunoAtualizacaoDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado com id: " + id));

        // Validação de unicidade de email se for alterado
        if (!aluno.getEmail().equalsIgnoreCase(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("E-mail já está em uso por outro usuário: " + dto.getEmail());
        }

        // Validação de unicidade de cpf se for alterado
        if (!aluno.getCpf().equals(dto.getCpf()) && alunoRepository.existsByCpf(dto.getCpf())) {
            throw new RegraNegocioException("CPF já cadastrado para outro aluno: " + dto.getCpf());
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instituição não encontrada com id: " + dto.getInstituicaoId()));

        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado com id: " + dto.getCursoId()));

        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setCpf(dto.getCpf());
        aluno.setRg(dto.getRg());
        aluno.setEndereco(toEnderecoEntity(dto.getEndereco()));
        aluno.setInstituicao(instituicao);
        aluno.setCurso(curso);

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            aluno.setSenha(dto.getSenha());
        }

        Aluno atualizado = alunoRepository.save(aluno);
        return toResponseDTO(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado com id: " + id));
        alunoRepository.delete(aluno);
    }

    private Endereco toEnderecoEntity(EnderecoDTO dto) {
        if (dto == null) return null;
        return Endereco.builder()
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .cep(dto.getCep())
                .build();
    }

    private EnderecoDTO toEnderecoDTO(Endereco entity) {
        if (entity == null) return null;
        return EnderecoDTO.builder()
                .logradouro(entity.getLogradouro())
                .numero(entity.getNumero())
                .complemento(entity.getComplemento())
                .bairro(entity.getBairro())
                .cidade(entity.getCidade())
                .estado(entity.getEstado())
                .cep(entity.getCep())
                .build();
    }

    private AlunoResponseDTO toResponseDTO(Aluno aluno) {
        return AlunoResponseDTO.builder()
                .id(aluno.getId())
                .email(aluno.getEmail())
                .perfil(aluno.getPerfil())
                .dataCriacao(aluno.getDataCriacao())
                .nome(aluno.getNome())
                .cpf(aluno.getCpf())
                .rg(aluno.getRg())
                .saldo(aluno.getSaldo())
                .endereco(toEnderecoDTO(aluno.getEndereco()))
                .instituicaoId(aluno.getInstituicao() != null ? aluno.getInstituicao().getId() : null)
                .instituicaoNome(aluno.getInstituicao() != null ? aluno.getInstituicao().getNome() : null)
                .cursoId(aluno.getCurso() != null ? aluno.getCurso().getId() : null)
                .cursoNome(aluno.getCurso() != null ? aluno.getCurso().getNome() : null)
                .build();
    }
}

