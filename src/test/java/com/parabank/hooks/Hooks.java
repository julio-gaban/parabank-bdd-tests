package com.parabank.hooks;

import com.parabank.context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

public class Hooks {
    private final TestContext testContext;

    // O PicoContainer injeta o TestContext aqui automaticamente
    public Hooks(TestContext testContext) {
        this.testContext = testContext;
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            // Só tira screenshot se o cenário falhou E o navegador/página foi de fato inicializado
            if (scenario.isFailed() && testContext.isPageInitialized()) {
                byte[] screenshot = testContext.getPage().screenshot();
                scenario.attach(screenshot, "image/png", "Screenshot da Falha");
            }
        } catch (Exception e) {
            System.err.println("Não foi possível capturar o screenshot da falha: " + e.getMessage());
        } finally {
            // O bloco finally garante que o navegador e recursos do Playwright
            // serão sempre encerrados, mesmo se o screenshot falhar
            testContext.cleanup();
        }
    }
}