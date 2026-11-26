package com.example.moeda_estudantil.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreditoSemestralService - cálculo do semestre")
class CreditoSemestralServiceTest {

    @Test
    @DisplayName("Janeiro a junho é o semestre 1; julho a dezembro é o semestre 2 (datas-limite incluídas)")
    void calculaSemestrePelaData() {
        assertThat(CreditoSemestralService.semestreDe(LocalDate.of(2026, 1, 1))).isEqualTo("2026/1");
        assertThat(CreditoSemestralService.semestreDe(LocalDate.of(2026, 6, 30))).isEqualTo("2026/1");
        assertThat(CreditoSemestralService.semestreDe(LocalDate.of(2026, 7, 1))).isEqualTo("2026/2");
        assertThat(CreditoSemestralService.semestreDe(LocalDate.of(2026, 12, 31))).isEqualTo("2026/2");
    }
}
