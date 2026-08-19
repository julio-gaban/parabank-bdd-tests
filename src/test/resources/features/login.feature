# language: en
# Funcionalidade principal de Autenticação do sistema ParaBank.
# Contém a especificação dos comportamentos esperados tanto para caminhos felizes (sucesso) 
# quanto para validações de segurança, tratamento de erros e regra de negócio de Logout.

Feature: User Login Functionality
  As a registered user of ParaBank
  I want to log in using my credentials
  So that I can access my account dashboard

  # Contexto (Background): Executado automaticamente antes de cada um dos cenários abaixo.
  # Garante que o navegador sempre iniciará o teste a partir da página inicial do banco.
  Background:
    Given the user is on the ParaBank home page

  # Caminho Feliz: Valida o acesso concedido ao informar usuário e senha válidos.
  Scenario: Successful login with valid credentials
    When the user enters username "john" and password "demo"
    And clicks the login button
    Then the user should see the account overview page
    And a welcome message "Welcome John Smith" should be displayed

  # Exceção BÁSICA: Valida a recusa de acesso para credenciais inexistentes ou incorretas.
  Scenario: Unsuccessful login with invalid credentials
    When the user enters username "invalidUser" and password "invalidPass"
    And clicks the login button
    Then an error message "The username and password could not be verified." should be displayed on the login page

  # Esquema do Cenário: Testa o comportamento do formulário ao submeter campos obrigatórios em branco.
  Scenario Outline: Unsuccessful login with empty credentials
    When the user enters username "<username>" and password "<password>"
    And clicks the login button
    Then an error message "Please enter a username and password." should be displayed on the login page

    # Exemplos/Massa de testes para combinações de campos vazios (apenas usuário, apenas senha, ambos).
    Examples:
      | username | password |
      |          | demo     |
      | john     |          |
      |          |          |

  # Esquema do Cenário: Garante que o sistema diferencie maiúsculas de minúsculas (Case Sensitivity).
  Scenario Outline: Unsuccessful login with case sensitivity variation
    When the user enters username "<username>" and password "<password>"
    And clicks the login button
    Then an error message "The username and password could not be verified." should be displayed on the login page

    # Exemplos/Massa de testes alternando letras maiúsculas em usuário e senha.
    Examples:
      | username | password |
      | JOHN     | demo     |
      | john     | DEMO     |
      | John     | Demo     |

  # Esquema do Cenário: Testa se o formulário rejeita ou trata adequadamente espaços em branco no início ou fim das entradas.
  Scenario Outline: Unsuccessful login with leading or trailing whitespaces
    When the user enters username "<username>" and password "<password>"
    And clicks the login button
    Then an error message "The username and password could not be verified." should be displayed on the login page

    # Exemplos/Massa de testes com caracteres de espaço antes/depois das credenciais válidas.
    Examples:
      | username | password |
      |  john    | demo     |
      | john     | demo     |

  # Esquema do Cenário (Segurança/SecOps): Simula tentativas de ataques de SQL Injection e XSS.
  # Valida se a aplicação bloqueia a autenticação e não expõe falhas internas/estruturais.
  Scenario Outline: Unsuccessful login with special characters and SQL injection attempts
    When the user enters username "<username>" and password "<password>"
    And clicks the login button
    Then an error message "The username and password could not be verified." should be displayed on the login page

    # Exemplos/Massa de testes com caracteres especiais de comandos SQL e tags HTML/JS.
    Examples:
      | username       | password |
      | ' OR '1'='1    | demo     |
      | john           | ' OR '1  |
      | <script>       | demo     |

  # Validação de Ciclo de Vida do Acesso: Confirma se o encerramento da sessão (Logout) 
  # revoga a autenticação e redireciona a interface de volta à tela inicial com o formulário limpo.
  Scenario: Verify user logout clears session
    When the user enters username "john" and password "demo"
    And clicks the login button
    And the user clicks the log out link
    Then the user should be redirected to the ParaBank home page
    And the login form should be displayed