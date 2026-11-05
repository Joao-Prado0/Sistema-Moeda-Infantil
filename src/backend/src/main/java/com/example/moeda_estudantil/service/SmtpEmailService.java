package com.example.moeda_estudantil.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Serviço de envio de e-mails via SMTP com suporte a templates HTML ricos
 * e fallback automático para modo local/desenvolvimento.
 *
 * Se as propriedades spring.mail.host e spring.mail.username estiverem configuradas,
 * envia mensagens reais via JavaMailSender (ex: Gmail, Mailtrap, Brevo, SendGrid).
 *
 * Caso contrário (ou em caso de falha de conexão/credencial no localhost),
 * registra a mensagem formatada no log sem interromper as transações do sistema.
 */
@Service
@Primary
public class SmtpEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.username:}")
    private String remetente;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${app.email.from-name:Moeda Estudantil}")
    private String nomeRemetente;

    // Histórico em memória dos últimos e-mails processados (útil para inspeção em desenvolvimento)
    private final List<RegistroEmail> historicoRecente = Collections.synchronizedList(new ArrayList<>());

    public SmtpEmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    @Override
    public void enviarCupom(String destinatarioAluno,
                            String destinatarioEmpresa,
                            String nomeAluno,
                            String nomeVantagem,
                            String codigoCupom) {

        // 1. E-mail para o Aluno com o Cupom de Resgate
        String assuntoAluno = "Seu Cupom de Vantagem: " + nomeVantagem;
        String htmlAluno = gerarHtmlCupomAluno(nomeAluno, nomeVantagem, codigoCupom);
        enviarMensagem(destinatarioAluno, assuntoAluno, htmlAluno, "Cupom para Aluno");

        // 2. E-mail para a Empresa Parceira notificando o resgate
        if (destinatarioEmpresa != null && !destinatarioEmpresa.isBlank()) {
            String assuntoEmpresa = "Novo Resgate de Vantagem: " + nomeVantagem;
            String htmlEmpresa = gerarHtmlCupomEmpresa(nomeAluno, nomeVantagem, codigoCupom);
            enviarMensagem(destinatarioEmpresa, assuntoEmpresa, htmlEmpresa, "Notificação para Empresa");
        }
    }

    @Override
    public void enviarNotificacaoRecebimento(String destinatarioAluno,
                                             String nomeAluno,
                                             String nomeProfessor,
                                             Double quantidade,
                                             String motivo) {

        String assunto = String.format("Você recebeu %.0f moedas de %s!", quantidade, nomeProfessor);
        String html = gerarHtmlRecebimentoMoedas(nomeAluno, nomeProfessor, quantidade, motivo);
        enviarMensagem(destinatarioAluno, assunto, html, "Reconhecimento de Moedas");
    }

    private void enviarMensagem(String destinatario, String assunto, String conteudoHtml, String tipoOperacao) {
        if (destinatario == null || destinatario.isBlank()) {
            log.warn("[EMAIL] Destinatário vazio para operação: {}", tipoOperacao);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        boolean smtpConfigurado = mailSender != null && remetente != null && !remetente.isBlank() && emailEnabled;

        if (smtpConfigurado) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setFrom(new InternetAddress(remetente, nomeRemetente));
                helper.setTo(destinatario);
                helper.setSubject(assunto);
                helper.setText(conteudoHtml, true);

                mailSender.send(mimeMessage);

                log.info("[EMAIL-SMTP] Enviado com sucesso via SMTP para <{}> | Assunto: '{}'", destinatario, assunto);
                registrarHistorico(destinatario, assunto, tipoOperacao, "ENVIADO_SMTP");
                return;
            } catch (Exception e) {
                log.warn("[EMAIL-SMTP] Não foi possível enviar via SMTP para <{}>: {}. Executando fallback em log.",
                        destinatario, e.getMessage());
            }
        }

        // Fallback para localhost / log de desenvolvimento
        log.info("[EMAIL-LOCAL] (Simulado) Para: <{}> | Assunto: '{}' | Operação: {}", destinatario, assunto, tipoOperacao);
        registrarHistorico(destinatario, assunto, tipoOperacao, "SIMULADO_LOCAL");
    }

    private void registrarHistorico(String destinatario, String assunto, String tipo, String status) {
        if (historicoRecente.size() >= 50) {
            historicoRecente.remove(0);
        }
        historicoRecente.add(new RegistroEmail(destinatario, assunto, tipo, status, System.currentTimeMillis()));
    }

    public List<RegistroEmail> getHistoricoRecente() {
        return Collections.unmodifiableList(new ArrayList<>(historicoRecente));
    }

    // ==========================================
    // Templates HTML dos E-mails
    // ==========================================

    private String gerarHtmlCupomAluno(String nomeAluno, String nomeVantagem, String codigoCupom) {
        return "<!DOCTYPE html>"
                + "<html lang='pt-BR'>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:24px;background-color:#F6F5FB;font-family:system-ui,-apple-system,sans-serif;color:#16142E;'>"
                + "  <div style='max-width:540px;margin:0 auto;background:#FFFFFF;border-radius:18px;border:1px solid #E7E5F1;overflow:hidden;box-shadow:0 8px 24px rgba(29,23,87,0.06);'>"
                + "    <div style='background:linear-gradient(135deg,#120E3A,#1D1757 70%,#29218A);padding:28px 24px;text-align:center;color:#FFFFFF;'>"
                + "      <div style='display:inline-block;padding:4px 12px;background:rgba(242,169,0,0.2);color:#FFC531;border-radius:99px;font-size:12px;font-weight:700;letter-spacing:0.04em;text-transform:uppercase;margin-bottom:12px;'>Resgate de Vantagem</div>"
                + "      <h1 style='margin:0;font-size:22px;font-weight:700;'>Parabéns, " + escapar(nomeAluno) + "!</h1>"
                + "      <p style='margin:6px 0 0;font-size:14px;color:#D2CEFC;'>Você utilizou suas moedas de mérito com sucesso.</p>"
                + "    </div>"
                + "    <div style='padding:28px 24px;text-align:center;'>"
                + "      <p style='margin:0 0 16px;font-size:15px;color:#5F5B7A;'>Apresente este código à empresa parceira para usufruir da sua vantagem:</p>"
                + "      <div style='background:#F4F3FF;border:2px dashed #4B3FD6;border-radius:12px;padding:16px;margin-bottom:20px;'>"
                + "        <span style='display:block;font-size:13px;font-weight:600;color:#6A5EF0;text-transform:uppercase;'>Vantagem Adquirida</span>"
                + "        <strong style='display:block;font-size:18px;color:#1D1757;margin:4px 0 12px;'>" + escapar(nomeVantagem) + "</strong>"
                + "        <div style='background:#FFFFFF;border-radius:8px;padding:12px;border:1px solid #E7E5F1;'>"
                + "          <span style='font-size:11px;color:#8A87A3;display:block;margin-bottom:4px;'>CÓDIGO DO CUPOM (UUID)</span>"
                + "          <code style='font-size:18px;font-weight:800;letter-spacing:2px;color:#F2A900;font-family:monospace;'>" + escapar(codigoCupom) + "</code>"
                + "        </div>"
                + "      </div>"
                + "      <p style='margin:0;font-size:13px;color:#8A87A3;'>Guarde este e-mail ou visualize seu cupom a qualquer momento na aba <strong>Meus cupons</strong> do sistema.</p>"
                + "    </div>"
                + "    <div style='background:#FBFAFF;padding:16px;text-align:center;border-top:1px solid #E7E5F1;font-size:12px;color:#8A87A3;'>"
                + "      Sistema de Moeda Estudantil • Reconhecimento acadêmico real"
                + "    </div>"
                + "  </div>"
                + "</body></html>";
    }

    private String gerarHtmlCupomEmpresa(String nomeAluno, String nomeVantagem, String codigoCupom) {
        return "<!DOCTYPE html>"
                + "<html lang='pt-BR'>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:24px;background-color:#F6F5FB;font-family:system-ui,-apple-system,sans-serif;color:#16142E;'>"
                + "  <div style='max-width:540px;margin:0 auto;background:#FFFFFF;border-radius:18px;border:1px solid #E7E5F1;overflow:hidden;box-shadow:0 8px 24px rgba(29,23,87,0.06);'>"
                + "    <div style='background:linear-gradient(135deg,#120E3A,#1D1757 70%,#29218A);padding:24px;text-align:center;color:#FFFFFF;'>"
                + "      <div style='display:inline-block;padding:4px 12px;background:rgba(242,169,0,0.2);color:#FFC531;border-radius:99px;font-size:12px;font-weight:700;letter-spacing:0.04em;text-transform:uppercase;margin-bottom:8px;'>Aviso à Empresa Parceira</div>"
                + "      <h1 style='margin:0;font-size:20px;font-weight:700;'>Novo Resgate Realizado</h1>"
                + "    </div>"
                + "    <div style='padding:24px;'>"
                + "      <p style='margin:0 0 16px;font-size:14px;color:#5F5B7A;'>Um estudante acabou de resgatar uma vantagem cadastrada pela sua empresa:</p>"
                + "      <table style='width:100%;border-collapse:collapse;margin-bottom:20px;font-size:14px;'>"
                + "        <tr><td style='padding:8px;border-bottom:1px solid #E7E5F1;color:#5F5B7A;'>Aluno:</td><td style='padding:8px;border-bottom:1px solid #E7E5F1;font-weight:600;color:#16142E;'>" + escapar(nomeAluno) + "</td></tr>"
                + "        <tr><td style='padding:8px;border-bottom:1px solid #E7E5F1;color:#5F5B7A;'>Vantagem:</td><td style='padding:8px;border-bottom:1px solid #E7E5F1;font-weight:600;color:#16142E;'>" + escapar(nomeVantagem) + "</td></tr>"
                + "        <tr><td style='padding:8px;color:#5F5B7A;'>Código Cupom:</td><td style='padding:8px;font-family:monospace;font-weight:700;color:#4B3FD6;'>" + escapar(codigoCupom) + "</td></tr>"
                + "      </table>"
                + "      <p style='margin:0;font-size:13px;color:#8A87A3;'>Você pode conferir e validar este cupom a qualquer momento na aba <strong>Conferir cupons</strong> do seu painel.</p>"
                + "    </div>"
                + "    <div style='background:#FBFAFF;padding:16px;text-align:center;border-top:1px solid #E7E5F1;font-size:12px;color:#8A87A3;'>"
                + "      Sistema de Moeda Estudantil • Portal de Parceiros"
                + "    </div>"
                + "  </div>"
                + "</body></html>";
    }

    private String gerarHtmlRecebimentoMoedas(String nomeAluno, String nomeProfessor, Double quantidade, String motivo) {
        return "<!DOCTYPE html>"
                + "<html lang='pt-BR'>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:24px;background-color:#F6F5FB;font-family:system-ui,-apple-system,sans-serif;color:#16142E;'>"
                + "  <div style='max-width:540px;margin:0 auto;background:#FFFFFF;border-radius:18px;border:1px solid #E7E5F1;overflow:hidden;box-shadow:0 8px 24px rgba(29,23,87,0.06);'>"
                + "    <div style='background:linear-gradient(135deg,#120E3A,#1D1757 70%,#29218A);padding:28px 24px;text-align:center;color:#FFFFFF;'>"
                + "      <div style='display:inline-block;padding:4px 12px;background:rgba(242,169,0,0.25);color:#FFC531;border-radius:99px;font-size:12px;font-weight:700;letter-spacing:0.04em;text-transform:uppercase;margin-bottom:12px;'>Reconhecimento Acadêmico</div>"
                + "      <h1 style='margin:0;font-size:22px;font-weight:700;'>Você recebeu moedas!</h1>"
                + "      <div style='margin-top:16px;display:inline-block;background:rgba(255,255,255,0.12);padding:10px 24px;border-radius:12px;border:1px solid rgba(255,255,255,0.2);'>"
                + "        <span style='font-size:32px;font-weight:800;color:#FFC531;'>+ " + String.format("%.0f", quantidade) + "</span>"
                + "        <span style='font-size:14px;color:#FFFFFF;margin-left:6px;font-weight:600;'>moedas</span>"
                + "      </div>"
                + "    </div>"
                + "    <div style='padding:28px 24px;'>"
                + "      <p style='margin:0 0 8px;font-size:14px;color:#5F5B7A;'><strong>Professor:</strong> " + escapar(nomeProfessor) + "</p>"
                + "      <p style='margin:0 0 16px;font-size:14px;color:#5F5B7A;'><strong>Mensagem / Motivo do reconhecimento:</strong></p>"
                + "      <div style='background:#FFF9E8;border-left:4px solid #F2A900;padding:14px;border-radius:6px;font-style:italic;color:#5F5B7A;margin-bottom:20px;'>"
                + "        \"" + escapar(motivo) + "\""
                + "      </div>"
                + "      <p style='margin:0;font-size:13px;color:#8A87A3;'>Seu saldo já foi atualizado! Acesse o sistema para conferir seu novo saldo ou resgatar vantagens no catálogo de parceiros.</p>"
                + "    </div>"
                + "    <div style='background:#FBFAFF;padding:16px;text-align:center;border-top:1px solid #E7E5F1;font-size:12px;color:#8A87A3;'>"
                + "      Sistema de Moeda Estudantil • Reconhecimento acadêmico real"
                + "    </div>"
                + "  </div>"
                + "</body></html>";
    }

    private String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    public record RegistroEmail(String destinatario, String assunto, String tipo, String status, long timestamp) {}
}
