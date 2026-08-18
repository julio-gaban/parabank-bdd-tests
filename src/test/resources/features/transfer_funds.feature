@transfer
Feature: Fund Transfer
  As an authenticated ParaBank user
  I want to transfer funds between my accounts
  So that I can manage my money effectively

  Background:
    Given the user is logged into their ParaBank account
    And the user navigates to the "Transfer Funds" page

  @transfer_success @smoke
  Scenario Outline: Successfully transfer funds with different amounts
    When the user enters an amount of "<amount>"
    And selects source account index <fromIdx> and target account index <toIdx>
    And clicks the transfer button
    Then a transfer success message should be displayed
    And the transferred amount "<amount>" should be confirmed in the summary

    Examples:
      | amount | fromIdx | toIdx |
      | 150.00 | 0       | 1     |
      | 1.00   | 0       | 0     |
      | 999.99 | 1       | 0     |

  @transfer_negative @validation
  Scenario Outline: Attempt to transfer with invalid amount
    When the user enters an amount of "<amount>"
    And selects source account index 0 and target account index 1
    And clicks the transfer button
    Then an amount validation error "<errorMessage>" should be displayed

    Examples:
      | amount | errorMessage                |
      |        | The amount cannot be empty. |
      | abc    | Please enter a valid amount.|