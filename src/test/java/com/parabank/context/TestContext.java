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
        }
        return page;
    }

    // Método auxiliar adicionado
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