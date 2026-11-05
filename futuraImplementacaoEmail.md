Ran search email files in backend
Ran list email files path
Analyzed EmailService.java:L1-42
Analyzed LogEmailService.java:L1-43
Ran check mail dependency in pom.xml

No projeto, a arquitetura do serviço de e-mail está estruturada da seguinte forma:

### 1. Dependência Base do Ecossistema
* **Spring Boot Starter Mail (`spring-boot-starter-mail`)**: Adicionada no [`pom.xml`](file:///c:/Users/joaop/OneDrive/Área%20de%20Trabalho/4semestre/LabDevSoft/Sistema-Moeda-Infantil/src/backend/pom.xml), trazendo a biblioteca padrão **JavaMail / Jakarta Mail** para envio via protocolo SMTP.

---

### 2. Padrão Arquitetural Adotado
Foi utilizado o padrão de **Inversão de Dependência (IoC/DIP)** com uma interface:
* [`EmailService`](file:///c:/Users/joaop/OneDrive/Área%20de%20Trabalho/4semestre/LabDevSoft/Sistema-Moeda-Infantil/src/backend/src/main/java/com/example/moeda_estudantil/service/EmailService.java): Define o contrato de negócio com os métodos:
  * `enviarCupom(...)`: Notifica o aluno e a empresa parceira com o código do cupom gerado.
  * `enviarNotificacaoRecebimento(...)`: Notifica o aluno quando um professor lhe transfere moedas, exibindo a quantidade e a justificativa.

---

### 3. Implementação Atual em Desenvolvimento
* **[`LogEmailService`](file:///c:/Users/joaop/OneDrive/Área%20de%20Trabalho/4semestre/LabDevSoft/Sistema-Moeda-Infantil/src/backend/src/main/java/com/example/moeda_estudantil/service/LogEmailService.java)** (Mock com **SLF4J / Logback**):
  * Para o ambiente acadêmico e de testes locais, o serviço simula o envio e imprime no log do console a mensagem formatada contendo remetente, destinatário, assunto, código do cupom e motivo.
  * **Vantagem:** Permite rodar e testar todo o fluxo de resgate e envio de moedas sem exigir credenciais SMTP reais (Gmail, SendGrid ou Mailgun) ou conexões de rede externas sujeitas a bloqueio.

---

### 4. Transição para Envio Real em Produção
Para ativar o envio real via SMTP (como Gmail, Outlook ou Mailgun), basta:
1. Inserir os parâmetros no `application.properties`:
   ```properties
   spring.mail.host=smtp.exemplo.com
   spring.mail.port=587
   spring.mail.username=seu-email@exemplo.com
   spring.mail.password=sua-senha-de-app
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```
2. Injetar o `JavaMailSender` em uma classe `SmtpEmailService` que implementa `EmailService`.