package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.dto.CupomResponseDTO;
import com.example.moeda_estudantil.exception.RecursoNaoEncontradoException;
import com.example.moeda_estudantil.exception.RegraNegocioException;
import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.Cupom;
import com.example.moeda_estudantil.model.EmpresaParceira;
import com.example.moeda_estudantil.model.Transacao;
import com.example.moeda_estudantil.model.Vantagem;
import com.example.moeda_estudantil.model.enums.PerfilUsuario;
import com.example.moeda_estudantil.model.enums.StatusCupom;
import com.example.moeda_estudantil.model.enums.TipoTransacao;
import com.example.moeda_estudantil.repository.AlunoRepository;
import com.example.moeda_estudantil.repository.CupomRepository;
import com.example.moeda_estudantil.repository.TransacaoRepository;
import com.example.moeda_estudantil.repository.VantagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VantagemService - Resgate")
class VantagemServiceTest {

    @Mock
    private VantagemRepository vantagemRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CupomRepository cupomRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VantagemService vantagemService;

    private Aluno aluno;
    private Vantagem vantagem;
    private EmpresaParceira empresa;

    @BeforeEach
    void setUp() {
        empresa = new EmpresaParceira(
                1L,
                "empresa@parceiro.com",
                "senha",
                PerfilUsuario.EMPRESA,
                LocalDateTime.now(),
                "Cantina Central",
                "00.000.001/0001-01"
        );

        aluno = new Aluno(
                10L,
                "aluno@test.com",
                "senha",
                PerfilUsuario.ALUNO,
                LocalDateTime.now(),
                "João Teste",
                "111.111.111-11",
                "MG-111111",
                500.0,
                null,
                null,
                null
        );

        vantagem = Vantagem.builder()
                .id(5L)
                .titulo("Desconto de 20% no restaurante")
                .descricao("Descrição da vantagem")
                .custoMoedas(200.0)
                .empresa(empresa)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cenário 1: Resgate com sucesso
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve resgatar vantagem com sucesso quando saldo for suficiente")
    void deveResgatar_ComSucesso() {
        // Arrange
        UUID codigoGerado = UUID.randomUUID();

        Cupom cupomSalvo = Cupom.builder()
                .id(100L)
                .codigo(codigoGerado)
                .status(StatusCupom.GERADO)
                .aluno(aluno)
                .vantagem(vantagem)
                .dataResgate(LocalDateTime.now())
                .build();

        Transacao transacaoSalva = Transacao.builder()
                .id(50L)
                .quantidadeMoedas(200.0)
                .tipo(TipoTransacao.RESGATE_VANTAGEM)
                .motivo("Resgate: Desconto de 20% no restaurante")
                .aluno(aluno)
                .dataHora(LocalDateTime.now())
                .build();

        when(vantagemRepository.findById(5L)).thenReturn(Optional.of(vantagem));
        when(alunoRepository.findById(10L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacaoSalva);
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomSalvo);

        // Act
        CupomResponseDTO resultado = vantagemService.resgatar(5L, 10L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigo()).isEqualTo(codigoGerado.toString());
        assertThat(resultado.getNomeVantagem()).isEqualTo("Desconto de 20% no restaurante");
        assertThat(resultado.getNomeEmpresa()).isEqualTo("Cantina Central");
        assertThat(resultado.getCusto()).isEqualTo(200.0);
        assertThat(resultado.getSaldoRestante()).isEqualTo(300.0); // 500 - 200

        // Verifica que o saldo foi debitado no aluno
        ArgumentCaptor<Aluno> alunoCaptor = ArgumentCaptor.forClass(Aluno.class);
        verify(alunoRepository).save(alunoCaptor.capture());
        assertThat(alunoCaptor.getValue().getSaldo()).isEqualTo(300.0);

        // Verifica que a transação foi criada com os dados corretos
        ArgumentCaptor<Transacao> transacaoCaptor = ArgumentCaptor.forClass(Transacao.class);
        verify(transacaoRepository).save(transacaoCaptor.capture());
        assertThat(transacaoCaptor.getValue().getTipo()).isEqualTo(TipoTransacao.RESGATE_VANTAGEM);
        assertThat(transacaoCaptor.getValue().getMotivo()).isEqualTo("Resgate: Desconto de 20% no restaurante");
        assertThat(transacaoCaptor.getValue().getQuantidadeMoedas()).isEqualTo(200.0);

        // Verifica que o e-mail foi enviado
        verify(emailService).enviarCupom(
                eq("aluno@test.com"),
                eq("empresa@parceiro.com"),
                eq("João Teste"),
                eq("Desconto de 20% no restaurante"),
                eq(codigoGerado.toString())
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cenário 2: Saldo insuficiente
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando saldo for insuficiente")
    void deveLancarExcecao_QuandoSaldoInsuficiente() {
        // Arrange: custo da vantagem maior que saldo do aluno
        aluno.setSaldo(100.0); // saldo menor que 200

        when(vantagemRepository.findById(5L)).thenReturn(Optional.of(vantagem));
        when(alunoRepository.findById(10L)).thenReturn(Optional.of(aluno));

        // Act + Assert
        assertThatThrownBy(() -> vantagemService.resgatar(5L, 10L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Saldo insuficiente");

        // Garante que nenhuma operação de escrita foi realizada
        verify(alunoRepository, never()).save(any());
        verify(transacaoRepository, never()).save(any());
        verify(cupomRepository, never()).save(any());
        verify(emailService, never()).enviarCupom(any(), any(), any(), any(), any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cenário 3: Vantagem inexistente
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando vantagem não existir")
    void deveLancarExcecao_QuandoVantagemInexistente() {
        // Arrange
        when(vantagemRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> vantagemService.resgatar(999L, 10L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Vantagem não encontrada");

        // Garante que nada mais foi chamado
        verify(alunoRepository, never()).findById(any());
        verify(transacaoRepository, never()).save(any());
        verify(cupomRepository, never()).save(any());
        verify(emailService, never()).enviarCupom(any(), any(), any(), any(), any());
    }
}

