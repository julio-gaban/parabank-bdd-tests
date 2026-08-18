package com.parabank.steps;

import com.microsoft.playwright.Page;
import com.parabank.context.TestContext;
import com.parabank.pages.LoginPage;
import com.parabank.pages.TransferPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferSteps {

    private final LoginPage loginPage;
    private final TransferPage transferPage;

    // O PicoContainer injeta o TestContext automaticamente no construtor
    public TransferSteps(TestContext testContext) {
        Page page = testContext.getPage();
        this.loginPage = new LoginPage(page);
        this.transferPage = new TransferPage(page);
    }

    @Given("the user is logged into their ParaBank account")
    public void theUserIsLoggedIntoTheirParaBankAccount() {
        loginPage.navigateToHomePage();
        
        // AJUSTE: Utiliza os métodos existentes na LoginPage
        loginPage.enterUsername("john");
        loginPage.enterPassword("demo");
        loginPage.clickLogin();
    }

    @Given("the user navigates to the {string} page")
    public void theUserNavigatesToThePage(String pageName) {
        transferPage.navigateToTransferPage();
    }

    @When("the user enters an amount of {string}")
    public void theUserEntersAnAmountOf(String amount) {
        transferPage.enterAmount(amount);
    }

    @When("selects the source account and target account")
    public void selectsTheSourceAccountAndTargetAccount() {
        transferPage.selectAccounts();
    }

    @When("clicks the transfer button")
    public void clicksTheTransferButton() {
        transferPage.clickTransfer();
    }

    @Then("a transfer success message should be displayed")
    public void aTransferSuccessMessageShouldBeDisplayed() {
        String titleText = transferPage.getSuccessTitleText();
        assertEquals("Transfer Complete!", titleText.trim(),
            "The expected success title was not displayed.");
    }

    @Then("the transferred amount {string} should be confirmed in the summary")
    public void theTransferredAmountShouldBeConfirmedInTheSummary(String expectedAmount) {
        String actualAmount = transferPage.getTransferredAmountText();
        assertTrue(actualAmount.contains(expectedAmount),
            "The transferred amount displayed does not match the entered amount.");
    }
}