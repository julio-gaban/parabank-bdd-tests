package com.parabank.hooks;

import com.microsoft.playwright.Page;
import com.parabank.context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Classe de Hooks do Cucumber responsável pelo ciclo de vida de cada cenário de teste.
 * Gerencia a inicialização, captura de evidências (screenshots) e encerramento do navegador.
 */
public class Hooks {

    // Instância do contexto gerenciado pelo PicoContainer (Injeção de Dependência)
    private final TestContext testContext;
    
    // Referência estática mantida para permitir acesso direto à Page caso necessário
    private static Page page;

    /**
     * Método de acesso (getter) estático para a página do Playwright.
     * Permite obter a 'Page' atual sem a necessidade de injetar o TestContext em todas as classes.
     * 
     * @return Instância atual da Page do Playwright.
     */
    public static Page getPage() {
        return page;
    }

    /**
     * Construtor da classe.
     * O 'cucumber-picocontainer' injeta a mesma instância do TestContext aqui
     * e em todas as outras classes de Steps do mesmo cenário.
     * 
     * @param testContext Instância compartilhada do contexto do teste.
     */
    public Hooks(TestContext testContext) {
        this.testContext = testContext;
        
        // Se o contexto já tiver inicializado a página no momento da construção, atualiza o atributo estático
        if (testContext != null && testContext.isPageInitialized()) {
            page = testContext.getPage();
        }
    }

    /**
     * Hook executado antes de cada cenário de teste do Cucumber.
     * Garante que a referência estática 'page' esteja sincronizada com a instância do cenário atual.
     */
    @Before
    public void setUp() {
        // Assegura que o 'page' estático fique atualizado a cada novo cenário
        if (testContext != null && testContext.isPageInitialized()) {
            page = testContext.getPage();
        }
    }

    /**
     * Hook executado após a conclusão de cada cenário de teste do Cucumber.
     * Captura evidências visuais em caso de falha e encerram os recursos do Playwright.
     * 
     * @param scenario Objeto do Cucumber contendo metadados e o status da execução do cenário atual.
     */
    @After
    public void tearDown(Scenario scenario) {
        try {
            // Captura screenshot e anexa ao relatório do Cucumber apenas se o cenário falhar e o navegador estiver aberto
            if (scenario.isFailed() && testContext.isPageInitialized()) {
                byte[] screenshot = testContext.getPage().screenshot();
                scenario.attach(screenshot, "image/png", "Screenshot da Falha");
            }
        } catch (Exception e) {
            // Garante que se a captura de imagem falhar (ex: navegador travado), o erro seja tratado sem quebrar o relatório
            System.err.println("Não foi possível capturar o screenshot da falha: " + e.getMessage());
        } finally {
            // Bloco final para garantir que as referências e processos sejam sempre encerrados (mesmo se ocorrerem exceções)
            page = null;
            testContext.cleanup();
        }
    }
}