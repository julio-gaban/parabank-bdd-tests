package com.parabank.context;

import com.microsoft.playwright.*;

/**
 * Classe responsável por gerenciar o ciclo de vida dos componentes do Playwright.
 * Atua como o contexto compartilhado (Dependency Injection) nos testes Cucumber,
 * garantindo que a mesma sessão de navegador seja reutilizada nos steps.
 */
public class TestContext {
    
    // Instância principal da API do Playwright que gerencia os drivers dos navegadores
    private Playwright playwright;
    
    // Instância do navegador selecionado (ex: Chromium, Firefox, WebKit)
    private Browser browser;
    
    // Sessão isolada do navegador (funciona como um perfil incógnito/limpo com cookies e cache próprios)
    private BrowserContext context;
    
    // Abstração de uma aba/página do navegador onde as interações do teste ocorrem
    private Page page;

    /**
     * Retorna a instância ativa da Page.
     * Aplica o padrão 'Lazy Initialization': a instância do Playwright e do naveagdor 
     * só é criada na primeira vez que este método for chamado.
     *
     * @return Page objeto pronto para interação no teste.
     */
    public Page getPage() {
        if (page == null) {
            // 1. Inicializa o servidor/driver do Playwright
            playwright = Playwright.create();
            
            // 2. Inicializa o navegador Chromium com interface gráfica visível (headless = false)
            browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
            );
            
            // 3. Cria uma nova sessão/perfil isolado de navegação
            context = browser.newContext();
            
            // 4. Abre uma nova aba/página dentro do contexto criado
            page = context.newPage();

            // Configuração dos timeouts globais do Playwright (valores em milissegundos)
            page.setDefaultTimeout(5000);           // Altera a espera padrão por elementos/ações de 30s para 5s
            page.setDefaultNavigationTimeout(10000); // Define o tempo limite para navegação entre URLs para 10s
        }
        return page;
    }

    /**
     * Verifica se a página do Playwright já foi instanciada.
     * Útil para ganchos (Hooks) de teardown que precisam saber se o navegador foi de fato aberto.
     *
     * @return true se a instância de Page existir; false caso contrário.
     */
    public boolean isPageInitialized() {
        return page != null;
    }

    /**
     * Encerra com segurança todos os recursos e conexões abertas no Playwright.
     * Deve ser chamado ao final da execução do teste (ex: em um Hook @After do Cucumber).
     * A ordem de fechamento respeita a hierarquia descendente dos objetos.
     */
    public void cleanup() {
        // 1. Fecha a aba/página e limpa a referência
        if (page != null) {
            page.close();
            page = null;
        }
        
        // 2. Fecha a sessão do navegador (limpa cookies e dados temporários)
        if (context != null) {
            context.close();
            context = null;
        }
        
        // 3. Encerra o processo do navegador
        if (browser != null) {
            browser.close();
            browser = null;
        }
        
        // 4. Finaliza a instância global do Playwright e libera os drivers do SO
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}