package com.example.moeda_estudantil.model;

import com.example.moeda_estudantil.exception.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Regras de saldo nas entidades Aluno e Professor")
class SaldoEntidadesTest {

    private Aluno alunoComSaldo(double saldo) {
        Aluno aluno = new Aluno();
        aluno.setSaldo(saldo);
        return aluno;
    }

    private Professor professorComSaldo(double saldo) {
        Professor professor = new Professor();
        professor.setSaldo(saldo);
        return professor;
    }

    @Test
    @DisplayName("Aluno: creditar e debitar alteram o saldo")
    void alunoCreditaEDebita() {
        Aluno aluno = alunoComSaldo(100.0);

        aluno.creditar(50.0);
        aluno.debitar(120.0);

        assertThat(aluno.getSaldo()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Aluno: debitar acima do saldo lança exceção e não altera o saldo")
    void alunoSaldoInsuficiente() {
        Aluno aluno = alunoComSaldo(100.0);

        assertThatThrownBy(() -> aluno.debitar(200.0))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Saldo insuficiente");
        assertThat(aluno.getSaldo()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Professor: creditar e debitar alteram o saldo")
    void professorCreditaEDebita() {
        Professor professor = professorComSaldo(0.0);

        professor.creditar(1000.0);
        professor.debitar(250.0);

        assertThat(professor.getSaldo()).isEqualTo(750.0);
    }

    @Test
    @DisplayName("Professor: debitar acima do saldo lança exceção e não altera o saldo")
    void professorSaldoInsuficiente() {
        Professor professor = professorComSaldo(10.0);

        assertThatThrownBy(() -> professor.debitar(11.0))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Saldo insuficiente");
        assertThat(professor.getSaldo()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("Quantidade zero ou negativa é rejeitada")
    void quantidadeInvalida() {
        Aluno aluno = alunoComSaldo(100.0);
        Professor professor = professorComSaldo(100.0);

        assertThatThrownBy(() -> aluno.creditar(0.0)).isInstanceOf(RegraNegocioException.class);
        assertThatThrownBy(() -> professor.debitar(-5.0)).isInstanceOf(RegraNegocioException.class);
    }
}
