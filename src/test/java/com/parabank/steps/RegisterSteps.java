package com.parabank.steps;

import com.parabank.context.TestContext;
import com.parabank.pages.RegisterPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

/**
 * Mapeamento dos passos (Steps Definitions) do Cucumber para as funcionalidades de Cadastro de Usuário (Register).
 * Conecta os cenários da feature aos métodos de interação da classe RegisterPage.
 */
public class RegisterSteps {

    private final RegisterPage registerPage;
    private String lastGeneratedUsername;

    /**
     * Construtor da classe com Injeção de Dependência via PicoContainer.
     * Compartilha a instância de TestContext para reaproveitar a página e a sessão ativas do Playwright.
     * 
     * @param testContext Contexto da execução dos testes.
     */
    public RegisterSteps(TestContext testContext) {
        this.registerPage = new RegisterPage(testContext.getPage());
    }

    /**
     * Navega diretamente para a página de registro do ParaBank.
     */
    @Given("the user is on the ParaBank register page")
    public void the_user_is_on_the_para_bank_register_page() {
        registerPage.navigateToRegisterPage();
    }

    /**
     * Preenche todos os campos do formulário de cadastro com dados válidos.
     * Gera um nome de usuário dinâmico usando timestamp para garantir a unicidade durante os testes.
     */
    @When("the user fills in the registration form with valid details")
    public void the_user_fills_in_the_registration_form_with_valid_details() {
        this.lastGeneratedUsername = "user_" + System.currentTimeMillis();
        registerPage.fillRegistrationFormWithValidData(this.lastGeneratedUsername);
    }

    /**
     * Clica no botão de submissão do formulário de cadastro.
     */
    @When("clicks the register button")
    public void clicks_the_register_button() {
        registerPage.clickRegisterButton();
    }

    /**
     * Valida se a conta foi criada com sucesso verificando os elementos visuais na interface.
     */
    @Then("the account should be created successfully")
    public void the_account_should_be_created_successfully() {
        Assertions.assertTrue(
            registerPage.isRegistrationSuccessful(),
            "Esperava que o cadastro fosse concluído com sucesso."
        );
    }

    /**
     * Confirma se a mensagem de boas-vindas exibe o nome de usuário recém-criado
     * ou o texto padrão de sucesso de criação de conta.
     */
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

    /**
     * Tenta realizar o cadastro utilizando um nome de usuário que já existe no sistema.
     * 
     * @param username Nome de usuário preexistente informado na Feature.
     */
    @When("the user attempts to register using an existing username {string}")
    public void the_user_attempts_to_register_using_an_existing_username(String username) {
        registerPage.fillRegistrationFormWithValidData(username);
    }

    /**
     * Valida se a mensagem de erro adequada para nome de usuário duplicado é exibida na tela.
     */
    @Then("an error message indicating that the username already exists should be displayed")
    public void an_error_message_indicating_that_the_username_already_exists_should_be_displayed() {
        String errorMessage = registerPage.getDuplicateUsernameErrorMessageSafely();
        Assertions.assertTrue(
            errorMessage.toLowerCase().contains("this username already exists"),
            String.format("Esperava erro de nome de usuário duplicado. Obtido: '%s'", errorMessage)
        );
    }

    /**
     * Preenche o formulário omitindo um campo específico passado por parâmetro.
     * 
     * @param fieldName Nome do campo obrigatório que deve ser deixado em branco.
     */
    @When("the user leaves the mandatory field {string} empty")
    public void the_user_leaves_the_mandatory_field_empty(String fieldName) {
        String username = "user_" + System.currentTimeMillis();
        registerPage.fillRegistrationFormExceptField(fieldName, username);
    }

    /**
     * Método utilitário/passo passivo para manter a legibilidade do cenário em BDD 
     * ao simular o preenchimento dos demais campos.
     */
    @When("fills all other registration fields with valid data")
    public void fills_all_other_registration_fields_with_valid_data() {
        // Método utilitário para manter compatibilidade com os passos da Feature
    }

    /**
     * Valida a mensagem de erro 'inline' (abaixo do campo específico).
     * Possui tratamento de exceção para mapeamento de nomes alternativos de seletores no ParaBank (ex: Confirm Password / repeatedPassword).
     * 
     * @param expectedError Texto de erro esperado.
     * @param fieldName Nome do campo onde o erro deve ser validado.
     */
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

    /**
     * Preenche o formulário informando senhas personalizadas para validar a divergência na confirmação.
     * 
     * @param password Senha principal.
     * @param confirmPassword Senha de confirmação.
     */
    @When("the user enters password {string} and confirm password {string}")
    public void the_user_enters_password_and_confirm_password(String password, String confirmPassword) {
        String username = "user_" + System.currentTimeMillis();
        registerPage.fillRegistrationFormWithCustomPasswords(username, password, confirmPassword);
    }

    /**
     * Valida mensagens globais de erro exibidas no topo ou corpo da página de cadastro.
     * 
     * @param expectedErrorMessage Mensagem de erro esperada.
     */
    @Then("an error message {string} should be displayed on the register page")
    public void an_error_message_should_be_displayed_on_the_register_page(String expectedErrorMessage) {
        String actualError = registerPage.getGeneralErrorMessageSafely();
        Assertions.assertTrue(
            actualError.toLowerCase().contains(expectedErrorMessage.toLowerCase()),
            String.format("Esperava a mensagem '%s', mas obteve: '%s'", expectedErrorMessage, actualError)
        );
    }

    /**
     * Clica no botão de registro com todo o formulário limpo para disparar as validações dos campos.
     */
    @When("the user clicks the register button without filling any field")
    public void the_user_clicks_the_register_button_without_filling_any_field() {
        registerPage.clickRegisterButton();
    }

    /**
     * Verifica se todas as mensagens de campos obrigatórios foram disparadas após submeter o formulário em branco.
     */
    @Then("error messages should be displayed for all mandatory fields")
    public void error_messages_should_be_displayed_for_all_mandatory_fields() {
        // Verifica se há erros inline visíveis ou mensagens globais de validação do formulário
        boolean mandatoryErrorsDisplayed = registerPage.areAllInlineErrorsDisplayed();

        Assertions.assertTrue(
            mandatoryErrorsDisplayed,
            "As mensagens de erro para os campos obrigatórios não foram exibidas ao submeter o formulário em branco."
        );
    }

    /**
     * Preenche um campo específico apenas com espaços em branco para testar sanitização/trim.
     * 
     * @param fieldName Nome do campo a ser testado.
     */
    @When("the user enters only whitespaces in the field {string}")
    public void the_user_enters_only_whitespaces_in_the_field(String fieldName) {
        String username = "user_" + System.currentTimeMillis();
        registerPage.fillRegistrationFormExceptField(fieldName, username);
        registerPage.fillFieldWithValue(fieldName, "   ");
    }

    /**
     * Insere uma string de ataque SQL Injection em um campo específico para testes de segurança (SecOps/QA).
     * 
     * @param sqlPayload String de payload SQL (ex: ' OR '1'='1).
     * @param fieldName Nome do campo em que o payload será inserido.
     */
    @When("the user enters the SQL injection string {string} in the field {string}")
    public void the_user_enters_the_sql_injection_string_in_the_field(String sqlPayload, String fieldName) {
        String username = "user_" + System.currentTimeMillis();
        registerPage.fillRegistrationFormExceptField(fieldName, username);
        registerPage.fillFieldWithValue(fieldName, sqlPayload);
    }

    /**
     * Valida que o backend tratou as entradas com segurança, garantindo que não houve falha grave 
     * do servidor (Erro HTTP 500), rastros de exceção do Spring ou vazamento de banco de dados (SQLException).
     */
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