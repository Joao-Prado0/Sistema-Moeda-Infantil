package com.example.moeda_estudantil.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EmailConditionalConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(JavaMailSender.class, () -> mock(JavaMailSender.class))
            .withUserConfiguration(LogEmailService.class, SmtpEmailService.class);

    @Test
    @DisplayName("Quando app.email.enabled=false, o LogEmailService deve ser injetado como EmailService")
    void deveUsarLogEmailServiceQuandoDesabilitado() {
        contextRunner.withPropertyValues("app.email.enabled=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(EmailService.class);
                    assertThat(context.getBean(EmailService.class)).isInstanceOf(LogEmailService.class);
                });
    }

    @Test
    @DisplayName("Quando app.email.enabled estiver ausente, o LogEmailService deve ser o padrão (matchIfMissing=true)")
    void deveUsarLogEmailServiceQuandoPropriedadeAusente() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(EmailService.class);
            assertThat(context.getBean(EmailService.class)).isInstanceOf(LogEmailService.class);
        });
    }

    @Test
    @DisplayName("Quando app.email.enabled=true, o SmtpEmailService deve ser injetado como EmailService")
    void deveUsarSmtpEmailServiceQuandoHabilitado() {
        contextRunner.withPropertyValues("app.email.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(EmailService.class);
                    assertThat(context.getBean(EmailService.class)).isInstanceOf(SmtpEmailService.class);
                });
    }
}
