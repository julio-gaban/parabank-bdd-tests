# 🧪 ParaBank Automated Test Suite (Playwright + Cucumber BDD)

Este projeto consiste em uma suíte de testes automatizados End-to-End (E2E) para a aplicação web **[ParaBank](https://parabank.parasoft.com/)**, desenvolvida utilizando **Playwright Java** e **Cucumber (BDD)** com suporte a **JUnit 5** e **Maven**.

A arquitetura foi projetada com o padrão **Page Object Model (POM)** e gerenciamento de estado via **PicoContainer**, garantindo isolamento entre cenários e facilidade de manutenção.

---

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 17+
* **Automação Web:** [Playwright Java](https://playwright.dev/java/)
* **Framework BDD:** [Cucumber Java](https://cucumber.io/)
* **Injeção de Dependências:** Cucumber PicoContainer
* **Runner & Asserções:** JUnit 5 (JUnit Platform Suite)
* **Gerenciador de Dependências:** Apache Maven

---

## 📁 Estrutura do Projeto

```text
parabank-bdd-tests/
├── src/
│   ├── main/
│   │   └── java/                 # Classes utilitárias do sistema (se houver)
│   └── test/
│       ├── java/
│       │   └── com/parabank/
│       │       ├── context/      # Gerenciamento de contexto do Playwright (TestContext)
│       │       ├── pages/        # Page Objects (LoginPage, RegisterPage, TransferPage, etc.)
│       │       ├── runners/      # Executores de testes JUnit 5 (TestRunner)
│       │       └── steps/        # Step Definitions do Cucumber
│       └── resources/
│           ├── features/         # Arquivos de especificação Gherkin (.feature)
│           └── junit-platform.properties # Configurações da JUnit Platform
├── target/                       # Artefatos do build e relatórios gerados
├── pom.xml                       # Dependências e plugins do Maven
└── README.md
```

##⚙️ Pré-requisitos
Antes de iniciar, certifique-se de ter instalado em sua máquina:

Java JDK 17 ou superior instalado e configurado no PATH.

Apache Maven 3.8+ instalado.

Git para clonar o repositório.

## 🛠️ Configuração e Instalação

### Clone o repositório:
```bash
git clone https://github.com/julio-gaban/parabank-bdd-tests.git
cd parabank-bdd-tests
```
### Instale as dependências e navegares do Playwright:
```bash
mvn clean test-compile
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```
## 🧪 Executando os Testes

### Execução Completa via Terminal
Para executar todos os cenários de teste da suíte via Maven:
```bash
mvn clean test
```

### Execução via IDE (VS Code / IntelliJ / Eclipse)

Você pode rodar a suíte diretamente pela classe executora:

Navegue até src/test/java/com/parabank/runners/TestRunner.java.

Clique com o botão direito na classe e selecione Run 'TestRunner'.

## 📊 Relatórios de Teste (Cucumber Reports)

A suíte está configurada para gerar relatórios visuais automaticamente a cada execução na pasta target/cucumber-reports/.

### Arquivos Gerados:
Relatório HTML Interativo: target/cucumber-reports/cucumber.html

Relatório JSON: target/cucumber-reports/cucumber.json

### Como Visualizar:
Após executar os testes, navegue até a pasta target/cucumber-reports/.

Abra o arquivo cucumber.html diretamente no seu navegador de preferência.

## 📋 Funcionalidades Cobertas
Autenticação (Login): Validação de credenciais válidas e inválidas.

Cadastro (Register): Registro de novos usuários com dados dinâmicos e validações de nome de usuário existente.

Transferência de Fundos (Transfer Funds): Movimentação financeira entre contas do usuário.

## 🎯 Boas Práticas Implementadas

Geração Dinâmica de Dados: Criação de usernames únicos por execução para evitar conflitos de massa de dados.

Resolução de Strict Mode Violations: Seletores refinados e combinados com hierarquia CSS clara.

Injeção de Dependência: Uso do PicoContainer para reutilização segura de instâncias do Page e componentes entre passos do Cucumber.