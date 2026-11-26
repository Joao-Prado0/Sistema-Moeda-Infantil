package com.example.moeda_estudantil.service;

import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.Cupom;
import com.example.moeda_estudantil.model.EmpresaParceira;
import com.example.moeda_estudantil.model.Professor;
import com.example.moeda_estudantil.model.Vantagem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Concentra as notificações por e-mail disparadas pelas regras de negócio.
 *
 * <p>Os serviços de domínio (envio de moedas, resgate de vantagem) apenas informam o que
 * aconteceu; esta classe decide quais e-mails enviar, monta os dados de cada um e garante
 * que o envio só ocorre depois do commit da transação. Uma falha no envio nunca desfaz a
 * operação de negócio.</p>
 */
@Service
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    private final EmailService emailService;

    public NotificacaoService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Avisa o aluno de que recebeu moedas de um professor (US05).
     */
    public void notificarRecebimentoMoedas(Aluno aluno, Professor professor, double quantidade, String motivo) {
        final String emailAluno = aluno.getEmail();
        final String nomeProfessor = professor.getNome();

        executarAposCommit(() ->
                emailService.enviarAvisoRecebimentoMoedas(emailAluno, quantidade, nomeProfessor, motivo));
    }

    /**
     * Envia o cupom ao aluno (US08) e o aviso de conferência à empresa parceira (US17),
     * ambos com o mesmo código gerado pelo sistema.
     */
    public void notificarResgate(Cupom cupom) {
        Aluno aluno = cupom.getAluno();
        Vantagem vantagem = cupom.getVantagem();
        EmpresaParceira empresa = vantagem.getEmpresa();

        final String codigoCupom = cupom.getCodigo().toString();
        final String tituloVantagem = vantagem.getTitulo();
        final String emailAluno = aluno.getEmail();
        final String nomeAluno = aluno.getNome();
        final String cpfMascarado = mascararCpf(aluno.getCpf());
        final String nomeEmpresa = empresa != null ? empresa.getNomeFantasia() : "";
        final String emailEmpresa = empresa != null ? empresa.getEmail() : null;

        executarAposCommit(() -> {
            emailService.enviarCupomAluno(emailAluno, codigoCupom, tituloVantagem, nomeEmpresa);
            if (emailEmpresa != null && !emailEmpresa.isBlank()) {
                emailService.enviarAvisoResgateEmpresa(
                        emailEmpresa, codigoCupom, tituloVantagem, nomeAluno, cpfMascarado);
            }
        });
    }

    private void executarAposCommit(Runnable envio) {
        Runnable envioSeguro = () -> {
            try {
                envio.run();
            } catch (Exception e) {
                // Falha no envio nunca desfaz a operação de negócio
                log.warn("Falha ao enviar notificação por e-mail: {}", e.getMessage());
            }
        };

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    envioSeguro.run();
                }
            });
        } else {
            envioSeguro.run();
        }
    }

    static String mascararCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return "***";
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() == 11) {
            return "***." + digits.substring(3, 6) + "." + digits.substring(6, 9) + "-**";
        }
        return "***";
    }
}
