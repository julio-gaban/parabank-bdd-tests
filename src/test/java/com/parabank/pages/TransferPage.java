package com.parabank.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Implementação do padrão Page Object Model (POM) para a página de Transferência de Fundos (Transfer Funds) do ParaBank.
 * Encapsula os elementos da tela e as interações para movimentação entre contas e validação de erros.
 */
public class TransferPage {

    // Instância da Page do Playwright para interagir com o navegador
    private final Page page;

    // --- Mapeamento dos Locators dos Campos do Formulário ---
    private final Locator transferFundsLink;
    private final Locator amountInput;
    private final Locator fromAccountSelect;
    private final Locator toAccountSelect;
    private final Locator transferButton;

    // --- Mapeamento dos Locators dos Resultados e Mensagens ---
    private final Locator successTitle;
    private final Locator resultAmount;
    private final Locator amountErrorMsg;

    /**
     * Construtor da classe TransferPage.
     * Mapeia os elementos da tela de transferência e prepara os seletores de resultados e mensagens.
     *
     * @param page Instância ativa da Page do Playwright.
     */
    public TransferPage(Page page) {
        this.page = page;
        
        // Mapeamento dos elementos de entrada e ação
        this.transferFundsLink = page.locator("a[href*='transfer.htm']");
        this.amountInput = page.locator("input[id='amount']");
        this.fromAccountSelect = page.locator("select[id='fromAccountId']");
        this.toAccountSelect = page.locator("select[id='toAccountId']");
        this.transferButton = page.locator("input[value='Transfer']");
        
        // Mapeamento do título de confirmação filtrado pelo texto visível para garantir unicidade
        this.successTitle = page.locator("h1.title", new Page.LocatorOptions().setHasText("Transfer Complete!"));
        this.resultAmount = page.locator("span[id='amountResult']");
        
        // Mapeia o ID de erro do valor (#amount.errors com o ponto escapado como \\.) ou parágrafos de erro gerais
        this.amountErrorMsg = page.locator("#amount\\.errors, p.error");
    }

    /**
     * Navega até a página de transferência clicando no menu lateral
     * e aguarda até que o campo de valor esteja totalmente carregado e visível.
     */
    public void navigateToTransferPage() {
        transferFundsLink.click();
        amountInput.waitFor();
    }

    /**
     * Preenche o campo de valor a ser transferido.
     * 
     * @param amount Valor a ser inserido no input (ex: "100.00").
     */
    public void enterAmount(String amount) {
        amountInput.fill(amount);
    }

    /**
     * Seleciona as contas de origem e destino nos elementos <select> com base no índice numérico da opção.
     * Aguarda o carregamento prévio das opções dentro do DOM (populadas via requisição assíncrona).
     * 
     * @param fromIndex Posição/Índice da conta de origem no combobox (iniciando em 0).
     * @param toIndex Posição/Índice da conta de destino no combobox (iniciando em 0).
     */
    public void selectAccountsByIndex(int fromIndex, int toIndex) {
        // Aguarda até que as opções (<option>) do select estejam devidamente anexadas (ATTACHED) ao DOM
        page.waitForSelector("select[id='fromAccountId'] option", 
            new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED)
        );
        
        // Seleciona a opção desejada em cada dropdown usando o índice
        fromAccountSelect.selectOption(new SelectOption().setIndex(fromIndex));
        toAccountSelect.selectOption(new SelectOption().setIndex(toIndex));
    }

    /**
     * Efetua o clique no botão de submissão da transferência ("Transfer").
     */
    public void clickTransfer() {
        transferButton.click();
    }

    /**
     * Captura o texto do título exibido após a conclusão da transferência.
     * Aguarda a renderização do elemento na tela.
     * 
     * @return O texto do título limpo (sem espaços extras nas pontas).
     */
    public String getSuccessTitleText() {
        successTitle.waitFor();
        return successTitle.textContent().trim();
    }

    /**
     * Captura o texto do valor confirmado na mensagem de sucesso.
     * 
     * @return O valor transferido conforme exibido na tela de confirmação.
     */
    public String getTransferredAmountText() {
        resultAmount.waitFor();
        return resultAmount.textContent().trim();
    }

    /**
     * Captura o texto da mensagem de erro referente ao valor da transferência.
     * Aguarda o vínculo do elemento ao DOM com um tempo limite (timeout) otimizado de 5 segundos.
     * 
     * @return O texto limpo da mensagem de erro encontrada.
     */
    public String getErrorMessageText() {
        // Aguarda estar anexado ao DOM com timeout reduzido de 5 segundos
        amountErrorMsg.first().waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.ATTACHED)
            .setTimeout(5000)
        );
        return amountErrorMsg.first().textContent().trim();
    }
}