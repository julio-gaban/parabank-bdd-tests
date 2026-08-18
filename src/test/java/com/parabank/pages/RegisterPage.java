package com.parabank.pages;

import com.microsoft.playwright.Page;

public class RegisterPage {

    private final Page page;

    private final String firstNameInput = "input[id='customer.firstName']";
    private final String lastNameInput = "input[id='customer.lastName']";
    private final String addressInput = "input[id='customer.address.street']";
    private final String cityInput = "input[id='customer.address.city']";
    private final String stateInput = "input[id='customer.address.state']";
    private final String zipCodeInput = "input[id='customer.address.zipCode']";
    private final String phoneNumberInput = "input[id='customer.phoneNumber']";
    private final String ssnInput = "input[id='customer.ssn']";
    private final String usernameInput = "input[id='customer.username']";
    private final String passwordInput = "input[id='customer.password']";
    private final String confirmPasswordInput = "input[id='repeatedPassword']";
    private final String registerButton = "input[value='Register']";

    private final String successTitle = "h1.title";
    private final String successText = "div#rightPanel p";
    private final String usernameError = "span[id='customer.username.errors']";

    public RegisterPage(Page page) {
        this.page = page;
    }

    public void navigateToRegisterPage() {
        page.navigate("https://parabank.parasoft.com/parabank/register.htm");
    }

    public void fillRegistrationForm(String firstName, String lastName, String address, 
                                     String city, String state, String zipCode, 
                                     String phone, String ssn, String username, String password) {
        page.fill(firstNameInput, firstName);
        page.fill(lastNameInput, lastName);
        page.fill(addressInput, address);
        page.fill(cityInput, city);
        page.fill(stateInput, state);
        page.fill(zipCodeInput, zipCode);
        page.fill(phoneNumberInput, phone);
        page.fill(ssnInput, ssn);
        page.fill(usernameInput, username);
        page.fill(passwordInput, password);
        page.fill(confirmPasswordInput, password);
    }

    public void clickRegister() {
        page.click(registerButton);
    }

    public boolean isRegistrationSuccessful() {
        // Aguarda até 5 segundos para o título de confirmação aparecer na página
        page.waitForSelector(successTitle, new Page.WaitForSelectorOptions().setTimeout(5000));
        String titleText = page.innerText(successTitle);
        
        // No ParaBank, o cadastro concluído exibe um título iniciado por "Welcome" 
        // e um parágrafo confirmando a criação da conta.
        return titleText.contains("Welcome") || page.innerText(successText).contains("Your account was created successfully");
    }

    public String getWelcomeMessageText() {
        page.waitForSelector(successTitle, new Page.WaitForSelectorOptions().setTimeout(5000));
        return page.innerText(successTitle);
    }

    public String getUsernameErrorMessageText() {
        page.waitForSelector(usernameError, new Page.WaitForSelectorOptions().setTimeout(5000));
        return page.innerText(usernameError);
    }
}