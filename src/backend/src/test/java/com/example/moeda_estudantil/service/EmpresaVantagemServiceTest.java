package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.dto.CupomEmpresaDTO;
import com.example.moeda_estudantil.dto.ConferirCupomRequestDTO;
import com.example.moeda_estudantil.dto.VantagemDetalheDTO;
import com.example.moeda_estudantil.dto.VantagemEmpresaRequestDTO;
import com.example.moeda_estudantil.exception.RecursoNaoEncontradoException;
import com.example.moeda_estudantil.exception.RegraNegocioException;
import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.Cupom;
import com.example.moeda_estudantil.model.EmpresaParceira;
import com.example.moeda_estudantil.model.Vantagem;
import com.example.moeda_estudantil.model.enums.PerfilUsuario;
import com.example.moeda_estudantil.model.enums.StatusCupom;
import com.example.moeda_estudantil.repository.CupomRepository;
import com.example.moeda_estudantil.repository.EmpresaParceiraRepository;
import com.example.moeda_estudantil.repository.VantagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmpresaVantagemService — testes de negócio")
class EmpresaVantagemServiceTest {

    @Mock
    private EmpresaParceiraRepository empresaParceiraRepository;

    @Mock
    private VantagemRepository vantagemRepository;

    @Mock
    private CupomRepository cupomRepository;

    @InjectMocks
    private EmpresaVantagemService service;

    private EmpresaParceira empresa;
    private EmpresaParceira outraEmpresa;
    private Vantagem vantagem;
    private Aluno aluno;

    @BeforeEach
    void setUp() {
        empresa = new EmpresaParceira(
                1L, "cantina@parceiro.com", "senha", PerfilUsuario.EMPRESA,
                LocalDateTime.now(), "Cantina Central", "00.000.001/0001-01");

        outraEmpresa = new EmpresaParceira(
                2L, "outra@parceiro.com", "senha", PerfilUsuario.EMPRESA,
                LocalDateTime.now(), "Outra Empresa", "00.000.002/0001-02");

        vantagem = Vantagem.builder()
                .id(10L)
                .titulo("Desconto 20%")
                .descricao("Desconto na cantina")
                .custoMoedas(200.0)
                .empresa(empresa)
                .build();

        aluno = new Aluno(
                5L, "aluno@test.com", "senha", PerfilUsuario.ALUNO,
                LocalDateTime.now(), "João Aluno", "111.111.111-11",
                "MG-111", 500.0, null, null, null);
    }

    // ── Teste 1: Criar vantagem com sucesso ──────────────────────────────────

    @Test
    @DisplayName("Deve criar vantagem vinculada à empresa logada")
    void deveCriarVantagem() {
        VantagemEmpresaRequestDTO dto = VantagemEmpresaRequestDTO.builder()
                .titulo("Lanche Combo")
                .descricao("Lanche + bebida")
                .custoMoedas(80.0)
                .build();

        Vantagem vantagemSalva = Vantagem.builder()
                .id(99L)
                .titulo("Lanche Combo")
                .descricao("Lanche + bebida")
                .custoMoedas(80.0)
                .empresa(empresa)
                .build();

        when(empresaParceiraRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(vantagemRepository.save(any(Vantagem.class))).thenReturn(vantagemSalva);

        VantagemDetalheDTO resultado = service.criarVantagem(1L, dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(99L);
        assertThat(resultado.getTitulo()).isEqualTo("Lanche Combo");
        assertThat(resultado.getCustoMoedas()).isEqualTo(80.0);

        verify(vantagemRepository).save(any(Vantagem.class));
    }

    // ── Teste 2: Editar vantagem de outra empresa deve retornar 404 ──────────

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao editar vantagem de outra empresa")
    void deveRejeitarEdicaoDeOutraEmpresa() {
        // vantagem pertence a `empresa` (id=1), mas a edição vem de `outraEmpresa` (id=2)
        VantagemEmpresaRequestDTO dto = VantagemEmpresaRequestDTO.builder()
                .titulo("Alteração indevida")
                .descricao("Descrição")
                .custoMoedas(50.0)
                .build();

        when(empresaParceiraRepository.findById(2L)).thenReturn(Optional.of(outraEmpresa));
        when(vantagemRepository.findById(10L)).thenReturn(Optional.of(vantagem));

        assertThatThrownBy(() -> service.editarVantagem(2L, 10L, dto))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Vantagem não encontrada");

        verify(vantagemRepository, never()).save(any());
    }

    // ── Teste 3: Excluir vantagem com cupons deve lançar RegraNegocioException

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao excluir vantagem com cupons vinculados")
    void deveRejeitarExclusaoComCupons() {
        when(empresaParceiraRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(vantagemRepository.findById(10L)).thenReturn(Optional.of(vantagem));
        when(cupomRepository.existsByVantagemId(10L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluirVantagem(1L, 10L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("cupons emitidos");

        verify(vantagemRepository, never()).delete(any());
    }

    // ── Teste 4: Conferir cupom válido com sucesso ───────────────────────────

    @Test
    @DisplayName("Deve conferir cupom GERADO e marcá-lo como UTILIZADO")
    void deveConferirCupomValido() {
        UUID codigo = UUID.randomUUID();
        Cupom cupom = Cupom.builder()
                .id(20L)
                .codigo(codigo)
                .status(StatusCupom.GERADO)
                .dataResgate(LocalDateTime.now())
                .aluno(aluno)
                .vantagem(vantagem)
                .build();

        Cupom cupomAtualizado = Cupom.builder()
                .id(20L)
                .codigo(codigo)
                .status(StatusCupom.UTILIZADO)
                .dataResgate(cupom.getDataResgate())
                .aluno(aluno)
                .vantagem(vantagem)
                .build();

        ConferirCupomRequestDTO dto = new ConferirCupomRequestDTO(codigo.toString());

        when(empresaParceiraRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(cupomRepository.findByCodigo(codigo)).thenReturn(Optional.of(cupom));
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomAtualizado);

        CupomEmpresaDTO resultado = service.conferirCupom(1L, dto);

        assertThat(resultado.getStatus()).isEqualTo(StatusCupom.UTILIZADO);
        assertThat(resultado.getCodigo()).isEqualTo(codigo.toString());
        assertThat(resultado.getNomeAluno()).isEqualTo("João Aluno");

        verify(cupomRepository).save(argThat(c -> c.getStatus() == StatusCupom.UTILIZADO));
    }

    // ── Teste 5: Conferir cupom já utilizado deve lançar RegraNegocioException

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao conferir cupom já utilizado")
    void deveRejeitarCupomJaUtilizado() {
        UUID codigo = UUID.randomUUID();
        Cupom cupom = Cupom.builder()
                .id(21L)
                .codigo(codigo)
                .status(StatusCupom.UTILIZADO)
                .dataResgate(LocalDateTime.now().minusDays(1))
                .aluno(aluno)
                .vantagem(vantagem)
                .build();

        ConferirCupomRequestDTO dto = new ConferirCupomRequestDTO(codigo.toString());

        when(empresaParceiraRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(cupomRepository.findByCodigo(codigo)).thenReturn(Optional.of(cupom));

        assertThatThrownBy(() -> service.conferirCupom(1L, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("já foi utilizado");

        verify(cupomRepository, never()).save(any());
    }
}

