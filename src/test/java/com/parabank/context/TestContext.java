package com.parabank.context;

import com.microsoft.playwright.*;

public class TestContext {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    public Page getPage() {
        if (page == null) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
            );
            context = browser.newContext();
            page = context.newPage();

            // Configuração dos timeouts globais (em milissegundos)
            page.setDefaultTimeout(5000);           // Reduz o tempo de espera padrão de locators/açoes de 30s para 5s
            page.setDefaultNavigationTimeout(10000); // Define o tempo máximo de carregamento de paginas para 10s
        }
        return page;
    }

    public boolean isPageInitialized() {
        return page != null;
    }

    public void cleanup() {
        if (page != null) {
            page.close();
            page = null;
        }
        if (context != null) {
            context.close();
            context = null;
        }
        if (browser != null) {
            browser.close();
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}