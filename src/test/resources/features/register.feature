@registration
Feature: User Registration on ParaBank
  As a new customer
  I want to create an account on the ParaBank platform
  So that I can access online banking services

  Background:
    Given the user is on the ParaBank register page

  @registration_success
  Scenario: Successfully register a new user with valid information
    When the user fills in the registration form with valid details
    And clicks the register button
    Then the account should be created successfully
    And the user should see a welcome message with the newly created username

  @registration_duplicate_username
  Scenario: Attempt to register with an already existing username
    When the user attempts to register using an existing username "john"
    And clicks the register button
    Then an error message indicating that the username already exists should be displayed