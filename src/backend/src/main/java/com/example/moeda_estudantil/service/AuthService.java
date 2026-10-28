package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.dto.LoginRequestDTO;
import com.example.moeda_estudantil.dto.LoginResponseDTO;
import com.example.moeda_estudantil.exception.RegraNegocioException;
import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.EmpresaParceira;
import com.example.moeda_estudantil.model.Professor;
import com.example.moeda_estudantil.model.Usuario;
import com.example.moeda_estudantil.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RegraNegocioException("Credenciais inválidas. Verifique o e-mail e a senha informados."));

        if (!usuario.getSenha().equals(dto.getSenha())) {
            throw new RegraNegocioException("Credenciais inválidas. Verifique o e-mail e a senha informados.");
        }

        String nome = "Usuário";
        Double saldo = null;

        if (usuario instanceof Aluno aluno) {
            nome = aluno.getNome();
            saldo = aluno.getSaldo();
        } else if (usuario instanceof Professor professor) {
            nome = professor.getNome();
            saldo = professor.getSaldo();
        } else if (usuario instanceof EmpresaParceira empresa) {
            nome = empresa.getNomeFantasia();
        }

        String token = "Bearer " + usuario.getId() + ":" + usuario.getPerfil().name();

        return LoginResponseDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nome(nome)
                .perfil(usuario.getPerfil())
                .saldo(saldo)
                .token(token)
                .build();
    }
}

