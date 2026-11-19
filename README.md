# Moeda Estudantil

Sistema para reconhecer o mérito estudantil por meio de uma moeda virtual. Professores distribuem moedas aos alunos por participação e bom comportamento, e os alunos trocam essas moedas por vantagens oferecidas por empresas parceiras, como descontos na cantina, na mensalidade e em materiais.

Projeto acadêmico da disciplina **Projeto de Software** (PUC Minas, Engenharia de Software).

---

## Sistema implantado

| Parte | Serviço | Endereço |
|---|---|---|
| **Aplicação (frontend)** | Vercel | https://sistema-moeda-estudantil-seven.vercel.app |
| API (backend) | Render | https://moeda-estudantil-api-hh22.onrender.com |
| Banco de dados | Neon (PostgreSQL) | acesso restrito |

> **Primeiro acesso lento:** o backend usa o plano gratuito do Render, que suspende o servidor após 15 minutos sem uso. A primeira requisição pode levar até cerca de 1 minuto. Depois disso o sistema responde normalmente.

### Contas para teste

Todas com a senha `senha123`.

| Perfil | Login (e-mail) |
|---|---|
| Aluno | `maria.silva@pucminas.edu.br` |
| Aluno | `pedro.alves@pucminas.edu.br` |
| Aluno | `carla.dias@pucminas.edu.br` |
| Professor | `joao.souza@pucminas.br` |
| Professor | `ana.lima@pucminas.br` |
| Empresa parceira | `cantina.central@parceiro.com` |
| Empresa parceira | `secretaria@parceiro.com` |
| Empresa parceira | `papelaria.campus@parceiro.com` |
| Empresa parceira | `copiadora.up@parceiro.com` |
| Empresa parceira | `livraria.saber@parceiro.com` |

O PDF de entrega com esses acessos está em [`doc/Lab04-Sprint2-Acesso.pdf`](doc/Lab04-Sprint2-Acesso.pdf).

### Arquitetura da implantação

```
Navegador ──▶ Vercel (React/Vite) ──/api/*──▶ Render (Spring Boot, Docker) ──▶ Neon (PostgreSQL)
```

- O frontend é estático. O arquivo `src/frontend/vercel.json` encaminha as chamadas `/api/*` para o backend no Render.
- O backend é construído a partir de `src/backend/Dockerfile` (Root Directory `src/backend`).
- A conexão com o banco e a chave administrativa vêm de variáveis de ambiente no Render (veja abaixo).

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
│   ├── backend/          # API REST em Spring Boot (MVC, Services, Repositories)
│   └── frontend/         # Interface em React 19 + Vite (Arquitetura Modular / Resources)
│       ├── src/
│       │   ├── components/layout/   # Navbar e Modais globais
│       │   ├── components/ui/       # Componentes atômicos (Input, Select, Toast, ConfirmDialog)
│       │   ├── contexts/            # AuthContext e ToastContext
│       │   ├── hooks/               # useAuth, useCountUp
│       │   ├── layouts/             # AppLayout (DashboardShell) e AuthLayout
│       │   ├── resources/           # Módulos por Domínio/Ator (Auth, Aluno, Professor, Empresa)
│       │   ├── routes/              # Configuração central de rotas e guards (routes.jsx)
│       │   ├── services/            # Cliente HTTP centralizado (api.js)
│       │   └── utils/               # Formatadores e máscaras
└── README.md
```

---

## Pré-requisitos

- JDK 21 ou superior
- Node.js 18 ou superior e npm
- Git
- Acesso à internet (o banco fica no Neon)
- Docker (apenas se quiser rodar o backend em contêiner)

---

## Como rodar localmente

### 0. Clonar o repositório

```bash
git clone https://github.com/Joao-Prado0/Sistema-Moeda-Infantil.git
cd Sistema-Moeda-Infantil
```

### 1. Backend

O banco é um PostgreSQL no Neon. A senha **não** deve ficar no código: informe-a por variável de ambiente antes de subir a API (peça a senha atual ao grupo).

**Windows (PowerShell):**

```powershell
cd src/backend
$env:SPRING_DATASOURCE_PASSWORD = "SENHA_DO_BANCO"
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
cd src/backend
export SPRING_DATASOURCE_PASSWORD="SENHA_DO_BANCO"
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Para testar rapidamente: `http://localhost:8080/api/instituicoes`.

Variáveis de ambiente aceitas (todas opcionais, exceto a senha do banco):

| Variável | Para que serve | Padrão |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC do PostgreSQL | banco Neon do projeto |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `neondb_owner` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | (obrigatória) |
| `ADMIN_KEY` | Chave do endpoint administrativo (`X-Admin-Key`) | `admin-secret-key` |
| `PORT` | Porta HTTP | `8080` |
| `APP_EMAIL_ENABLED` | Liga o envio real de e-mail | `false` |

Para rodar os testes:

```bash
./mvnw test
```

Para usar um banco local em memória (H2), comente a conexão do Neon e descomente o bloco H2 em `src/backend/src/main/resources/application.properties`.

Ao iniciar, a classe `DataLoader` cria dados de exemplo (instituição, cursos, empresas parceiras, professores, alunos e vantagens) caso ainda não existam. As contas de teste estão na seção [Sistema implantado](#sistema-implantado).

### 2. Frontend

Em outro terminal:

```bash
cd src/frontend
npm install
npm run dev
```

A interface fica em `http://localhost:5173`. As chamadas para `/api` são encaminhadas para o backend em `localhost:8080` (proxy configurado em `vite.config.js`). Mantenha o backend rodando.

Para gerar a versão de produção: `npm run build` (saída em `dist/`).

### 3. Rodar o backend com Docker (opcional)

```bash
cd src/backend
docker build -t moeda-estudantil-api .
docker run -p 8080:8080 -e SPRING_DATASOURCE_PASSWORD="SENHA_DO_BANCO" moeda-estudantil-api
```

---

## Como implantar

**Backend (Render)**
1. Novo **Web Service** apontando para este repositório, branch `main`.
2. **Language:** Docker. **Root Directory:** `src/backend`.
3. Variáveis de ambiente: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` e `ADMIN_KEY`.

**Frontend (Vercel)**
1. Em `src/frontend/vercel.json`, confira se o destino de `/api/:path*` é a URL do backend no Render.
2. Pelo terminal, a partir de `src/frontend`: `npm i -g vercel`, `vercel login` e `vercel --prod`.
   Alternativa: importar o repositório na Vercel com **Root Directory** `src/frontend` (framework Vite, saída `dist`).

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