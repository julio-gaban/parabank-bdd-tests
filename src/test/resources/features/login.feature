Feature: User Login Functionality
  As a registered user of ParaBank
  I want to log in using my credentials
  So that I can access my account dashboard

  Background:
    Given the user is on the ParaBank home page

  Scenario: Successful login with valid credentials
    When the user enters username "john" and password "demo"
    And clicks the login button
    Then the user should see the account overview page
    And a welcome message "Welcome John Smith" should be displayed

  Scenario: Unsuccessful login with invalid credentials
    When the user enters username "invalidUser" and password "invalidPass"
    And clicks the login button
    Then an error message "The username and password could not be verified." should be displayed

  Scenario Outline: Unsuccessful login with empty credentials
    When the user enters username "<username>" and password "<password>"
    And clicks the login button
    Then an error message "Please enter a username and password." should be displayed

    Examples:
      | username | password |
      |          | demo     |
      | john     |          |
      |          |          |

  Scenario Outline: Unsuccessful login with case sensitivity variation
    When the user enters username "<username>" and password "<password>"
    And clicks the login button
    Then an error message "The username and password could not be verified." should be displayed

    Examples:
      | username | password |
      | JOHN     | demo     |
      | john     | DEMO     |
      | John     | Demo     |

  Scenario Outline: Unsuccessful login with leading or trailing whitespaces
    When the user enters username "<username>" and password "<password>"
    And clicks the login button
    Then an error message "The username and password could not be verified." should be displayed

    Examples:
      | username | password |
      |  john    | demo     |
      | john     | demo     |

  Scenario Outline: Unsuccessful login with special characters and SQL injection attempts
    When the user enters username "<username>" and password "<password>"
    And clicks the login button
    Then an error message "The username and password could not be verified." should be displayed

    Examples:
      | username       | password |
      | ' OR '1'='1    | demo     |
      | john           | ' OR '1  |
      | <script>       | demo     |

  Scenario: Verify user logout clears session
    When the user enters username "john" and password "demo"
    And clicks the login button
    And the user clicks the log out link
    Then the user should be redirected to the ParaBank home page
    And the login form should be displayed