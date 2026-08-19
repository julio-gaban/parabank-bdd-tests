# language: en
# Tag global que agrupa todos os cenários relacionados ao fluxo de registro de novos usuários.
@registration
Feature: User Registration on ParaBank
  As a new customer
  I want to create an account on the ParaBank platform
  So that I can access online banking services

  # Contexto (Background): Garantia de pré-requisito executada antes de cada cenário.
  # Redireciona o navegador diretamente para a tela de formulário de cadastro.
  Background:
    Given the user is on the ParaBank register page

  # Caminho Feliz: Cadastro com sucesso utilizando massa de dados válida gerada dinamicamente.
  @registration_success
  Scenario: Successfully register a new user with valid information
    When the user fills in the registration form with valid details
    And clicks the register button
    Then the account should be created successfully
    And the user should see a welcome message with the newly created username

  # Regra de Negócio: Impede a duplicidade de cadastro para um nome de usuário que já existe no banco de dados.
  @registration_duplicate_username
  Scenario: Attempt to register with an already existing username
    When the user attempts to register using an existing username "john"
    And clicks the register button
    Then an error message indicating that the username already exists should be displayed

  # Esquema do Cenário: Valida individualmente a obrigatoriedade de cada campo do formulário.
  @registration_blank_fields
  Scenario Outline: Attempt to register leaving a mandatory field empty
    When the user leaves the mandatory field "<field_name>" empty
    And fills all other registration fields with valid data
    And clicks the register button
    Then an inline error message "<error_message>" should be displayed for the field "<field_name>"

    # Tabela com o mapeamento de todos os campos obrigatórios e suas respectivas mensagens de erro esperadas.
    Examples:
      | field_name       | error_message                      |
      | First Name       | First name is required.            |
      | Last Name        | Last name is required.             |
      | Address          | Address is required.               |
      | City             | City is required.                  |
      | State            | State is required.                 |
      | Zip Code         | Zip Code is required.              |
      | Social Security  | Social Security Number is required.|
      | Username         | Username is required.              |
      | Password         | Password is required.              |
      | Confirm Password | Password confirmation is required. |

  # Validação de Integridade: Impede o envio do formulário se as senhas informadas forem divergentes.
  @registration_mismatched_passwords
  Scenario: Attempt to register with mismatched passwords
    When the user enters password "Password123" and confirm password "Password321"
    And fills all other registration fields with valid data
    And clicks the register button
    Then an error message "Passwords did not match." should be displayed on the register page

  # Validação em Lote: Dispara todas as mensagens de erro de campos obrigatórios ao submeter o formulário limpo.
  @registration_all_fields_empty
  Scenario: Attempt to register with all fields empty
    When the user clicks the register button without filling any field
    Then error messages should be displayed for all mandatory fields

  # Esquema do Cenário: Garante que espaços em branco não sejam aceitos como valores válidos em campos obrigatórios (Trim/Sanitização).
  @registration_whitespace_inputs
  Scenario Outline: Attempt to register using only whitespaces in mandatory fields
    When the user enters only whitespaces in the field "<field_name>"
    And fills all other registration fields with valid data
    And clicks the register button
    Then an inline error message "<error_message>" should be displayed for the field "<field_name>"

    # Amostra de campos para validação de espaços em branco.
    Examples:
      | field_name | error_message           |
      | First Name | First name is required. |
      | Last Name  | Last name is required.  |
      | Username   | Username is required.   |

  # Esquema do Cenário (Segurança/SecOps): Simula injeções de código SQL nos campos do formulário de cadastro.
  # Garante que a aplicação trate as entradas e não sofra crashes (Erro 500) nem vazamento de dados do banco.
  @registration_sql_injection
  Scenario Outline: Attempt to inject SQL during registration
    When the user enters the SQL injection string "<sql_payload>" in the field "<field_name>"
    And fills all other registration fields with valid data
    And clicks the register button
    Then the system should handle the input safely without crashing or leaking sensitive data

    # Payloads clássicos de SQL Injection para testes de vulnerabilidade em formulários.
    Examples:
      | field_name | sql_payload               |
      | Username   | ' OR '1'='1              |
      | Password   | admin' --                 |
      | First Name | '; DROP TABLE customer;-- |