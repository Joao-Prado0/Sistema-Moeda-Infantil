package com.example.moeda_estudantil.service;

/**
 * Serviço de envio de e-mail para o sistema de moeda estudantil.
 * <p>
 * A implementação atual registra as mensagens apenas em log (mock).
 * TODO: Substituir LogEmailService por SmtpEmailService quando o SMTP
 *       estiver configurado em application.properties (spring.mail.*).
 *       Injetar JavaMailSender e usar MimeMessageHelper para envio real.
 */
public interface EmailService {

    /**
     * Envia o cupom gerado para o aluno e para a empresa parceira.
     *
     * @param destinatarioAluno   e-mail do aluno que resgatou a vantagem
     * @param destinatarioEmpresa e-mail da empresa parceira
     * @param nomeAluno           nome do aluno
     * @param nomeVantagem        nome da vantagem resgatada
     * @param codigoCupom         código UUID do cupom gerado
     */
    void enviarCupom(String destinatarioAluno,
                     String destinatarioEmpresa,
                     String nomeAluno,
                     String nomeVantagem,
                     String codigoCupom);

    /**
     * Notifica o aluno que recebeu moedas de um professor.
     *
     * @param destinatarioAluno e-mail do aluno beneficiado
     * @param nomeAluno         nome do aluno beneficiado
     * @param nomeProfessor     nome do professor que enviou
     * @param quantidade        quantidade de moedas enviadas
     * @param motivo            motivo descrito pelo professor
     */
    void enviarNotificacaoRecebimento(String destinatarioAluno,
                                      String nomeAluno,
                                      String nomeProfessor,
                                      Double quantidade,
                                      String motivo);
}
