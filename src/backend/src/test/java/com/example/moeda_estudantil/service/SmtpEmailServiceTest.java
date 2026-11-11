package com.example.moeda_estudantil.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private SmtpEmailService smtpEmailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(smtpEmailService, "remetente", "danielbonycg@gmail.com");
        // Configura o mock para devolver um MimeMessage válido
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar aviso de recebimento de moedas com sucesso via JavaMailSender")
    void deveEnviarAvisoRecebimentoMoedas() {
        smtpEmailService.enviarAvisoRecebimentoMoedas(
                "aluno@teste.com",
                250.0,
                "Prof. Carvalho",
                "Excelente participação no projeto"
        );

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve enviar cupom de vantagem para o aluno com sucesso via JavaMailSender")
    void deveEnviarCupomAluno() {
        smtpEmailService.enviarCupomAluno(
                "aluno@teste.com",
                "b1c2d3e4-5678-90ab-cdef-1234567890ab",
                "Vale Livro 50 reais",
                "Livraria Central"
        );

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve enviar aviso de resgate para a empresa parceira com CPF mascarado")
    void deveEnviarAvisoResgateEmpresa() {
        smtpEmailService.enviarAvisoResgateEmpresa(
                "contato@livraria.com",
                "b1c2d3e4-5678-90ab-cdef-1234567890ab",
                "Vale Livro 50 reais",
                "João Aluno",
                "***.456.789-**"
        );

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Falha no envio de e-mail nunca propaga exceção e não quebra a operação")
    void falhaNoEnvioNaoLancaExcecao() {
        doThrow(new RuntimeException("Falha de conexão SMTP")).when(mailSender).send(any(MimeMessage.class));

        smtpEmailService.enviarAvisoRecebimentoMoedas(
                "aluno@teste.com",
                100.0,
                "Prof. Carvalho",
                "Participação"
        );

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
