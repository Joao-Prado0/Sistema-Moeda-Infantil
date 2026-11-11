package com.example.moeda_estudantil.service;

/**
 * Interface para serviço de envio de e-mails do sistema de moeda estudantil.
 */
public interface EmailService {

    /**
     * (a) Aviso ao aluno quando recebe moedas (valor, nome do professor, motivo) - US05.
     *
     * @param emailAluno    e-mail do aluno beneficiado
     * @param valor         quantidade de moedas recebidas
     * @param nomeProfessor nome do professor remetente
     * @param motivo        justificativa / motivo do envio
     */
    void enviarAvisoRecebimentoMoedas(String emailAluno, Double valor, String nomeProfessor, String motivo);

    /**
     * (b) Cupom ao aluno (código, vantagem, empresa) - US08.
     *
     * @param emailAluno   e-mail do aluno que resgatou
     * @param codigoCupom  código UUID do cupom gerado
     * @param nomeVantagem título da vantagem adquirida
     * @param nomeEmpresa  nome da empresa parceira ofertante
     */
    void enviarCupomAluno(String emailAluno, String codigoCupom, String nomeVantagem, String nomeEmpresa);

    /**
     * (c) Aviso à empresa parceira com o mesmo código, a vantagem e a identificação do aluno (nome e CPF mascarado) - US17.
     *
     * @param emailEmpresa       e-mail da empresa parceira
     * @param codigoCupom        código UUID do cupom gerado
     * @param nomeVantagem       título da vantagem
     * @param nomeAluno          nome do aluno que efetuou o resgate
     * @param cpfMascaradoAluno  CPF mascarado do aluno
     */
    void enviarAvisoResgateEmpresa(String emailEmpresa, String codigoCupom, String nomeVantagem, String nomeAluno, String cpfMascaradoAluno);

    /**
     * Método utilitário de retrocompatibilidade para notificação de recebimento.
     */
    default void enviarNotificacaoRecebimento(String destinatarioAluno,
                                              String nomeAluno,
                                              String nomeProfessor,
                                              Double quantidade,
                                              String motivo) {
        enviarAvisoRecebimentoMoedas(destinatarioAluno, quantidade, nomeProfessor, motivo);
    }

    /**
     * Método utilitário de retrocompatibilidade para envio unificado de cupom.
     */
    default void enviarCupom(String destinatarioAluno,
                             String destinatarioEmpresa,
                             String nomeAluno,
                             String nomeVantagem,
                             String codigoCupom) {
        enviarCupomAluno(destinatarioAluno, codigoCupom, nomeVantagem, "");
        if (destinatarioEmpresa != null && !destinatarioEmpresa.isBlank()) {
            enviarAvisoResgateEmpresa(destinatarioEmpresa, codigoCupom, nomeVantagem, nomeAluno, "***");
        }
    }
}
