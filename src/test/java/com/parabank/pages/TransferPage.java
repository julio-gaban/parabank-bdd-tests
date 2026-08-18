package com.parabank.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

public class TransferPage {
    private final Page page;

    private final Locator transferFundsLink;
    private final Locator amountInput;
    private final Locator fromAccountSelect;
    private final Locator toAccountSelect;
    private final Locator transferButton;

    private final Locator successTitle;
    private final Locator resultAmount;
    private final Locator amountErrorMsg;

    public TransferPage(Page page) {
        this.page = page;
        this.transferFundsLink = page.locator("a[href*='transfer.htm']");
        this.amountInput = page.locator("input[id='amount']");
        this.fromAccountSelect = page.locator("select[id='fromAccountId']");
        this.toAccountSelect = page.locator("select[id='toAccountId']");
        this.transferButton = page.locator("input[value='Transfer']");
        
        this.successTitle = page.locator("h1.title", new Page.LocatorOptions().setHasText("Transfer Complete!"));
        this.resultAmount = page.locator("span[id='amountResult']");
        
        // Mapeia diretamente o ID retornado pelo ParaBank no log
        this.amountErrorMsg = page.locator("#amount\\.errors, p.error");
    }

    public void navigateToTransferPage() {
        transferFundsLink.click();
        amountInput.waitFor();
    }

    public void enterAmount(String amount) {
        amountInput.fill(amount);
    }

    public void selectAccountsByIndex(int fromIndex, int toIndex) {
        page.waitForSelector("select[id='fromAccountId'] option", 
            new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED)
        );
        
        fromAccountSelect.selectOption(new SelectOption().setIndex(fromIndex));
        toAccountSelect.selectOption(new SelectOption().setIndex(toIndex));
    }

    public void clickTransfer() {
        transferButton.click();
    }

    public String getSuccessTitleText() {
        successTitle.waitFor();
        return successTitle.textContent().trim();
    }

    public String getTransferredAmountText() {
        resultAmount.waitFor();
        return resultAmount.textContent().trim();
    }

    public String getErrorMessageText() {
        // Aguarda estar anexado ao DOM com timeout reduzido de 5 segundos
        amountErrorMsg.first().waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.ATTACHED)
            .setTimeout(5000)
        );
        return amountErrorMsg.first().textContent().trim();
    }
}