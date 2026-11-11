package com.example.moeda_estudantil.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementação padrão de EmailService: registra o conteúdo de todos os e-mails no log.
 * Ativa quando app.email.enabled=false (ou ausente).
 */
@Service
@ConditionalOnProperty(name = "app.email.enabled", havingValue = "false", matchIfMissing = true)
public class LogEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(LogEmailService.class);

    @Override
    @Async
    public void enviarAvisoRecebimentoMoedas(String emailAluno, Double valor, String nomeProfessor, String motivo) {
        log.info("[EMAIL-LOG] Aviso ao aluno: destinatário <{}> | Moedas: {} | Professor: {} | Motivo: {}",
                emailAluno, valor, nomeProfessor, motivo);
    }

    @Override
    @Async
    public void enviarCupomAluno(String emailAluno, String codigoCupom, String nomeVantagem, String nomeEmpresa) {
        log.info("[EMAIL-LOG] Cupom ao aluno: destinatário <{}> | Cupom: {} | Vantagem: {} | Empresa: {}",
                emailAluno, codigoCupom, nomeVantagem, nomeEmpresa);
    }

    @Override
    @Async
    public void enviarAvisoResgateEmpresa(String emailEmpresa, String codigoCupom, String nomeVantagem, String nomeAluno, String cpfMascaradoAluno) {
        log.info("[EMAIL-LOG] Aviso à empresa parceira: destinatário <{}> | Cupom: {} | Vantagem: {} | Aluno: {} (CPF: {})",
                emailEmpresa, codigoCupom, nomeVantagem, nomeAluno, cpfMascaradoAluno);
    }
}
