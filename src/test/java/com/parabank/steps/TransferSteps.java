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

/**
 * Mapeamento dos passos (Step Definitions) do Cucumber para as funcionalidades de Transferência de Fundos.
 * Coordena as interações necessárias entre as páginas de Login e Transferência para validar os cenários Gherkin.
 */
public class TransferSteps {

    private final LoginPage loginPage;
    private final TransferPage transferPage;

    /**
     * Construtor com Injeção de Dependência via PicoContainer.
     * Recupera a instância compartilhada da 'Page' do Playwright a partir do 'TestContext'
     * e inicializa as Page Objects necessárias para os passos deste arquivo.
     * 
     * @param testContext Contexto compartilhado de execução dos testes.
     */
    public TransferSteps(TestContext testContext) {
        Page page = testContext.getPage();
        this.loginPage = new LoginPage(page);
        this.transferPage = new TransferPage(page);
    }

    /**
     * Garante o pré-requisito de autenticação no ParaBank realizando o login automático
     * com credenciais padrão de demonstração ("john" / "demo").
     */
    @Given("the user is logged into their ParaBank account")
    public void theUserIsLoggedIntoTheirParaBankAccount() {
        loginPage.navigateToHomePage();
        loginPage.enterUsername("john");
        loginPage.enterPassword("demo");
        loginPage.clickLogin();
    }

    /**
     * Executa a navegação para a tela de transferência de fundos.
     * 
     * @param pageName Nome da página capturado do passo BDD (mantido como parâmetro para reuso da sintaxe Gherkin).
     */
    @Given("the user navigates to the {string} page")
    public void theUserNavigatesToThePage(String pageName) {
        transferPage.navigateToTransferPage();
    }

    /**
     * Preenche o campo de valor a ser transferido no formulário.
     * 
     * @param amount Valor da transferência capturado da especificação BDD.
     */
    @When("the user enters an amount of {string}")
    public void theUserEntersAnAmountOf(String amount) {
        transferPage.enterAmount(amount);
    }

    /**
     * Seleciona as contas de origem (from) e destino (to) com base nos seus índices de posição no dropdown.
     * 
     * @param fromIndex Índice da conta de origem no elemento select.
     * @param toIndex Índice da conta de destino no elemento select.
     */
    @When("selects source account index {int} and target account index {int}")
    public void selectsSourceAccountIndexAndTargetAccountIndex(int fromIndex, int toIndex) {
        transferPage.selectAccountsByIndex(fromIndex, toIndex);
    }

    /**
     * Aciona a submissão do formulário clicando no botão de transferência.
     */
    @When("clicks the transfer button")
    public void clicksTheTransferButton() {
        transferPage.clickTransfer();
    }

    /**
     * Valida se o título de confirmação de transferência concluída ("Transfer Complete!") é exibido na tela de resumo.
     */
    @Then("a transfer success message should be displayed")
    public void aTransferSuccessMessageShouldBeDisplayed() {
        String titleText = transferPage.getSuccessTitleText();
        assertEquals("Transfer Complete!", titleText.trim(),
            "The expected success title was not displayed.");
    }

    /**
     * Confirma se o valor exibido na tela de resumo/comprovante corresponde exatamente ao valor preenchido no formulário.
     * 
     * @param expectedAmount Valor esperado para confirmação.
     */
    @Then("the transferred amount {string} should be confirmed in the summary")
    public void theTransferredAmountShouldBeConfirmedInTheSummary(String expectedAmount) {
        String actualAmount = transferPage.getTransferredAmountText();
        assertTrue(actualAmount.contains(expectedAmount),
            "The transferred amount displayed does not match the entered amount.");
    }

    /**
     * Valida a exibição e o texto exato da mensagem de erro ao fornecer um valor inválido para transferência.
     * 
     * @param expectedError Mensagem de erro esperada segundo o cenário BDD.
     */
    @Then("an amount validation error {string} should be displayed")
    public void anAmountValidationErrorShouldBeDisplayed(String expectedError) {
        String actualError = transferPage.getErrorMessageText();
        assertEquals(expectedError, actualError,
            "The displayed error message does not match the expected message.");
    }
}