package com.example.moeda_estudantil.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementação mock do EmailService: registra as mensagens no log.
 * <p>
 * TODO: Quando o SMTP estiver disponível, crie SmtpEmailService implementando
 *       EmailService, injete JavaMailSender e marque esta classe com @Profile("dev")
 *       para que ela só seja usada fora de produção.
 */
@Service
public class LogEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(LogEmailService.class);

    @Override
    public void enviarCupom(String destinatarioAluno,
                            String destinatarioEmpresa,
                            String nomeAluno,
                            String nomeVantagem,
                            String codigoCupom) {

        log.info("[EMAIL-MOCK] Para aluno <{}> | Assunto: Seu cupom de resgate | Vantagem: {} | Código: {}",
                destinatarioAluno, nomeVantagem, codigoCupom);

        log.info("[EMAIL-MOCK] Para empresa <{}> | Assunto: Novo resgate de vantagem | Aluno: {} | Vantagem: {} | Código: {}",
                destinatarioEmpresa, nomeAluno, nomeVantagem, codigoCupom);
    }

    @Override
    public void enviarNotificacaoRecebimento(String destinatarioAluno,
                                             String nomeAluno,
                                             String nomeProfessor,
                                             Double quantidade,
                                             String motivo) {
        // TODO: implementar envio SMTP real
        log.info("[EMAIL-MOCK] Para aluno <{}> ({}) | Assunto: Você recebeu {} moedas | De: {} | Motivo: {}",
                destinatarioAluno, nomeAluno, quantidade, nomeProfessor, motivo);
    }
}
