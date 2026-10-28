package com.example.moeda_estudantil.config;

import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.Curso;
import com.example.moeda_estudantil.model.Departamento;
import com.example.moeda_estudantil.model.EmpresaParceira;
import com.example.moeda_estudantil.model.Endereco;
import com.example.moeda_estudantil.model.Instituicao;
import com.example.moeda_estudantil.model.Transacao;
import com.example.moeda_estudantil.model.Vantagem;
import com.example.moeda_estudantil.model.enums.PerfilUsuario;
import com.example.moeda_estudantil.model.enums.TipoTransacao;
import com.example.moeda_estudantil.repository.AlunoRepository;
import com.example.moeda_estudantil.repository.CursoRepository;
import com.example.moeda_estudantil.repository.DepartamentoRepository;
import com.example.moeda_estudantil.repository.EmpresaParceiraRepository;
import com.example.moeda_estudantil.repository.InstituicaoRepository;
import com.example.moeda_estudantil.repository.TransacaoRepository;
import com.example.moeda_estudantil.repository.VantagemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initData(InstituicaoRepository instituicaoRepository,
                                      CursoRepository cursoRepository,
                                      DepartamentoRepository departamentoRepository,
                                      AlunoRepository alunoRepository,
                                      TransacaoRepository transacaoRepository,
                                      EmpresaParceiraRepository empresaParceiraRepository,
                                      VantagemRepository vantagemRepository) {
        return args -> {

            // ── Instituição, Departamento e Cursos ──────────────────────────
            Instituicao pucMinas;
            if (instituicaoRepository.count() == 0) {
                pucMinas = instituicaoRepository.save(
                        Instituicao.builder().nome("PUC Minas").build()
                );

                departamentoRepository.save(
                        Departamento.builder()
                                .nome("Instituto de Ciências Exatas e Informática")
                                .instituicao(pucMinas)
                                .build()
                );

                cursoRepository.save(
                        Curso.builder().nome("Engenharia de Software").instituicao(pucMinas).build()
                );
                cursoRepository.save(
                        Curso.builder().nome("Ciência da Computação").instituicao(pucMinas).build()
                );
            } else {
                pucMinas = instituicaoRepository.findAll().get(0);
            }

            // ── Aluna Maria Silva ────────────────────────────────────────────
            // Saldo alvo: 850 moedas
            // Transações:
            //   Recebimentos: CREDITO_SEMESTRAL(500) + BONIFICACAO(100) + BONIFICACAO(150) + BONIFICACAO(50) = 800
            //   Resgates:    RESGATE_VANTAGEM(200) + RESGATE_VANTAGEM(150) = 350  (não usado abaixo; ajuste)
            //   Saldo resultante calculado manualmente: 800 - 350 = 450 ≠ 850
            //
            // Para totalRecebido visar 1.200 e saldo 850:
            //   Recebimentos: 1.200
            //   Trocas: 1.200 - 850 = 350  (200 + 150 = 350)  ✔
            //
            // Distribuição de recebimentos: 500 + 350 + 200 + 100 + 50 = 1.200
            if (!alunoRepository.existsByEmail("maria.silva@pucminas.edu.br")) {
                Curso cursoEs = cursoRepository.findAll().stream()
                        .filter(c -> c.getNome().equals("Engenharia de Software"))
                        .findFirst()
                        .orElse(cursoRepository.findAll().get(0));

                Aluno maria = new Aluno(
                        null,
                        "maria.silva@pucminas.edu.br",
                        "senha123",
                        PerfilUsuario.ALUNO,
                        LocalDateTime.now(),
                        "Maria Silva",
                        "000.000.000-01",
                        "MG-1000001",
                        850.0,
                        Endereco.builder()
                                .logradouro("Rua dos Estudantes")
                                .numero("100")
                                .bairro("Coração Eucarístico")
                                .cidade("Belo Horizonte")
                                .estado("MG")
                                .cep("30535-090")
                                .build(),
                        pucMinas,
                        cursoEs
                );
                maria = alunoRepository.save(maria);

                LocalDateTime base = LocalDateTime.now().minusDays(60);

                // Recebimentos (total = 1.200)
                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(500.0)
                        .dataHora(base)
                        .tipo(TipoTransacao.CREDITO_SEMESTRAL)
                        .motivo("Crédito semestral")
                        .aluno(maria)
                        .build());

                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(350.0)
                        .dataHora(base.plusDays(5))
                        .tipo(TipoTransacao.BONIFICACAO)
                        .motivo("Participação em projeto de extensão")
                        .aluno(maria)
                        .build());

                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(200.0)
                        .dataHora(base.plusDays(10))
                        .tipo(TipoTransacao.BONIFICACAO)
                        .motivo("Desempenho acadêmico")
                        .aluno(maria)
                        .build());

                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(100.0)
                        .dataHora(base.plusDays(20))
                        .tipo(TipoTransacao.BONIFICACAO)
                        .motivo("Atividade complementar")
                        .aluno(maria)
                        .build());

                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(50.0)
                        .dataHora(base.plusDays(25))
                        .tipo(TipoTransacao.BONIFICACAO)
                        .motivo("Participação em palestra")
                        .aluno(maria)
                        .build());

                // Resgates (total = 350; saldo: 1200 - 350 = 850 ✔)
                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(200.0)
                        .dataHora(base.plusDays(30))
                        .tipo(TipoTransacao.RESGATE_VANTAGEM)
                        .motivo("Resgate: Desconto de 20% no restaurante")
                        .aluno(maria)
                        .build());

                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(150.0)
                        .dataHora(base.plusDays(40))
                        .tipo(TipoTransacao.RESGATE_VANTAGEM)
                        .motivo("Resgate: Caderno universitário")
                        .aluno(maria)
                        .build());
            }

            // ── Empresas Parceiras ───────────────────────────────────────────
            EmpresaParceira cantinaCentral = empresaParceiraRepository
                    .findByEmail("cantina.central@parceiro.com")
                    .orElseGet(() -> {
                        EmpresaParceira e = new EmpresaParceira(
                                null,
                                "cantina.central@parceiro.com",
                                "senha123",
                                PerfilUsuario.EMPRESA,
                                LocalDateTime.now(),
                                "Cantina Central",
                                "00.000.001/0001-01"
                        );
                        return empresaParceiraRepository.save(e);
                    });

            EmpresaParceira secretaria = empresaParceiraRepository
                    .findByEmail("secretaria@parceiro.com")
                    .orElseGet(() -> {
                        EmpresaParceira e = new EmpresaParceira(
                                null,
                                "secretaria@parceiro.com",
                                "senha123",
                                PerfilUsuario.EMPRESA,
                                LocalDateTime.now(),
                                "Secretaria",
                                "00.000.002/0001-02"
                        );
                        return empresaParceiraRepository.save(e);
                    });

            EmpresaParceira papelariaCampus = empresaParceiraRepository
                    .findByEmail("papelaria.campus@parceiro.com")
                    .orElseGet(() -> {
                        EmpresaParceira e = new EmpresaParceira(
                                null,
                                "papelaria.campus@parceiro.com",
                                "senha123",
                                PerfilUsuario.EMPRESA,
                                LocalDateTime.now(),
                                "Papelaria Campus",
                                "00.000.003/0001-03"
                        );
                        return empresaParceiraRepository.save(e);
                    });

            EmpresaParceira copiadoraUp = empresaParceiraRepository
                    .findByEmail("copiadora.up@parceiro.com")
                    .orElseGet(() -> {
                        EmpresaParceira e = new EmpresaParceira(
                                null,
                                "copiadora.up@parceiro.com",
                                "senha123",
                                PerfilUsuario.EMPRESA,
                                LocalDateTime.now(),
                                "Copiadora UP",
                                "00.000.004/0001-04"
                        );
                        return empresaParceiraRepository.save(e);
                    });

            EmpresaParceira livrariaSaber = empresaParceiraRepository
                    .findByEmail("livraria.saber@parceiro.com")
                    .orElseGet(() -> {
                        EmpresaParceira e = new EmpresaParceira(
                                null,
                                "livraria.saber@parceiro.com",
                                "senha123",
                                PerfilUsuario.EMPRESA,
                                LocalDateTime.now(),
                                "Livraria Saber",
                                "00.000.005/0001-05"
                        );
                        return empresaParceiraRepository.save(e);
                    });

            // ── Vantagens ────────────────────────────────────────────────────
            if (vantagemRepository.count() == 0) {
                vantagemRepository.save(Vantagem.builder()
                        .titulo("Desconto de 20% no restaurante")
                        .descricao("Ganhe 20% de desconto em qualquer refeição na Cantina Central.")
                        .custoMoedas(200.0)
                        .empresa(cantinaCentral)
                        .build());

                vantagemRepository.save(Vantagem.builder()
                        .titulo("Desconto de 10% na mensalidade")
                        .descricao("Obtenha 10% de desconto na próxima mensalidade da instituição.")
                        .custoMoedas(500.0)
                        .empresa(secretaria)
                        .build());

                vantagemRepository.save(Vantagem.builder()
                        .titulo("Caderno universitário")
                        .descricao("Caderno universitário 200 folhas capa dura.")
                        .custoMoedas(150.0)
                        .empresa(papelariaCampus)
                        .build());

                vantagemRepository.save(Vantagem.builder()
                        .titulo("Lanche combo")
                        .descricao("Combo lanche + bebida na Cantina Central.")
                        .custoMoedas(80.0)
                        .empresa(cantinaCentral)
                        .build());

                vantagemRepository.save(Vantagem.builder()
                        .titulo("Impressão 50 folhas")
                        .descricao("50 folhas impressas em P&B na Copiadora UP.")
                        .custoMoedas(60.0)
                        .empresa(copiadoraUp)
                        .build());

                vantagemRepository.save(Vantagem.builder()
                        .titulo("Livro técnico")
                        .descricao("Um livro técnico à escolha no catálogo da Livraria Saber.")
                        .custoMoedas(120.0)
                        .empresa(livrariaSaber)
                        .build());
            }
        };
    }
}
