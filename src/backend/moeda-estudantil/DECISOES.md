# Decisões Arquiteturais e Mapeamento de Entidades

Este documento registra as decisões técnicas tomadas na implementação das classes base do backend do Sistema de Moeda Estudantil, bem como o tratamento das ambiguidades identificadas no diagrama de classes (`doc/Diagrama_Classes.jpg`) e nas histórias de usuário (`doc/Historias_de_Usuario_Moeda_Estudantil (1).pdf`).

---

## 1. Tipo de Dado para Dinheiro/Moedas (`Double`)
- **Decisão**: Foi mantido o tipo `Double` para saldo (`saldo` em `Aluno` e `Professor`), quantidade de moedas (`quantidadeMoedas` em `Transacao`) e custo (`custoMoedas` em `Vantagem`), conforme expressamente especificado no diagrama de classes e nas instruções.
- **Observação técnica**: Em sistemas financeiros reais em produção, a melhor prática é o uso de `BigDecimal` ou tipos inteiros (para representação em centavos) para evitar problemas de imprecisão com ponto flutuante. A opção por `Double` preserva estrita aderência ao contrato do modelo de classes do laboratório acadêmico.

---

## 2. Estratégia de Herança da Entidade `Usuario`
- **Decisão**: Foi adotada a estratégia `@Inheritance(strategy = InheritanceType.JOINED)` na classe `Usuario`.
- **Justificativa**: Conforme especificação do projeto, a herança `JOINED` cria uma tabela base `usuarios` contendo os dados de autenticação e comuns (`id`, `email`, `senha`, `perfil`, `dataCriacao`), enquanto as tabelas derivadas (`alunos`, `professores`, `empresas_parceiras`) mantêm chaves primárias que também são chaves estrangeiras para a tabela base. Isso normaliza os dados e garante integridade referencial.

---

## 3. Ambiguidades Encontradas no Diagrama de Classes e Resoluções

### 3.1. Relacionamento `Transacao` e `Cupom`
- **Diagrama**: A linha entre `Transacao` e `Cupom` traz multiplicidade `0..1` do lado da `Transacao` e `*..1` do lado do `Cupom`.
- **Análise com as Histórias de Usuário**:
  - Uma transação do tipo `RESGATE_VANTAGEM` dá origem a um único cupom emitido para o aluno (US07 e US08). Transações de outros tipos (`BONIFICACAO`, `CREDITO_SEMESTRAL`) não geram cupom.
  - Cada cupom gerado decorre de um resgate específico.
- **Resolução no Modelo JPA**:
  - `Cupom` referencia a `Transacao` de origem via `@OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "transacao_id")`.
  - Essa modelagem atende perfeitamente à multiplicidade `0..1` (uma transação pode ou não ter um cupom vinculado) e mantém a rastreabilidade do resgate.

### 3.2. Participantes em `Transacao` (Aluno vs Professor)
- **Diagrama**: `Transacao` possui associações `*..1` com `Aluno` e `*..1` com `Professor`.
- **Análise com as Histórias de Usuário**:
  - Em `CREDITO_SEMESTRAL`, apenas o `Professor` é creditado (não há aluno destinatário).
  - Em `RESGATE_VANTAGEM`, o `Aluno` troca moedas por vantagem (não há professor envolvido).
  - Em `BONIFICACAO`, o `Professor` envia moedas ao `Aluno` (ambos presentes).
- **Resolução no Modelo JPA**:
  - Ambas as chaves estrangeiras (`aluno_id` e `professor_id`) são mapeadas com `@ManyToOne(fetch = FetchType.LAZY)`, permitindo valores nulos dependendo do `tipo` da transação (`TipoTransacao`).

### 3.3. Vínculo de `Professor` com `Instituicao` e `Departamento`
- **Diagrama**: Há uma relação explícita de `Professor` com `Instituicao` (`*..1`) e com `Departamento` (`*..1`).
- **Análise**: O enunciado e a US09 ("O vínculo com a instituição é sempre explícito") reforçam a necessidade da presença direta de `instituicao` na entidade `Professor`, além de `departamento`.
- **Resolução no Modelo JPA**: `Professor` possui explicitamente `@ManyToOne` para `Instituicao` e `@ManyToOne` para `Departamento`.

### 3.4. Vínculo de `Departamento` e `Curso` com `Instituicao`
- **Diagrama**: O diagrama exibe conexões entre `Instituicao` e `Departamento`, bem como entre `Instituicao` e `Curso` (`1..*` em ambas as pontas na notação do diagrama).
- **Resolução no Modelo JPA**: Mapeamento `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "instituicao_id")` em `Departamento` e `Curso`, permitindo que cada departamento e curso pertença a uma instituição de ensino.

### 3.5. `Endereco` como Objeto de Valor (`@Embeddable`)
- **Diagrama**: A relação entre `Aluno` e `Endereco` é expressa por composição (losango preenchido) com multiplicidade `1..1`.
- **Resolução no Modelo JPA**: A classe `Endereco` foi mapeada como `@Embeddable` e embutida na entidade `Aluno` com `@Embedded`, evitando uma tabela separada e integrando os campos de endereço diretamente na tabela `alunos`.

### 3.6. Tipo do Campo `codigo` na Entidade `Cupom`
- **Diagrama**: O tipo do atributo `codigo` em `Cupom` está explicitado como `UUID`.
- **Resolução no Modelo JPA**: O campo foi implementado com `java.util.UUID` e inicializado automaticamente com `UUID.randomUUID()` no ciclo de vida da entidade (`@PrePersist`).

