package com.parabank.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Implementação do padrão Page Object Model (POM) para a página de Cadastro (Register) do ParaBank.
 * Encapsula os seletores dos campos de formulário, mensagens de erro inline e ações de registro.
 */
public class RegisterPage {

    // Instância da Page do Playwright para interações com a página
    private final Page page;

    // --- Mapeamento dos Seletores CSS dos Campos de Formulário ---
    private final String firstNameInput = "input[id='customer.firstName']";
    private final String lastNameInput = "input[id='customer.lastName']";
    private final String streetInput = "input[id='customer.address.street']";
    private final String cityInput = "input[id='customer.address.city']";
    private final String stateInput = "input[id='customer.address.state']";
    private final String zipCodeInput = "input[id='customer.address.zipCode']";
    private final String phoneNumberInput = "input[id='customer.phoneNumber']";
    private final String ssnInput = "input[id='customer.ssn']";
    private final String usernameInput = "input[id='customer.username']";
    private final String passwordInput = "input[id='customer.password']";
    private final String confirmPasswordInput = "input[id='repeatedPassword']";
    private final String registerButton = "input[value='Register']";

    // --- Mapeamento dos Seletores de Sucesso e Erros ---
    private final String successTitle = "#rightPanel h1.title";
    private final String successMessage = "#rightPanel p";
    private final String generalErrorSelector = ".error, span.errors, span[id$='.errors']";

    /**
     * Construtor da classe RegisterPage.
     * Valida a presença da instância da Page para evitar erros de NullPointerException.
     * 
     * @param page Instância ativa da Page do Playwright.
     */
    public RegisterPage(Page page) {
        if (page == null) {
            throw new IllegalArgumentException("A instância de Page do Playwright não pode ser nula.");
        }
        this.page = page;
    }

    /**
     * Navega diretamente para a URL do formulário de cadastro de usuário.
     */
    public void navigateToRegisterPage() {
        page.navigate("https://parabank.parasoft.com/parabank/register.htm");
    }

    /**
     * Preenche dinamicamente um campo do formulário com base no nome amigável do campo.
     * 
     * @param fieldName Nome amigável do campo (ex: "First Name", "Username").
     * @param value Texto a ser digitado no campo.
     */
    public void fillFieldWithValue(String fieldName, String value) {
        String selector = getSelectorByFieldName(fieldName);
        page.fill(selector, value);
    }

    /**
     * Preenche todos os campos do formulário de registro com dados padrão válidos.
     * 
     * @param username Nome de usuário dinâmico para evitar conflito de cadastro duplicado.
     */
    public void fillRegistrationFormWithValidData(String username) {
        page.fill(firstNameInput, "John");
        page.fill(lastNameInput, "Doe");
        page.fill(streetInput, "123 Main St");
        page.fill(cityInput, "Springfield");
        page.fill(stateInput, "IL");
        page.fill(zipCodeInput, "62701");
        page.fill(phoneNumberInput, "555-1234");
        page.fill(ssnInput, "123-45-6789");
        page.fill(usernameInput, username);
        page.fill(passwordInput, "Password123");
        page.fill(confirmPasswordInput, "Password123");
    }

    /**
     * Preenche o formulário com dados válidos, mas deixa um campo específico em branco.
     * Útil para testes de validação de campos obrigatórios.
     * 
     * @param fieldToSkip Nome amigável do campo que deve ser limpo.
     * @param username Nome de usuário para os demais dados.
     */
    public void fillRegistrationFormExceptField(String fieldToSkip, String username) {
        fillRegistrationFormWithValidData(username);
        String selector = getSelectorByFieldName(fieldToSkip);
        page.fill(selector, "");
    }

    /**
     * Preenche o formulário permitindo definir senhas personalizadas (ex: senhas divergentes).
     * 
     * @param username Nome de usuário.
     * @param password Senha principal.
     * @param confirmPassword Confirmação da senha.
     */
    public void fillRegistrationFormWithCustomPasswords(String username, String password, String confirmPassword) {
        fillRegistrationFormWithValidData(username);
        page.fill(passwordInput, password);
        page.fill(confirmPasswordInput, confirmPassword);
    }

    /**
     * Efetua o clique no botão de submissão do formulário de registro.
     */
    public void clickRegisterButton() {
        page.click(registerButton);
    }

    /**
     * Confirma se o cadastro foi realizado com sucesso buscando o título de boas-vindas.
     * 
     * @return true se o título com "Welcome" for exibido em até 3 segundos; false caso contrário.
     */
    public boolean isRegistrationSuccessful() {
        try {
            page.waitForSelector(successTitle, new Page.WaitForSelectorOptions().setTimeout(3000));
            return page.innerText(successTitle).contains("Welcome");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Captura a mensagem de confirmação do cadastro de forma segura.
     * 
     * @return O texto da mensagem de sucesso ou uma string vazia em caso de falha.
     */
    public String getSuccessMessageSafely() {
        try {
            return page.innerText(successMessage);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Captura a mensagem de erro específica para tentativas de cadastro com usuário duplicado.
     * 
     * @return Texto do erro no elemento do usuário ou o texto bruto do painel principal (fallback).
     */
    public String getDuplicateUsernameErrorMessageSafely() {
        try {
            return page.innerText("span[id='customer.username.errors']");
        } catch (Exception e) {
            return page.innerText("#rightPanel");
        }
    }

    /**
     * Captura o texto da mensagem de erro exibida logo abaixo (inline) de um campo específico.
     * 
     * @param fieldName Nome amigável do campo com erro.
     * @return O texto do elemento span de erro ou string vazia se não encontrado.
     */
    public String getInlineFieldErrorSafely(String fieldName) {
        String selector = getErrorSpanIdByFieldName(fieldName);
        try {
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(3000));
            return page.innerText(selector);
        } catch (Exception e) {
            // Fallback: busca por qualquer span de erro geral presente na tela
            try {
                return page.innerText("span.errors");
            } catch (Exception ex) {
                return "";
            }
        }
    }

    /**
     * Captura qualquer mensagem de erro genérica exibida no painel.
     * 
     * @return O texto do erro localizado ou todo o texto do body como fallback final.
     */
    public String getGeneralErrorMessageSafely() {
        try {
            page.waitForSelector(generalErrorSelector, new Page.WaitForSelectorOptions().setTimeout(3000));
            return page.innerText(generalErrorSelector);
        } catch (Exception e) {
            return getPageBodyText();
        }
    }

    /**
     * Verifica se pelo menos uma mensagem de erro inline de validação está visível na tela.
     * 
     * @return true se houver um ou mais spans de erro renderizados no DOM; false caso contrário.
     */
    public boolean areAllInlineErrorsDisplayed() {
        try {
            // Aguarda os spans de erro aparecerem após o clique no botão Register
            page.waitForSelector("span.errors, span[id$='.errors']", new Page.WaitForSelectorOptions().setTimeout(4000));
            Locator inlineErrors = page.locator("span.errors, span[id$='.errors']");
            return inlineErrors.count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtém todo o texto do corpo da página (HTML <body>).
     * Útil para asserções abrangentes em falhas de integração ou exceções 500 do servidor.
     * 
     * @return Texto visível contido na tag body.
     */
    public String getPageBodyText() {
        return page.innerText("body");
    }

    // --- MÉTODOS AUXILIARES PRIVADOS (Mapeadores de Nomes para Seletores) ---

    /**
     * Traduz o nome amigável de um campo recebido do arquivo BDD/Cucumber para o seu seletor CSS de input.
     */
    private String getSelectorByFieldName(String fieldName) {
        switch (fieldName.trim()) {
            case "First Name": return firstNameInput;
            case "Last Name": return lastNameInput;
            case "Address": return streetInput;
            case "City": return cityInput;
            case "State": return stateInput;
            case "Zip Code": return zipCodeInput;
            case "Social Security":
            case "SSN": return ssnInput;
            case "Username": return usernameInput;
            case "Password": return passwordInput;
            case "Confirm Password":
            case "Confirm":
            case "repeatedPassword": return confirmPasswordInput;
            default: throw new IllegalArgumentException("Campo não reconhecido: " + fieldName);
        }
    }

    /**
     * Traduz o nome amigável do campo para o seletor CSS do seu respectivo span de mensagem de erro inline.
     */
    private String getErrorSpanIdByFieldName(String fieldName) {
        switch (fieldName.trim()) {
            case "First Name": return "span[id='customer.firstName.errors']";
            case "Last Name": return "span[id='customer.lastName.errors']";
            case "Address": return "span[id='customer.address.street.errors']";
            case "City": return "span[id='customer.address.city.errors']";
            case "State": return "span[id='customer.address.state.errors']";
            case "Zip Code": return "span[id='customer.address.zipCode.errors']";
            case "Social Security":
            case "SSN": return "span[id='customer.ssn.errors']";
            case "Username": return "span[id='customer.username.errors']";
            case "Password": return "span[id='customer.password.errors']";
            case "Confirm Password":
            case "Confirm":
            case "repeatedPassword": return "span[id='repeatedPassword.errors']";
            default: return "span.errors";
        }
    }
}