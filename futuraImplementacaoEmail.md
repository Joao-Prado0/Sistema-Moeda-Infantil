# Implementação do Serviço de E-mail — Moeda Estudantil

## 1. Status da Implementação: CONCLUÍDO (Pronto para Uso)

A funcionalidade de envio de e-mails foi completamente implementada no backend com suporte duplo:
* **Ambiente Localhost (Desenvolvimento / Apresentação):** Fallback automático inteligente que gera os e-mails com templates HTML completos, registra no log do console com todos os detalhes e mantém histórico em memória. **Não exige nenhum cadastro ou serviço externo.**
* **Ambiente Real (SMTP):** Envio real de mensagens HTML para caixas de entrada reais (Gmail, Mailtrap, Outlook, etc.) via `JavaMailSender` assim que as credenciais forem fornecidas no `application.properties`.

---

## 2. Arquivos Implementados

* [`EmailService.java`](file:///c:/Users/joaop/OneDrive/Área%20de%20Trabalho/4semestre/LabDevSoft/Sistema-Moeda-Infantil/src/backend/src/main/java/com/example/moeda_estudantil/service/EmailService.java): Contrato de envio de cupons e notificações de mérito.
* [`SmtpEmailService.java`](file:///c:/Users/joaop/OneDrive/Área%20de%20Trabalho/4semestre/LabDevSoft/Sistema-Moeda-Infantil/src/backend/src/main/java/com/example/moeda_estudantil/service/SmtpEmailService.java): Implementação `@Primary` com templates HTML formatados (paleta Índigo `#1D1757` e Dourado `#F2A900`), tratamento de erros e resiliência transacional.
* [`application.properties`](file:///c:/Users/joaop/OneDrive/Área%20de%20Trabalho/4semestre/LabDevSoft/Sistema-Moeda-Infantil/src/backend/src/main/resources/application.properties): Parâmetros pré-formatados para Gmail e Mailtrap.

---

## 3. Disparos Automáticos do Sistema

1. **Quando o Professor Bonifica um Aluno:**
   * O aluno recebe o e-mail: *"Você recebeu X moedas de Prof. Y!"* com a justificativa cadastrada pelo professor e seu saldo atualizado.
2. **Quando o Aluno Resgata uma Vantagem:**
   * **Aluno recebe:** Cupom formatado em caixa de destaque com o código UUID para apresentação na loja.
   * **Empresa parceira recebe:** Aviso de resgate com nome do estudante, vantagem e código do cupom para validação.

---

## 4. Como Ativar o Envio Real para sua Caixa de Entrada (Opcional)

Se desejar testar o recebimento em sua caixa postal real pelo localhost, adicione no `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-de-app-de-16-letras
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> **Como gerar a senha de app no Gmail (1 minuto):**
> Conta Google > Segurança > Verificação em duas etapas > Senhas de app > Criar uma senha com nome "Moeda Estudantil" > Copiar os 16 caracteres.
