package com.parabank.hooks;

import com.microsoft.playwright.Page;
import com.parabank.context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {

    private final TestContext testContext;
    private static Page page;

    // Método acessor para classes que consomem 'Hooks.getPage()' de forma estática
    public static Page getPage() {
        return page;
    }

    // O PicoContainer injeta o TestContext automaticamente
    public Hooks(TestContext testContext) {
        this.testContext = testContext;
        // Atribui a instância ativa do TestContext à variável estática 'page'
        if (testContext != null && testContext.isPageInitialized()) {
            page = testContext.getPage();
        }
    }

    @Before
    public void setUp() {
        // Assegura que o 'page' estático fique atualizado a cada novo cenário
        if (testContext != null && testContext.isPageInitialized()) {
            page = testContext.getPage();
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            // Captura screenshot em caso de falha caso a página esteja ativa
            if (scenario.isFailed() && testContext.isPageInitialized()) {
                byte[] screenshot = testContext.getPage().screenshot();
                scenario.attach(screenshot, "image/png", "Screenshot da Falha");
            }
        } catch (Exception e) {
            System.err.println("Não foi possível capturar o screenshot da falha: " + e.getMessage());
        } finally {
            // Limpa a referência estática e encerra os recursos do Playwright
            page = null;
            testContext.cleanup();
        }
    }
}