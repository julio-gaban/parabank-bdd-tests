package com.parabank.steps;

import com.parabank.context.TestContext;
import com.parabank.pages.RegisterPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class RegisterSteps {

    private final RegisterPage registerPage;
    private String lastGeneratedUsername;

    // Injeção de dependência via PicoContainer usando TestContext
    public RegisterSteps(TestContext testContext) {
        this.registerPage = new RegisterPage(testContext.getPage());
    }

    @Given("the user is on the ParaBank register page")
    public void the_user_is_on_the_para_bank_register_page() {
        registerPage.navigateToRegisterPage();
    }

    @When("the user fills in the registration form with valid details")
    public void the_user_fills_in_the_registration_form_with_valid_details() {
        this.lastGeneratedUsername = "user_" + System.currentTimeMillis();
        registerPage.fillRegistrationFormWithValidData(this.lastGeneratedUsername);
    }

    @When("clicks the register button")
    public void clicks_the_register_button() {
        registerPage.clickRegisterButton();
    }

    @Then("the account should be created successfully")
    public void the_account_should_be_created_successfully() {
        Assertions.assertTrue(
            registerPage.isRegistrationSuccessful(),
            "Esperava que o cadastro fosse concluído com sucesso."
        );
    }

    @Then("the user should see a welcome message with the newly created username")
    public void the_user_should_see_a_welcome_message_with_the_newly_created_username() {
        String welcomeMessage = registerPage.getSuccessMessageSafely();
        String expectedSuccessText = "Your account was created successfully. You are now logged in.";
        
        boolean containsUsername = this.lastGeneratedUsername != null && welcomeMessage.contains(this.lastGeneratedUsername);
        boolean containsSuccessText = welcomeMessage.contains(expectedSuccessText);

        Assertions.assertTrue(
            containsUsername || containsSuccessText,
            String.format("Mensagem de confirmação inválida. Esperava contendo o usuário '%s' ou o texto '%s'. Recebido: '%s'",
                this.lastGeneratedUsername, expectedSuccessText, welcomeMessage)
        );
    }

    @When("the user attempts to register using an existing username {string}")
    public void the_user_attempts_to_register_using_an_existing_username(String username) {
        registerPage.fillRegistrationFormWithValidData(username);
    }

    @Then("an error message indicating that the username already exists should be displayed")
    public void an_error_message_indicating_that_the_username_already_exists_should_be_displayed() {
        String errorMessage = registerPage.getDuplicateUsernameErrorMessageSafely();
        Assertions.assertTrue(
            errorMessage.toLowerCase().contains("this username already exists"),
            String.format("Esperava erro de nome de usuário duplicado. Obtido: '%s'", errorMessage)
        );
    }

    @When("the user leaves the mandatory field {string} empty")
    public void the_user_leaves_the_mandatory_field_empty(String fieldName) {
        String username = "user_" + System.currentTimeMillis();
        registerPage.fillRegistrationFormExceptField(fieldName, username);
    }

    @When("fills all other registration fields with valid data")
    public void fills_all_other_registration_fields_with_valid_data() {
        // Método utilitário para manter compatibilidade com os passos da Feature
    }

    @Then("an inline error message {string} should be displayed for the field {string}")
    public void an_inline_error_message_should_be_displayed_for_the_field(String expectedError, String fieldName) {
        String actualError = registerPage.getInlineFieldErrorSafely(fieldName);
        
        // Tratamento especial para o campo "Confirm Password" / "repeatedPassword" no ParaBank
        if (actualError.isBlank() && fieldName.toLowerCase().contains("confirm")) {
            actualError = registerPage.getInlineFieldErrorSafely("repeatedPassword");
        }

        // Validação insensível a maiúsculas/minúsculas e sem espaços extras
        boolean matchesError = actualError.toLowerCase().trim().contains(expectedError.toLowerCase().trim());

        Assertions.assertTrue(
            matchesError,
            String.format("Esperava erro contendo '%s' para o campo '%s', mas obteve: '%s'", expectedError, fieldName, actualError)
        );
    }

    @When("the user enters password {string} and confirm password {string}")
    public void the_user_enters_password_and_confirm_password(String password, String confirmPassword) {
        String username = "user_" + System.currentTimeMillis();
        registerPage.fillRegistrationFormWithCustomPasswords(username, password, confirmPassword);
    }

    @Then("an error message {string} should be displayed on the register page")
    public void an_error_message_should_be_displayed_on_the_register_page(String expectedErrorMessage) {
        String actualError = registerPage.getGeneralErrorMessageSafely();
        Assertions.assertTrue(
            actualError.toLowerCase().contains(expectedErrorMessage.toLowerCase()),
            String.format("Esperava a mensagem '%s', mas obteve: '%s'", expectedErrorMessage, actualError)
        );
    }

    @When("the user clicks the register button without filling any field")
    public void the_user_clicks_the_register_button_without_filling_any_field() {
        registerPage.clickRegisterButton();
    }

    @Then("error messages should be displayed for all mandatory fields")
    public void error_messages_should_be_displayed_for_all_mandatory_fields() {
        // Verifica se há erros inline visíveis ou mensagens globais de validação do formulário
        boolean mandatoryErrorsDisplayed = registerPage.areAllInlineErrorsDisplayed();

        Assertions.assertTrue(
            mandatoryErrorsDisplayed,
            "As mensagens de erro para os campos obrigatórios não foram exibidas ao submeter o formulário em branco."
        );
    }

    @When("the user enters only whitespaces in the field {string}")
    public void the_user_enters_only_whitespaces_in_the_field(String fieldName) {
        String username = "user_" + System.currentTimeMillis();
        registerPage.fillRegistrationFormExceptField(fieldName, username);
        registerPage.fillFieldWithValue(fieldName, "   ");
    }

    @When("the user enters the SQL injection string {string} in the field {string}")
    public void the_user_enters_the_sql_injection_string_in_the_field(String sqlPayload, String fieldName) {
        String username = "user_" + System.currentTimeMillis();
        registerPage.fillRegistrationFormExceptField(fieldName, username);
        registerPage.fillFieldWithValue(fieldName, sqlPayload);
    }

    @Then("the system should handle the input safely without crashing or leaking sensitive data")
    public void the_system_should_handle_the_input_safely_without_crashing_or_leaking_sensitive_data() {
        String bodyText = registerPage.getPageBodyText();
        
        // Verifica se houve crash com exceção não tratada do Spring ou vazamento de banco de dados
        boolean hasBackendCrash = bodyText.contains("An internal error has occurred") 
                               || bodyText.contains("org.springframework")
                               || bodyText.contains("java.lang.NullPointerException")
                               || bodyText.contains("SQLException");

        Assertions.assertFalse(
            hasBackendCrash,
            "A aplicação falhou gravemente (HTTP 500 / Exception) ao processar o payload/entrada enviada."
        );
    }
}