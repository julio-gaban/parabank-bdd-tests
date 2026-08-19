package com.parabank.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Classe utilitária responsável pelo gerenciamento do ciclo de vida das instâncias do Playwright (Singleton).
 * Centraliza a inicialização do navegador, a criação de páginas e o encerramento seguro dos recursos.
 */
public class PlaywrightDriver {

    // Instância principal da API do Playwright
    private static Playwright playwright;
    
    // Instância do navegador (Chromium/Chrome)
    private static Browser browser;
    
    // Instância da aba/página ativa no navegador
    private static Page page;

    /**
     * Retorna a instância ativa da 'Page' do Playwright.
     * Caso o contexto ainda não exista, inicializa o servidor do Playwright,
     * lança a instância do navegador Chromium (em modo com interface gráfica - Headless false) 
     * e abre uma nova página.
     * 
     * @return Objeto Page ativo para execução das ações nos testes.
     */
    public static Page getPage() {
        if (page == null) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            page = browser.newPage();
        }
        return page;
    }

    /**
     * Encerra com segurança o navegador e o processo do Playwright.
     * Limpa a referência estática da página para permitir uma nova inicialização em execuções futuras.
     */
    public static void close() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
        page = null;
    }
}