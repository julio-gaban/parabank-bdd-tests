# language: en
# Tag de agrupamento para a funcionalidade de Transferência de Fundos.
@transfer
Feature: Transfer Funds
  As a logged-in ParaBank customer
  I want to transfer funds between my accounts
  So that I can manage my money effectively

  # Contexto (Background): Define os pré-requisitos essenciais antes de cada teste.
  # Realiza o login na plataforma e navega até a tela de transferência.
  Background:
    Given the user is logged into their ParaBank account
    And the user navigates to the "Transfer Funds" page

  # Caminho Feliz: Sucesso ao realizar uma transferência entre contas informando um valor válido.
  @transfer_positive
  Scenario: Successfully transfer funds between accounts
    When the user enters an amount of "100.00"
    And selects source account index 0 and target account index 1
    And clicks the transfer button
    Then a transfer success message should be displayed
    And the transferred amount "$100.00" should be confirmed in the summary

  # Esquema do Cenário: Validações negativas de valor (entradas inválidas como texto, valores vazios ou caracteres especiais).
  @transfer_negative @validation
  Scenario Outline: Attempt to transfer with invalid amount
    When the user enters an amount of "<amount>"
    And selects source account index 0 and target account index 1
    And clicks the transfer button
    Then an amount validation error "<error_message>" should be displayed

    # Tabela com as entradas inválidas de teste e suas respectivas mensagens de erro esperadas pela aplicação.
    Examples:
      | amount | error_message                             |
      |        | Please enter a valid amount.              |
      | abc    | Please enter a valid amount.              |
      | !@#$   | Please enter a valid amount.              |