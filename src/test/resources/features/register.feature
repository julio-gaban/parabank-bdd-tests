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

  @registration_blank_fields
  Scenario Outline: Attempt to register leaving a mandatory field empty
    When the user leaves the mandatory field "<field_name>" empty
    And fills all other registration fields with valid data
    And clicks the register button
    Then an inline error message "<error_message>" should be displayed for the field "<field_name>"

    Examples:
      | field_name       | error_message               |
      | First Name       | First name is required.     |
      | Last Name        | Last name is required.      |
      | Address          | Address is required.        |
      | City             | City is required.           |
      | State            | State is required.          |
      | Zip Code         | Zip code is required.       |
      | Social Security  | SSN is required.            |
      | Username         | Username is required.       |
      | Password         | Password is required.       |
      | Confirm Password | Password confirmation is required. |

  @registration_mismatched_passwords
  Scenario: Attempt to register with mismatched passwords
    When the user enters password "Password123" and confirm password "Password321"
    And fills all other registration fields with valid data
    And clicks the register button
    Then an error message "Passwords did not match." should be displayed

  @registration_all_fields_empty
  Scenario: Attempt to register with all fields empty
    When the user clicks the register button without filling any field
    Then error messages should be displayed for all mandatory fields

  @registration_whitespace_inputs
  Scenario Outline: Attempt to register using only whitespaces in mandatory fields
    When the user enters only whitespaces in the field "<field_name>"
    And fills all other registration fields with valid data
    And clicks the register button
    Then an inline error message "<error_message>" should be displayed for the field "<field_name>"

    Examples:
      | field_name | error_message           |
      | First Name | First name is required. |
      | Last Name  | Last name is required.  |
      | Username   | Username is required.   |

  @registration_sql_injection
  Scenario Outline: Attempt to inject SQL during registration
    When the user enters the SQL injection string "<sql_payload>" in the field "<field_name>"
    And fills all other registration fields with valid data
    And clicks the register button
    Then the system should handle the input safely without crashing or leaking sensitive data

    Examples:
      | field_name | sql_payload            |
      | Username   | ' OR '1'='1           |
      | Password   | admin' --              |
      | First Name | '; DROP TABLE customer;-- |