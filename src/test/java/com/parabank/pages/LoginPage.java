package com.parabank.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.LocatorOptions;
import com.microsoft.playwright.options.WaitForSelectorState;

public class LoginPage {
    private final Page page;

    // Locators
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator accountOverviewTitle;
    private final Locator welcomeMessage;
    private final Locator logoutLink;
    private final Locator errorMessageLocator;

    public LoginPage(Page page) {
        this.page = page;
        this.usernameInput = page.locator("input[name='username']");
        this.passwordInput = page.locator("input[name='password']");
        this.loginButton = page.locator("input[value='Log In']");
        this.errorMessageLocator = page.locator("#rightPanel .error, #rightPanel p");
        
        // Filtra o h1.title para garantir unicidade e evitar o erro de strict mode
        this.accountOverviewTitle = page.locator("h1.title", new LocatorOptions().setHasText("Accounts Overview"));
        
        this.welcomeMessage = page.locator("p.smallText");
        this.logoutLink = page.locator("a[href*='logout.htm']");
    }

    public void navigateToHomePage() {
        page.navigate("https://parabank.parasoft.com/parabank/index.htm");
    }

    public void enterUsername(String username) {
        usernameInput.fill(username);
    }

    public void enterPassword(String password) {
        passwordInput.fill(password);
    }

    public void clickLogin() {
        loginButton.click();
    }

    public boolean isAccountOverviewDisplayed() {
        return accountOverviewTitle.isVisible();
    }

    public String getWelcomeMessageText() {
        welcomeMessage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        return welcomeMessage.innerText();
    }

    public String getErrorMessageText() {
        // Tenta capturar a mensagem em 'p.error' ou na área geral de erros de conteúdo (#rightPanel p)
        Locator generalError = page.locator("p.error, #rightPanel p.error, #rightPanel p");
        
        // Aguarda a presença do elemento na DOM (attached) sem forçar visibilidade visual estrita
        generalError.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000));
        
        return generalError.first().innerText();
    }

    // NOVOS MÉTODOS: Suporte aos novos cenários de logout e validação de formulário

    public void clickLogout() {
        logoutLink.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        logoutLink.click();
    }

    public boolean isLoginFormDisplayed() {
        return usernameInput.isVisible() && passwordInput.isVisible() && loginButton.isVisible();
    }

    /**
     * Verifica se o formulário de login está visível na tela.
     * Retorna true se os campos principais estiverem visíveis, ou false caso contrário.
     */
    public boolean isLoginFormVisible() {
        try {
            // Aguarda brevemente a presença do formulário para evitar falsos negativos rápidos
            usernameInput.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(3000));

            return usernameInput.isVisible() && passwordInput.isVisible() && loginButton.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Captura o texto da mensagem de erro de forma segura, evitando Timeouts longos.
     */
    public String getErrorMessageSafely() {
        try {
            // Aguarda a mensagem aparecer na tela
            errorMessageLocator.first().waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(3000));
            return errorMessageLocator.first().innerText();
        } catch (Exception e) {
            // Fallback: se o painel falhar devido a SQL Injection / Erro 500
            String bodyContent = page.locator("body").innerText();
            if (bodyContent.contains("An internal error has occurred") || bodyContent.contains("Error!")) {
                return bodyContent;
            }
            return "ERRO_DOM_NAO_ENCONTRADO";
        }
    }
}