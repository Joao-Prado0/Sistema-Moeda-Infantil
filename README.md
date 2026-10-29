# Moeda Estudantil

Sistema para reconhecer o mérito estudantil por meio de uma moeda virtual. Professores distribuem moedas aos alunos por participação e bom comportamento, e os alunos trocam essas moedas por vantagens oferecidas por empresas parceiras, como descontos na cantina, na mensalidade e em materiais.

Projeto acadêmico da disciplina **Projeto de Software** (PUC Minas, Engenharia de Software).

---

## Funcionalidades

**Aluno**
- Cadastro com dados pessoais, endereço, instituição e curso
- Dashboard com saldo, total recebido, total trocado e extrato
- Catálogo de vantagens e resgate com confirmação
- Lista de cupons gerados

**Professor**
- Cadastro com instituição e departamento
- Saldo de 1.000 moedas por semestre, acumulável
- Envio de moedas a alunos com motivo obrigatório
- Extrato de envios

**Empresa parceira**
- Cadastro e edição de perfil
- CRUD de vantagens (título, descrição, custo e foto)
- Listagem de cupons resgatados
- Conferência de cupom pelo código, marcando como utilizado

---

## Tecnologias

**Backend:** Java 21, Spring Boot 4, Spring Data JPA, Spring Security, Spring Mail, Bean Validation, Lombok, PostgreSQL, JUnit e Mockito.

**Frontend:** React 19, Vite, React Router, Lucide React.

**Banco de dados:** PostgreSQL (Neon, em nuvem).

---

## Estrutura do repositório

```
Sistema-Moeda-Infantil/
├── doc/                  # Diagramas, wireframes e histórias de usuário
├── src/
│   ├── backend/          # API REST em Spring Boot
│   └── frontend/         # Interface em React + Vite
└── README.md
```

---

## Pré-requisitos

- JDK 21 ou superior
- Node.js 18 ou superior e npm
- Acesso à internet (o banco fica no Neon)

---

## Como rodar

### 1. Backend

```bash
cd src/backend
./mvnw spring-boot:run          # Linux/macOS
mvnw.cmd spring-boot:run        # Windows
```

A API sobe em `http://localhost:8080`.

Para rodar os testes:

```bash
./mvnw test
```

> **Configuração do banco:** a conexão está em `src/backend/src/main/resources/application.properties`. Antes de compartilhar o projeto, troque a senha do banco por variáveis de ambiente (`SPRING_DATASOURCE_PASSWORD`) e não suba o arquivo com a senha real.

Para usar um banco local em memória (H2), descomente o bloco H2 no mesmo arquivo.

Ao iniciar, a classe `DataLoader` cria dados de exemplo (instituição, empresas parceiras e vantagens) caso as tabelas estejam vazias.

### 2. Frontend

```bash
cd src/frontend
npm install
npm run dev
```

A interface fica em `http://localhost:5173`. As chamadas para `/api` são encaminhadas para o backend (confira a configuração de proxy em `vite.config.js`).

---

## Autenticação

O login é feito em `POST /api/auth/login` com `email` e `senha`. A resposta traz o `id`, o `perfil` (`ALUNO`, `PROFESSOR` ou `EMPRESA`) e um `token`.

Nas demais chamadas, o front envia o id do usuário logado no header correspondente:

| Perfil | Header |
|---|---|
| Aluno | `X-Aluno-Id` |
| Professor | `X-Professor-Id` |
| Empresa | `X-Empresa-Id` |

> **Atenção:** essa identificação por header é provisória. Ela não é segura, porque qualquer cliente pode enviar outro id. A próxima etapa é trocar por JWT ou sessão.

---

## Endpoints principais

| Método | Caminho | Descrição |
|---|---|---|
| POST | `/api/auth/login` | Login de qualquer perfil |
| POST | `/api/alunos` | Cadastro de aluno |
| GET | `/api/alunos/me/dashboard` | Dashboard do aluno logado |
| GET | `/api/alunos/me/cupons` | Cupons do aluno logado |
| GET | `/api/vantagens` | Catálogo de vantagens |
| POST | `/api/vantagens/{id}/resgate` | Resgate de vantagem |
| POST | `/api/professores` | Cadastro de professor |
| GET | `/api/professores/me/dashboard` | Dashboard do professor logado |
| POST | `/api/professores/me/envios` | Envio de moedas a um aluno |
| POST | `/api/professores/{id}/credito-semestral` | Crédito semestral (uso de teste) |
| POST | `/api/empresas` | Cadastro de empresa |
| GET | `/api/empresas/me/vantagens` | Vantagens da empresa logada |
| POST | `/api/empresas/me/vantagens` | Criar vantagem |
| PUT | `/api/empresas/me/vantagens/{id}` | Editar vantagem |
| DELETE | `/api/empresas/me/vantagens/{id}` | Excluir vantagem (bloqueado se houver cupons) |
| GET | `/api/empresas/me/cupons` | Cupons resgatados na empresa |
| POST | `/api/empresas/me/cupons/conferir` | Conferir cupom pelo código |
| GET | `/api/instituicoes` | Lista de instituições |
| GET | `/api/departamentos` | Lista de departamentos |

---

## Regras de negócio

- Professores recebem 1.000 moedas por semestre. O saldo não utilizado é somado ao do semestre seguinte.
- O envio de moedas exige saldo suficiente e um motivo não vazio.
- Ao resgatar uma vantagem, o custo é debitado do saldo do aluno e um cupom com código único é gerado.
- O aluno recebe e-mail ao receber moedas, e o aluno e a empresa recebem o cupom por e-mail no resgate.
- Cupons têm dois estados: `GERADO` (exibido como "Pendente" no front) e `UTILIZADO`.

---

## Status do projeto

Em desenvolvimento. Ainda não implementado:

- Autenticação segura (JWT ou sessão) em lugar dos headers de id
- Envio de e-mail real (hoje as mensagens só são registradas no log)
- Upload de foto das vantagens (hoje é só uma URL)
- Crédito semestral automático (agendamento)
- Telas de meu perfil do aluno e de login de professor completas

---

## Documentação

Os diagramas de classes, componentes, casos de uso, modelo ER e os wireframes estão na pasta `doc/`. As decisões técnicas estão em `src/backend/DECISOES.md`.

## Equipe

Projeto desenvolvido pela equipe da disciplina (PUC Minas). Veja o histórico de commits para as contribuições individuais.

## Licença

Veja o arquivo [LICENSE](LICENSE).