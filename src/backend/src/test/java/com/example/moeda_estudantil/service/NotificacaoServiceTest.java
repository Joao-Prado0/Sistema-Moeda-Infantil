package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.Cupom;
import com.example.moeda_estudantil.model.EmpresaParceira;
import com.example.moeda_estudantil.model.Professor;
import com.example.moeda_estudantil.model.Vantagem;
import com.example.moeda_estudantil.model.enums.PerfilUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacaoService")
class NotificacaoServiceTest {

    @Mock
    private EmailService emailService;

    private NotificacaoService notificacaoService;

    private Aluno aluno;
    private EmpresaParceira empresa;
    private UUID codigo;

    @BeforeEach
    void setUp() {
        notificacaoService = new NotificacaoService(emailService);
        aluno = new Aluno(10L, "aluno@test.com", "senha", PerfilUsuario.ALUNO, LocalDateTime.now(),
                "João Teste", "123.456.789-01", "MG-1", 500.0, null, null, null);
        empresa = new EmpresaParceira(1L, "empresa@parceiro.com", "senha", PerfilUsuario.EMPRESA,
                LocalDateTime.now(), "Cantina Central", "00.000.001/0001-01");
        codigo = UUID.randomUUID();
    }

    private Cupom cupomDe(EmpresaParceira empresaDaVantagem) {
        Vantagem vantagem = Vantagem.builder()
                .id(5L)
                .titulo("Desconto de 20%")
                .custoMoedas(200.0)
                .empresa(empresaDaVantagem)
                .build();
        return Cupom.builder()
                .id(100L)
                .codigo(codigo)
                .aluno(aluno)
                .vantagem(vantagem)
                .build();
    }

    @Test
    @DisplayName("Resgate: envia exatamente um e-mail ao aluno e um à empresa, com o mesmo código")
    void resgateEnviaUmEmailParaCadaDestinatario() {
        notificacaoService.notificarResgate(cupomDe(empresa));

        verify(emailService, times(1)).enviarCupomAluno(
                "aluno@test.com", codigo.toString(), "Desconto de 20%", "Cantina Central");
        verify(emailService, times(1)).enviarAvisoResgateEmpresa(
                "empresa@parceiro.com", codigo.toString(), "Desconto de 20%", "João Teste", "***.456.789-**");
        verifyNoMoreInteractions(emailService);
    }

    @Test
    @DisplayName("Resgate sem empresa vinculada: só o aluno recebe e-mail")
    void resgateSemEmpresa() {
        notificacaoService.notificarResgate(cupomDe(null));

        verify(emailService, times(1)).enviarCupomAluno(anyString(), anyString(), anyString(), anyString());
        verify(emailService, never()).enviarAvisoResgateEmpresa(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Envio de moedas: avisa o aluno com valor, professor e motivo")
    void recebimentoDeMoedas() {
        Professor professor = new Professor(2L, "prof@test.com", "senha", PerfilUsuario.PROFESSOR,
                LocalDateTime.now(), "Prof. Ana", "11111111111", 1000.0, null, null);

        notificacaoService.notificarRecebimentoMoedas(aluno, professor, 50.0, "Participação em aula");

        verify(emailService, times(1)).enviarAvisoRecebimentoMoedas(
                "aluno@test.com", 50.0, "Prof. Ana", "Participação em aula");
    }

    @Test
    @DisplayName("Falha no envio de e-mail não é propagada para a regra de negócio")
    void falhaNoEnvioNaoPropaga() {
        doThrow(new RuntimeException("SMTP fora do ar"))
                .when(emailService).enviarAvisoRecebimentoMoedas(anyString(), anyDouble(), anyString(), anyString());
        Professor professor = new Professor();
        professor.setNome("Prof. Ana");

        assertThatCode(() -> notificacaoService.notificarRecebimentoMoedas(aluno, professor, 10.0, "Motivo"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("CPF é mascarado no aviso à empresa")
    void mascaraCpf() {
        assertThat(NotificacaoService.mascararCpf("123.456.789-01")).isEqualTo("***.456.789-**");
        assertThat(NotificacaoService.mascararCpf(null)).isEqualTo("***");
        assertThat(NotificacaoService.mascararCpf("123")).isEqualTo("***");
    }
}
