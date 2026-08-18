@transfer
Feature: Fund Transfer
  As an authenticated ParaBank user
  I want to transfer funds between my accounts
  So that I can manage my money effectively

  Background:
    Given the user is logged into their ParaBank account
    And the user navigates to the "Transfer Funds" page

  @transfer_success
  Scenario: Successfully transfer funds between two valid accounts
    When the user enters an amount of "150.00"
    And selects the source account and target account
    And clicks the transfer button
    Then a transfer success message should be displayed
    And the transferred amount "150.00" should be confirmed in the summary