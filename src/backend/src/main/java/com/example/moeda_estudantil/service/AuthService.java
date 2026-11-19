package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.dto.LoginRequestDTO;
import com.example.moeda_estudantil.dto.LoginResponseDTO;
import com.example.moeda_estudantil.exception.RegraNegocioException;
import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.EmpresaParceira;
import com.example.moeda_estudantil.model.Professor;
import com.example.moeda_estudantil.model.Usuario;
import com.example.moeda_estudantil.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RegraNegocioException("Credenciais inválidas. Verifique o e-mail e a senha informados."));

        if (!senhaConfere(dto.getSenha(), usuario.getSenha())) {
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

    /** Aceita senhas salvas em BCrypt ($2a$/$2b$/$2y$) ou em texto puro. */
    private boolean senhaConfere(String senhaInformada, String senhaSalva) {
        if (senhaInformada == null || senhaSalva == null) {
            return false;
        }
        if (senhaSalva.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$")) {
            return passwordEncoder.matches(senhaInformada, senhaSalva);
        }
        return senhaSalva.equals(senhaInformada);
    }
}

