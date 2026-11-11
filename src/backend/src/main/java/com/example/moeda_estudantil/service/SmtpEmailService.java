package com.example.moeda_estudantil.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementação real de envio de e-mails via Brevo / SMTP usando JavaMailSender.
 * Ativa quando app.email.enabled=true.
 */
@Service
@ConditionalOnProperty(name = "app.email.enabled", havingValue = "true")
public class SmtpEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.email.from:danielbonycg@gmail.com}")
    private String remetente;

    public SmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void enviarAvisoRecebimentoMoedas(String emailAluno, Double valor, String nomeProfessor, String motivo) {
        try {
            String assunto = String.format("Você recebeu %.0f moedas de %s!", valor, nomeProfessor);
            String corpo = String.format(
                    "Olá!\n\nVocê recebeu %.0f moedas do professor %s.\n\nMotivo do reconhecimento:\n\"%s\"\n\n" +
                    "Acesse o sistema para visualizar seu novo saldo e o catálogo de vantagens disponíveis!\n\n" +
                    "Atenciosamente,\nSistema de Moeda Estudantil",
                    valor, nomeProfessor, motivo);

            enviarMensagem(emailAluno, assunto, corpo);
            log.info("[SMTP-BREVO] Notificação de moedas enviada com sucesso para <{}>.", emailAluno);
        } catch (Exception e) {
            log.error("[SMTP-BREVO] Erro ao enviar aviso de moedas para <{}>: {}", emailAluno, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void enviarCupomAluno(String emailAluno, String codigoCupom, String nomeVantagem, String nomeEmpresa) {
        try {
            String assunto = "Seu Cupom de Vantagem: " + nomeVantagem;
            String corpo = String.format(
                    "Parabéns pelo resgate!\n\nVantagem: %s\nEmpresa parceira: %s\n\nCódigo do Cupom de Resgate:\n%s\n\n" +
                    "Apresente esse código no estabelecimento da empresa para usufruir do benefício.\n\n" +
                    "Atenciosamente,\nSistema de Moeda Estudantil",
                    nomeVantagem, nomeEmpresa, codigoCupom);

            enviarMensagem(emailAluno, assunto, corpo);
            log.info("[SMTP-BREVO] Cupom enviado com sucesso para o aluno <{}>.", emailAluno);
        } catch (Exception e) {
            log.error("[SMTP-BREVO] Erro ao enviar cupom para o aluno <{}>: {}", emailAluno, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void enviarAvisoResgateEmpresa(String emailEmpresa, String codigoCupom, String nomeVantagem, String nomeAluno, String cpfMascaradoAluno) {
        try {
            String assunto = "Novo Resgate de Vantagem: " + nomeVantagem;
            String corpo = String.format(
                    "Olá!\n\nUm aluno acabou de resgatar a sua vantagem '%s'.\n\n" +
                    "Dados do Resgate:\n- Aluno: %s\n- CPF: %s\n- Código do Cupom: %s\n\n" +
                    "Você pode validar este cupom a qualquer momento no seu painel de parceiro.\n\n" +
                    "Atenciosamente,\nSistema de Moeda Estudantil",
                    nomeVantagem, nomeAluno, cpfMascaradoAluno, codigoCupom);

            enviarMensagem(emailEmpresa, assunto, corpo);
            log.info("[SMTP-BREVO] Notificação de resgate enviada com sucesso para empresa parceira <{}>.", emailEmpresa);
        } catch (Exception e) {
            log.error("[SMTP-BREVO] Erro ao enviar notificação de resgate para empresa parceira <{}>: {}", emailEmpresa, e.getMessage(), e);
        }
    }

    private void enviarMensagem(String destinatario, String assunto, String corpo) throws Exception {
        if (destinatario == null || destinatario.isBlank()) {
            log.warn("[SMTP-BREVO] Destinatário não informado. Ignorando envio.");
            return;
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

        helper.setFrom(new InternetAddress(remetente, "Sistema de Moeda Estudantil"));
        helper.setTo(destinatario);
        helper.setSubject(assunto);
        helper.setText(corpo, false);

        mailSender.send(mimeMessage);
    }
}
