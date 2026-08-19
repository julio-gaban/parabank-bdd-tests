package com.parabank.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.LocatorOptions;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Implementação do padrão Page Object Model (POM) para a página de Login do ParaBank.
 * Encapsula os mapeamentos de elementos (Locators) e as ações que podem ser realizadas na tela.
 */
public class LoginPage {
    
    // Instância da Page injetada para permitir a navegação e criação de mapeamentos
    private final Page page;

    // --- Mapeamento dos Mapeadores/Mapeamentos (Locators) ---
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator accountOverviewTitle;
    private final Locator welcomeMessage;
    private final Locator logoutLink;
    private final Locator errorMessageLocator;

    /**
     * Construtor da página.
     * Recebe a instância da Page e mapeia todos os elementos web necessários para as interações.
     *
     * @param page Instância ativa do Playwright utilizada para interagir com o navegador.
     */
    public LoginPage(Page page) {
        this.page = page;
        
        // Mapeamento dos campos de entrada de dados e botões do formulário
        this.usernameInput = page.locator("input[name='username']");
        this.passwordInput = page.locator("input[name='password']");
        this.loginButton = page.locator("input[value='Log In']");
        
        // Mapeamento genérico para captura de mensagens de erro no painel principal
        this.errorMessageLocator = page.locator("#rightPanel .error, #rightPanel p");
        
        // Filtra o seletor 'h1.title' especificamente pelo texto visível para garantir unicidade e evitar o erro de 'strict mode' do Playwright
        this.accountOverviewTitle = page.locator("h1.title", new LocatorOptions().setHasText("Accounts Overview"));
        
        // Mapeamento de elementos da sessão logada
        this.welcomeMessage = page.locator("p.smallText");
        this.logoutLink = page.locator("a[href*='logout.htm']");
    }

    /**
     * Navega diretamente para a URL inicial do sistema ParaBank.
     */
    public void navigateToHomePage() {
        page.navigate("https://parabank.parasoft.com/parabank/index.htm");
    }

    /**
     * Preenche o campo de nome de usuário no formulário de login.
     * 
     * @param username Nome de usuário a ser preenchido.
     */
    public void enterUsername(String username) {
        usernameInput.fill(username);
    }

    /**
     * Preenche o campo de senha no formulário de login.
     * 
     * @param password Senha a ser preenchida.
     */
    public void enterPassword(String password) {
        passwordInput.fill(password);
    }

    /**
     * Realiza o clique no botão de envio do formulário ("Log In").
     */
    public void clickLogin() {
        loginButton.click();
    }

    /**
     * Verifica se o título da área do cliente ("Accounts Overview") está visível na tela.
     * 
     * @return true se o título estiver visível, indicando sucesso no login; false caso contrário.
     */
    public boolean isAccountOverviewDisplayed() {
        return accountOverviewTitle.isVisible();
    }

    /**
     * Obtém o texto da mensagem de boas-vindas exibida após o login.
     * Aguarda explicitamente o elemento ficar visível antes de capturar o texto.
     * 
     * @return O texto de boas-vindas do usuário (ex: "Welcome John Doe").
     */
    public String getWelcomeMessageText() {
        welcomeMessage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        return welcomeMessage.innerText();
    }

    /**
     * Obtém o texto da mensagem de erro após uma tentativa inválida de login.
     * Utiliza fallback no seletor e aguarda o vínculo (ATTACHED) ao DOM com timeout de 5s.
     * 
     * @return Texto contido na mensagem de erro encontrada.
     */
    public String getErrorMessageText() {
        // Tenta capturar a mensagem em 'p.error' ou na área geral de erros de conteúdo (#rightPanel p)
        Locator generalError = page.locator("p.error, #rightPanel p.error, #rightPanel p");
        
        // Aguarda a presença do elemento no DOM (ATTACHED) sem forçar visibilidade estrita
        generalError.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000));
        
        return generalError.first().innerText();
    }

    // --- MÉTODOS DE NAVEGAÇÃO E SUPORTE A NOVOS CENÁRIOS ---

    /**
     * Efetua o clique no link de encerramento da sessão (Logout).
     * Aguarda o elemento estar visível antes de clicar.
     */
    public void clickLogout() {
        logoutLink.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        logoutLink.click();
    }

    /**
     * Verifica a visibilidade instantânea do formulário de login completo.
     * 
     * @return true se o usuário, senha e botão estiverem simultaneamente visíveis.
     */
    public boolean isLoginFormDisplayed() {
        return usernameInput.isVisible() && passwordInput.isVisible() && loginButton.isVisible();
    }

    /**
     * Verifica se o formulário de login está visível na tela de forma resiliente.
     * Aguarda até 3 segundos pelo campo de usuário para evitar falsos negativos causados por pequenas lentidões de renderização.
     *
     * @return true se todos os campos principais estiverem visíveis; false se houver timeout ou exceção.
     */
    public boolean isLoginFormVisible() {
        try {
            // Aguarda brevemente a presença do formulário no estado VISIBLE
            usernameInput.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(3000));

            return usernameInput.isVisible() && passwordInput.isVisible() && loginButton.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Captura o texto de erro de forma segura, tratando exceções e cenários extremos.
     * Possui mecanismo de fallback para capturar mensagens de erro interno (como Erro 500 ou SQL Injection no servidor).
     *
     * @return Texto do erro capturado na tela ou no corpo da página HTML.
     */
    public String getErrorMessageSafely() {
        try {
            // Aguarda a mensagem específica de erro aparecer na interface
            errorMessageLocator.first().waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(3000));
            return errorMessageLocator.first().innerText();
        } catch (Exception e) {
            // Fallback: em cenários extremos (ex: exceção do servidor/SQL Injection), lê o texto bruto do <body>
            String bodyContent = page.locator("body").innerText();
            if (bodyContent.contains("An internal error has occurred") || bodyContent.contains("Error!")) {
                return bodyContent;
            }
            return "ERRO_DOM_NAO_ENCONTRADO";
        }
    }
}