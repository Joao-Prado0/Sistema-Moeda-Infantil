package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.controller.AdminController;
import com.example.moeda_estudantil.dto.CreditoSemestralRequestDTO;
import com.example.moeda_estudantil.exception.AcessoNegadoException;
import com.example.moeda_estudantil.exception.ConflitoException;
import com.example.moeda_estudantil.model.Professor;
import com.example.moeda_estudantil.model.Transacao;
import com.example.moeda_estudantil.model.enums.TipoTransacao;
import com.example.moeda_estudantil.repository.AlunoRepository;
import com.example.moeda_estudantil.repository.DepartamentoRepository;
import com.example.moeda_estudantil.repository.InstituicaoRepository;
import com.example.moeda_estudantil.repository.ProfessorRepository;
import com.example.moeda_estudantil.repository.TransacaoRepository;
import com.example.moeda_estudantil.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCreditoSemestralTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @Mock
    private DepartamentoRepository departamentoRepository;

    private ProfessorService professorService;
    private AdminController adminController;

    private static final String ADMIN_KEY = "segredo-teste-123";

    @BeforeEach
    void setUp() {
        professorService = new ProfessorService(
                professorRepository,
                alunoRepository,
                transacaoRepository,
                emailService,
                usuarioRepository,
                instituicaoRepository,
                departamentoRepository
        );

        adminController = new AdminController(professorService, ADMIN_KEY);
    }

    @Test
    @DisplayName("AdminController: Deve lançar AcessoNegadoException (403) quando X-Admin-Key estiver incorreta ou ausente")
    void deveNegarAcessoSemChaveCorreta() {
        CreditoSemestralRequestDTO dto = new CreditoSemestralRequestDTO("2026/2");

        assertThatThrownBy(() -> adminController.aplicarCreditosSemestrais("chave-invalida", dto))
                .isInstanceOf(AcessoNegadoException.class)
                .hasMessageContaining("Chave administrativa inválida");

        assertThatThrownBy(() -> adminController.aplicarCreditosSemestrais(null, dto))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    @DisplayName("AdminController: Deve aplicar crédito semestral com sucesso quando chave estiver correta")
    void deveAplicarCreditoComChaveCorreta() {
        Professor p1 = new Professor(1L, "carlos@teste.com", "123", null, null, "Prof. Carlos", "11111111111", 500.0, null, null);
        when(transacaoRepository.existsByTipoAndMotivo(eq(TipoTransacao.CREDITO_SEMESTRAL), anyString()))
                .thenReturn(false);
        when(professorRepository.findAll()).thenReturn(List.of(p1));

        ResponseEntity<Map<String, Object>> resp = adminController.aplicarCreditosSemestrais(
                ADMIN_KEY,
                new CreditoSemestralRequestDTO("2026/2")
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().get("professoresCreditados")).isEqualTo(1);
        assertThat(resp.getBody().get("semestre")).isEqualTo("2026/2");
    }

    @Test
    @DisplayName("Idempotência: Deve lançar ConflitoException (409) se o mesmo semestre já foi creditado")
    void deveLancarConflitoSeJaFoiCreditado() {
        when(transacaoRepository.existsByTipoAndMotivo(TipoTransacao.CREDITO_SEMESTRAL, "Crédito semestral 2026/2"))
                .thenReturn(true);

        assertThatThrownBy(() -> professorService.aplicarCreditoSemestralIdempotente("2026/2"))
                .isInstanceOf(ConflitoException.class)
                .hasMessageContaining("já foi aplicado anteriormente");

        verify(professorRepository, never()).save(any());
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve creditar 1.000 moedas acumuláveis e criar Transacao CREDITO_SEMESTRAL para cada professor")
    void deveCreditarProfessoresECriarTransacoes() {
        Professor p1 = new Professor(1L, "ana@teste.com", "123", null, null, "Prof. Ana", "22222222222", 200.0, null, null);
        Professor p2 = new Professor(2L, "roberto@teste.com", "123", null, null, "Prof. Roberto", "33333333333", 1000.0, null, null);

        when(transacaoRepository.existsByTipoAndMotivo(TipoTransacao.CREDITO_SEMESTRAL, "Crédito semestral 2026/1"))
                .thenReturn(false);
        when(professorRepository.findAll()).thenReturn(List.of(p1, p2));

        int creditados = professorService.aplicarCreditoSemestralIdempotente("2026/1");

        assertThat(creditados).isEqualTo(2);
        assertThat(p1.getSaldo()).isEqualTo(1200.0);
        assertThat(p2.getSaldo()).isEqualTo(2000.0);

        verify(professorRepository, times(1)).save(p1);
        verify(professorRepository, times(1)).save(p2);

        ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
        verify(transacaoRepository, times(2)).save(captor.capture());

        List<Transacao> salvas = captor.getAllValues();
        assertThat(salvas).allMatch(t -> t.getTipo() == TipoTransacao.CREDITO_SEMESTRAL);
        assertThat(salvas).allMatch(t -> t.getQuantidadeMoedas().equals(1000.0));
        assertThat(salvas).allMatch(t -> t.getMotivo().equals("Crédito semestral 2026/1"));
    }

    @Test
    @DisplayName("Quando não houver professores cadastrados, deve retornar 0 sem lançar erro")
    void semProfessoresRetornaZero() {
        when(transacaoRepository.existsByTipoAndMotivo(any(), anyString())).thenReturn(false);
        when(professorRepository.findAll()).thenReturn(Collections.emptyList());

        int count = professorService.aplicarCreditoSemestralIdempotente("2026/2");
        assertThat(count).isZero();
    }
}
