package com.parabank.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

public class TransferPage {
    private final Page page;

    // Locators
    private final Locator transferFundsLink;
    private final Locator amountInput;
    private final Locator fromAccountSelect;
    private final Locator toAccountSelect;
    private final Locator transferButton;

    // Assertion Locators
    private final Locator successTitle;
    private final Locator resultAmount;

    public TransferPage(Page page) {
        this.page = page;
        this.transferFundsLink = page.locator("a[href*='transfer.htm']");
        this.amountInput = page.locator("input[id='amount']");
        this.fromAccountSelect = page.locator("select[id='fromAccountId']");
        this.toAccountSelect = page.locator("select[id='toAccountId']");
        this.transferButton = page.locator("input[value='Transfer']");
        
        // AJUSTE PRINCIPAL: Filtra o h1.title para pegar especificamente o do cabeçalho de confirmação
        this.successTitle = page.locator("h1.title", new Page.LocatorOptions().setHasText("Transfer Complete!"));
        
        this.resultAmount = page.locator("span[id='amountResult']");
    }

    public void navigateToTransferPage() {
        transferFundsLink.click();
        amountInput.waitFor();
    }

    public void enterAmount(String amount) {
        amountInput.fill(amount);
    }

    public void selectAccounts() {
        // Aguarda até que os elementos <option> estejam carregados na árvore DOM via AJAX do ParaBank
        page.waitForSelector("select[id='fromAccountId'] option", 
            new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED)
        );
        
        // Seleciona a primeira conta de origem e a primeira de destino
        fromAccountSelect.selectOption(new SelectOption().setIndex(0));
        toAccountSelect.selectOption(new SelectOption().setIndex(0));
    }

    public void clickTransfer() {
        transferButton.click();
    }

    public String getSuccessTitleText() {
        // Agora o locator referencia unicamente o elemento "Transfer Complete!"
        successTitle.waitFor();
        return successTitle.textContent().trim();
    }

    public String getTransferredAmountText() {
        resultAmount.waitFor();
        return resultAmount.textContent().trim();
    }
}