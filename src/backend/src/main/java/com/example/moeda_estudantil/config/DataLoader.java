package com.example.moeda_estudantil.config;

import com.example.moeda_estudantil.model.Aluno;
import com.example.moeda_estudantil.model.Cupom;
import com.example.moeda_estudantil.model.Curso;
import com.example.moeda_estudantil.model.Departamento;
import com.example.moeda_estudantil.model.EmpresaParceira;
import com.example.moeda_estudantil.model.Endereco;
import com.example.moeda_estudantil.model.Instituicao;
import com.example.moeda_estudantil.model.Professor;
import com.example.moeda_estudantil.model.Transacao;
import com.example.moeda_estudantil.model.Vantagem;
import com.example.moeda_estudantil.model.enums.PerfilUsuario;
import com.example.moeda_estudantil.model.enums.StatusCupom;
import com.example.moeda_estudantil.model.enums.TipoTransacao;
import com.example.moeda_estudantil.repository.AlunoRepository;
import com.example.moeda_estudantil.repository.CupomRepository;
import com.example.moeda_estudantil.repository.CursoRepository;
import com.example.moeda_estudantil.repository.DepartamentoRepository;
import com.example.moeda_estudantil.repository.EmpresaParceiraRepository;
import com.example.moeda_estudantil.repository.InstituicaoRepository;
import com.example.moeda_estudantil.repository.ProfessorRepository;
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
                                      ProfessorRepository professorRepository,
                                      TransacaoRepository transacaoRepository,
                                      EmpresaParceiraRepository empresaParceiraRepository,
                                      VantagemRepository vantagemRepository,
                                      CupomRepository cupomRepository) {
        return args -> {

            // ── Instituição, Departamento e Cursos ──────────────────────────
            Instituicao pucMinas;
            Departamento icei;
            if (instituicaoRepository.count() == 0) {
                pucMinas = instituicaoRepository.save(
                        Instituicao.builder().nome("PUC Minas").build()
                );

                icei = departamentoRepository.save(
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
                icei = departamentoRepository.findAll().get(0);
            }

            Curso cursoEs = cursoRepository.findAll().stream()
                    .filter(c -> c.getNome().equals("Engenharia de Software"))
                    .findFirst()
                    .orElse(cursoRepository.findAll().get(0));

            // ── Empresas Parceiras ───────────────────────────────────────────
            EmpresaParceira cantinaCentral = empresaParceiraRepository
                    .findByEmail("cantina.central@parceiro.com")
                    .orElseGet(() -> empresaParceiraRepository.save(new EmpresaParceira(
                            null,
                            "cantina.central@parceiro.com",
                            "senha123",
                            PerfilUsuario.EMPRESA,
                            LocalDateTime.now().minusMonths(6),
                            "Cantina Central",
                            "00.000.001/0001-01"
                    )));

            EmpresaParceira secretaria = empresaParceiraRepository
                    .findByEmail("secretaria@parceiro.com")
                    .orElseGet(() -> empresaParceiraRepository.save(new EmpresaParceira(
                            null,
                            "secretaria@parceiro.com",
                            "senha123",
                            PerfilUsuario.EMPRESA,
                            LocalDateTime.now().minusMonths(6),
                            "Secretaria",
                            "00.000.002/0001-02"
                    )));

            EmpresaParceira papelariaCampus = empresaParceiraRepository
                    .findByEmail("papelaria.campus@parceiro.com")
                    .orElseGet(() -> empresaParceiraRepository.save(new EmpresaParceira(
                            null,
                            "papelaria.campus@parceiro.com",
                            "senha123",
                            PerfilUsuario.EMPRESA,
                            LocalDateTime.now().minusMonths(6),
                            "Papelaria Campus",
                            "00.000.003/0001-03"
                    )));

            EmpresaParceira copiadoraUp = empresaParceiraRepository
                    .findByEmail("copiadora.up@parceiro.com")
                    .orElseGet(() -> empresaParceiraRepository.save(new EmpresaParceira(
                            null,
                            "copiadora.up@parceiro.com",
                            "senha123",
                            PerfilUsuario.EMPRESA,
                            LocalDateTime.now().minusMonths(6),
                            "Copiadora UP",
                            "00.000.004/0001-04"
                    )));

            EmpresaParceira livrariaSaber = empresaParceiraRepository
                    .findByEmail("livraria.saber@parceiro.com")
                    .orElseGet(() -> empresaParceiraRepository.save(new EmpresaParceira(
                            null,
                            "livraria.saber@parceiro.com",
                            "senha123",
                            PerfilUsuario.EMPRESA,
                            LocalDateTime.now().minusMonths(6),
                            "Livraria Saber",
                            "00.000.005/0001-05"
                    )));

            // ── Vantagens das Empresas ──────────────────────────────────────
            Vantagem vRestaurante = vantagemRepository.findAll().stream()
                    .filter(v -> v.getTitulo().contains("restaurante"))
                    .findFirst()
                    .orElseGet(() -> vantagemRepository.save(Vantagem.builder()
                            .titulo("Desconto de 20% no restaurante")
                            .descricao("Ganhe 20% de desconto em qualquer refeição na Cantina Central.")
                            .custoMoedas(200.0)
                            .empresa(cantinaCentral)
                            .fotoUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&auto=format&fit=crop&q=60")
                            .build()));

            Vantagem vMensalidade = vantagemRepository.findAll().stream()
                    .filter(v -> v.getTitulo().contains("mensalidade"))
                    .findFirst()
                    .orElseGet(() -> vantagemRepository.save(Vantagem.builder()
                            .titulo("Desconto de 10% na mensalidade")
                            .descricao("Obtenha 10% de desconto na próxima mensalidade da instituição.")
                            .custoMoedas(500.0)
                            .empresa(secretaria)
                            .fotoUrl("https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=500&auto=format&fit=crop&q=60")
                            .build()));

            Vantagem vCaderno = vantagemRepository.findAll().stream()
                    .filter(v -> v.getTitulo().contains("Caderno"))
                    .findFirst()
                    .orElseGet(() -> vantagemRepository.save(Vantagem.builder()
                            .titulo("Caderno universitário")
                            .descricao("Caderno universitário 200 folhas capa dura.")
                            .custoMoedas(150.0)
                            .empresa(papelariaCampus)
                            .fotoUrl("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&auto=format&fit=crop&q=60")
                            .build()));

            Vantagem vLanche = vantagemRepository.findAll().stream()
                    .filter(v -> v.getTitulo().contains("Lanche"))
                    .findFirst()
                    .orElseGet(() -> vantagemRepository.save(Vantagem.builder()
                            .titulo("Lanche combo")
                            .descricao("Combo lanche + bebida na Cantina Central.")
                            .custoMoedas(80.0)
                            .empresa(cantinaCentral)
                            .fotoUrl("https://images.unsplash.com/photo-1521305916504-4a1121188589?w=500&auto=format&fit=crop&q=60")
                            .build()));

            Vantagem vImpressao = vantagemRepository.findAll().stream()
                    .filter(v -> v.getTitulo().contains("Impressão"))
                    .findFirst()
                    .orElseGet(() -> vantagemRepository.save(Vantagem.builder()
                            .titulo("Impressão (50 folhas)")
                            .descricao("50 folhas impressas em P&B na Copiadora UP.")
                            .custoMoedas(60.0)
                            .empresa(copiadoraUp)
                            .fotoUrl("https://images.unsplash.com/photo-1563986768609-322da13575f3?w=500&auto=format&fit=crop&q=60")
                            .build()));

            Vantagem vLivro = vantagemRepository.findAll().stream()
                    .filter(v -> v.getTitulo().contains("Livro"))
                    .findFirst()
                    .orElseGet(() -> vantagemRepository.save(Vantagem.builder()
                            .titulo("Livro técnico")
                            .descricao("Um livro técnico à escolha no catálogo da Livraria Saber.")
                            .custoMoedas(120.0)
                            .empresa(livrariaSaber)
                            .fotoUrl("https://images.unsplash.com/photo-1532012164546-f432f2e3777f?w=500&auto=format&fit=crop&q=60")
                            .build()));

            // ── Professores Pré-cadastrados (US09 e Wireframe 09) ─────────────
            Professor profJoao = professorRepository.findByEmail("joao.souza@pucminas.br")
                    .orElseGet(() -> professorRepository.save(new Professor(
                            null,
                            "joao.souza@pucminas.br",
                            "senha123",
                            PerfilUsuario.PROFESSOR,
                            LocalDateTime.of(2026, 1, 15, 9, 0),
                            "João Souza",
                            "111.111.111-11",
                            1450.0,
                            icei,
                            pucMinas
                    )));

            Professor profaAna = professorRepository.findByEmail("ana.lima@pucminas.br")
                    .orElseGet(() -> professorRepository.save(new Professor(
                            null,
                            "ana.lima@pucminas.br",
                            "senha123",
                            PerfilUsuario.PROFESSOR,
                            LocalDateTime.of(2026, 1, 15, 9, 0),
                            "Ana Lima",
                            "222.222.222-22",
                            850.0,
                            icei,
                            pucMinas
                    )));

            // ── Alunos (Wireframes 04, 09, 10) ───────────────────────────────
            Aluno maria = alunoRepository.findByEmail("maria.silva@pucminas.edu.br")
                    .orElseGet(() -> alunoRepository.save(new Aluno(
                            null,
                            "maria.silva@pucminas.edu.br",
                            "senha123",
                            PerfilUsuario.ALUNO,
                            LocalDateTime.of(2026, 2, 1, 8, 0),
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
                    )));

            Aluno pedro = alunoRepository.findByEmail("pedro.alves@pucminas.edu.br")
                    .orElseGet(() -> alunoRepository.save(new Aluno(
                            null,
                            "pedro.alves@pucminas.edu.br",
                            "senha123",
                            PerfilUsuario.ALUNO,
                            LocalDateTime.of(2026, 2, 1, 8, 0),
                            "Pedro Alves",
                            "000.000.000-02",
                            "MG-1000002",
                            50.0,
                            Endereco.builder()
                                    .logradouro("Av. Dom José Gaspar")
                                    .numero("500")
                                    .bairro("Coração Eucarístico")
                                    .cidade("Belo Horizonte")
                                    .estado("MG")
                                    .cep("30535-901")
                                    .build(),
                            pucMinas,
                            cursoEs
                    )));

            Aluno carla = alunoRepository.findByEmail("carla.dias@pucminas.edu.br")
                    .orElseGet(() -> alunoRepository.save(new Aluno(
                            null,
                            "carla.dias@pucminas.edu.br",
                            "senha123",
                            PerfilUsuario.ALUNO,
                            LocalDateTime.of(2026, 2, 1, 8, 0),
                            "Carla Dias",
                            "000.000.000-03",
                            "MG-1000003",
                            200.0,
                            Endereco.builder()
                                    .logradouro("Rua Padre Pedro Pinto")
                                    .numero("1200")
                                    .bairro("Venda Nova")
                                    .cidade("Belo Horizonte")
                                    .estado("MG")
                                    .cep("31510-000")
                                    .build(),
                            pucMinas,
                            cursoEs
                    )));

            // ── Histórico de Transações & Cupons (Consistência com Wireframes) ─
            if (transacaoRepository.count() == 0) {
                // 1. Crédito Semestral 2026/1 para Prof. João (+1.000)
                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(1000.0)
                        .dataHora(LocalDateTime.of(2026, 2, 1, 10, 0))
                        .tipo(TipoTransacao.CREDITO_SEMESTRAL)
                        .motivo("Crédito do semestre 2026/1")
                        .professor(profJoao)
                        .build());

                // 2. Crédito Semestral 2026/2 para Prof. João (+1.000) [Wireframe 09: 01/08/2026]
                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(1000.0)
                        .dataHora(LocalDateTime.of(2026, 8, 1, 10, 0))
                        .tipo(TipoTransacao.CREDITO_SEMESTRAL)
                        .motivo("Crédito do semestre 2026/2")
                        .professor(profJoao)
                        .build());

                // 3. Envio de Prof. João para Carla Dias (-200) [Wireframe 09: 28/07/2026]
                Transacao tCarla = transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(200.0)
                        .dataHora(LocalDateTime.of(2026, 7, 28, 14, 30))
                        .tipo(TipoTransacao.ENVIO)
                        .motivo("Excelente trabalho em grupo")
                        .professor(profJoao)
                        .aluno(carla)
                        .build());

                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(200.0)
                        .dataHora(tCarla.getDataHora())
                        .tipo(TipoTransacao.BONIFICACAO)
                        .motivo("Excelente trabalho em grupo")
                        .professor(profJoao)
                        .aluno(carla)
                        .build());

                // 4. Recebimento de Maria Silva: bônus base de reconhecimento (+900)
                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(900.0)
                        .dataHora(LocalDateTime.of(2026, 8, 15, 9, 0))
                        .tipo(TipoTransacao.BONIFICACAO)
                        .motivo("Bolsa mérito acadêmico")
                        .aluno(maria)
                        .build());

                // 5. Envio de Prof. João para Maria Silva (-50 / +50) [Wireframe 04 e 09: 25/09/2026]
                Transacao tMaria1 = transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(50.0)
                        .dataHora(LocalDateTime.of(2026, 9, 25, 11, 20))
                        .tipo(TipoTransacao.ENVIO)
                        .motivo("Bom comportamento")
                        .professor(profJoao)
                        .aluno(maria)
                        .build());

                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(50.0)
                        .dataHora(tMaria1.getDataHora())
                        .tipo(TipoTransacao.BONIFICACAO)
                        .motivo("Prof. João Souza — bom comportamento")
                        .professor(profJoao)
                        .aluno(maria)
                        .build());

                // 6. Resgate de Maria Silva: Caderno universitário (-150) [Wireframe 04: 01/10/2026]
                Transacao tResgate1 = transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(150.0)
                        .dataHora(LocalDateTime.of(2026, 10, 1, 16, 45))
                        .tipo(TipoTransacao.RESGATE_VANTAGEM)
                        .motivo("Material específico — caderno")
                        .aluno(maria)
                        .build());

                cupomRepository.save(Cupom.builder()
                        .aluno(maria)
                        .vantagem(vCaderno)
                        .transacao(tResgate1)
                        .dataResgate(tResgate1.getDataHora())
                        .status(StatusCupom.UTILIZADO)
                        .build());

                // 7. Envio de Profa. Ana para Maria Silva (+150) [Wireframe 04: 09/10/2026]
                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(150.0)
                        .dataHora(LocalDateTime.of(2026, 10, 9, 15, 10))
                        .tipo(TipoTransacao.BONIFICACAO)
                        .motivo("Profa. Ana Lima — excelente trabalho em grupo")
                        .professor(profaAna)
                        .aluno(maria)
                        .build());

                // 8. Envio de Prof. João para Pedro Alves (-50 / +50) [Wireframe 09: 10/10/2026]
                Transacao tPedro = transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(50.0)
                        .dataHora(LocalDateTime.of(2026, 10, 10, 10, 0))
                        .tipo(TipoTransacao.ENVIO)
                        .motivo("Bom comportamento")
                        .professor(profJoao)
                        .aluno(pedro)
                        .build());

                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(50.0)
                        .dataHora(tPedro.getDataHora())
                        .tipo(TipoTransacao.BONIFICACAO)
                        .motivo("Prof. João Souza — bom comportamento")
                        .professor(profJoao)
                        .aluno(pedro)
                        .build());

                // 9. Resgate de Maria Silva: Desconto de 20% no restaurante (-200) [Wireframe 04: 15/10/2026]
                Transacao tResgate2 = transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(200.0)
                        .dataHora(LocalDateTime.of(2026, 10, 15, 12, 30))
                        .tipo(TipoTransacao.RESGATE_VANTAGEM)
                        .motivo("Desconto no restaurante universitário")
                        .aluno(maria)
                        .build());

                cupomRepository.save(Cupom.builder()
                        .aluno(maria)
                        .vantagem(vRestaurante)
                        .transacao(tResgate2)
                        .dataResgate(tResgate2.getDataHora())
                        .status(StatusCupom.GERADO)
                        .build());

                // 10. Envio de Prof. João para Maria Silva (-100 / +100) [Wireframe 04 e 09: 18/10/2026]
                Transacao tMaria2 = transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(100.0)
                        .dataHora(LocalDateTime.of(2026, 10, 18, 14, 0))
                        .tipo(TipoTransacao.ENVIO)
                        .motivo("Participação em aula")
                        .professor(profJoao)
                        .aluno(maria)
                        .build());

                transacaoRepository.save(Transacao.builder()
                        .quantidadeMoedas(100.0)
                        .dataHora(tMaria2.getDataHora())
                        .tipo(TipoTransacao.BONIFICACAO)
                        .motivo("Prof. João Souza — participação em aula")
                        .professor(profJoao)
                        .aluno(maria)
                        .build());

                // Cupom adicional de impressão para completar histórico do Wireframe 07
                cupomRepository.save(Cupom.builder()
                        .aluno(maria)
                        .vantagem(vImpressao)
                        .dataResgate(LocalDateTime.of(2026, 9, 22, 11, 0))
                        .status(StatusCupom.UTILIZADO)
                        .build());
            }
        };
    }
}
