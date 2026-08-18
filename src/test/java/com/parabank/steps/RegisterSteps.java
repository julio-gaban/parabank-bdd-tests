package com.parabank.steps;

import com.microsoft.playwright.Page;
import com.parabank.context.TestContext;
import com.parabank.pages.RegisterPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterSteps {

    private final Page page;
    private final RegisterPage registerPage;

    public RegisterSteps(TestContext testContext) {
        this.page = testContext.getPage();
        this.registerPage = new RegisterPage(page);
    }

    @Given("the user is on the ParaBank register page")
    public void the_user_is_on_the_para_bank_register_page() {
        registerPage.navigateToRegisterPage();
    }

   @When("the user fills in the registration form with valid details")
    public void the_user_fills_in_the_registration_form_with_valid_details() {
        // Gera um username único usando o timestamp em milissegundos
        String uniqueUsername = "user" + System.currentTimeMillis();
        
        registerPage.fillRegistrationForm(
            "Jane", "Doe", "123 Main St", "New York", 
            "NY", "10001", "555-0199", "123-45-678", 
            uniqueUsername, "password123"
        );
    }

    @When("the user attempts to register using an existing username {string}")
    public void the_user_attempts_to_register_using_an_existing_username(String username) {
        registerPage.fillRegistrationForm(
            "John", "Smith", "123 Main St", "New York", 
            "NY", "10001", "555-0199", "123-45-678", 
            username, "password123"
        );
    }

    // PASSO FALTANTE 1: Clique no botão de registro
    @When("clicks the register button")
    public void clicks_the_register_button() {
        registerPage.clickRegister();
    }

    @Then("the account should be created successfully")
    public void the_account_should_be_created_successfully() {
        assertTrue(registerPage.isRegistrationSuccessful(), 
            "Account registration was not successful.");
    }

    @Then("the user should see a welcome message with the newly created username")
    public void the_user_should_see_a_welcome_message_with_the_newly_created_username() {
        assertTrue(registerPage.getWelcomeMessageText().contains("Welcome"), 
            "Welcome message was not displayed.");
    }

    // PASSO FALTANTE 2: Validação da mensagem de erro de usuário existente
    @Then("an error message indicating that the username already exists should be displayed")
    public void an_error_message_indicating_that_the_username_already_exists_should_be_displayed() {
        String errorMessage = registerPage.getUsernameErrorMessageText().trim();
        assertEquals("This username already exists.", errorMessage,
            "The error message for existing username was not as expected.");
    }
}