package com.parabank.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class RegisterPage {

    private final Page page;

    // Seletores Mapeados
    private final String firstNameInput = "input[id='customer.firstName']";
    private final String lastNameInput = "input[id='customer.lastName']";
    private final String streetInput = "input[id='customer.address.street']";
    private final String cityInput = "input[id='customer.address.city']";
    private final String stateInput = "input[id='customer.address.state']";
    private final String zipCodeInput = "input[id='customer.address.zipCode']";
    private final String phoneNumberInput = "input[id='customer.phoneNumber']";
    private final String ssnInput = "input[id='customer.ssn']";
    private final String usernameInput = "input[id='customer.username']";
    private final String passwordInput = "input[id='customer.password']";
    private final String confirmPasswordInput = "input[id='repeatedPassword']";
    private final String registerButton = "input[value='Register']";

    // Mensagens e Painéis
    private final String successTitle = "#rightPanel h1.title";
    private final String successMessage = "#rightPanel p";
    private final String generalErrorSelector = ".error, span[id$='.errors']";

    public RegisterPage(Page page) {
        if (page == null) {
            throw new IllegalArgumentException("A instância de Page do Playwright não pode ser nula.");
        }
        this.page = page;
    }

    public void navigateToRegisterPage() {
        page.navigate("https://parabank.parasoft.com/parabank/register.htm");
    }

    public void fillFieldWithValue(String fieldName, String value) {
        String selector = getSelectorByFieldName(fieldName);
        page.fill(selector, value);
    }

    public void fillRegistrationFormWithValidData(String username) {
        page.fill(firstNameInput, "John");
        page.fill(lastNameInput, "Doe");
        page.fill(streetInput, "123 Main St");
        page.fill(cityInput, "Springfield");
        page.fill(stateInput, "IL");
        page.fill(zipCodeInput, "62701");
        page.fill(phoneNumberInput, "555-1234");
        page.fill(ssnInput, "123-45-6789");
        page.fill(usernameInput, username);
        page.fill(passwordInput, "Password123");
        page.fill(confirmPasswordInput, "Password123");
    }

    public void fillRegistrationFormExceptField(String fieldToSkip, String username) {
        fillRegistrationFormWithValidData(username);
        String selector = getSelectorByFieldName(fieldToSkip);
        page.fill(selector, "");
    }

    public void fillRegistrationFormWithCustomPasswords(String username, String password, String confirmPassword) {
        fillRegistrationFormWithValidData(username);
        page.fill(passwordInput, password);
        page.fill(confirmPasswordInput, confirmPassword);
    }

    public void clickRegisterButton() {
        page.click(registerButton);
    }

    public boolean isRegistrationSuccessful() {
        try {
            page.waitForSelector(successTitle, new Page.WaitForSelectorOptions().setTimeout(3000));
            return page.innerText(successTitle).contains("Welcome");
        } catch (Exception e) {
            return false;
        }
    }

    public String getSuccessMessageSafely() {
        try {
            return page.innerText(successMessage);
        } catch (Exception e) {
            return "";
        }
    }

    public String getDuplicateUsernameErrorMessageSafely() {
        try {
            return page.innerText("span[id='customer.username.errors']");
        } catch (Exception e) {
            return page.innerText("#rightPanel");
        }
    }

    public String getInlineFieldErrorSafely(String fieldName) {
        String errorSpanId = getErrorSpanIdByFieldName(fieldName);
        try {
            page.waitForSelector(errorSpanId, new Page.WaitForSelectorOptions().setTimeout(3000));
            return page.innerText(errorSpanId);
        } catch (Exception e) {
            return "";
        }
    }

    public String getGeneralErrorMessageSafely() {
        try {
            page.waitForSelector(generalErrorSelector, new Page.WaitForSelectorOptions().setTimeout(3000));
            return page.innerText(generalErrorSelector);
        } catch (Exception e) {
            return getPageBodyText();
        }
    }

    public boolean areAllInlineErrorsDisplayed() {
        Locator inlineErrors = page.locator("span.errors");
        return inlineErrors.count() > 0 && inlineErrors.first().isVisible();
    }

    public String getPageBodyText() {
        return page.innerText("body");
    }

    private String getSelectorByFieldName(String fieldName) {
        switch (fieldName) {
            case "First Name": return firstNameInput;
            case "Last Name": return lastNameInput;
            case "Address": return streetInput;
            case "City": return cityInput;
            case "State": return stateInput;
            case "Zip Code": return zipCodeInput;
            case "Social Security": return ssnInput;
            case "Username": return usernameInput;
            case "Password": return passwordInput;
            case "Confirm Password": return confirmPasswordInput;
            default: throw new IllegalArgumentException("Campo não reconhecido: " + fieldName);
        }
    }

    private String getErrorSpanIdByFieldName(String fieldName) {
        switch (fieldName) {
            case "First Name": return "span[id='customer.firstName.errors']";
            case "Last Name": return "span[id='customer.lastName.errors']";
            case "Address": return "span[id='customer.address.street.errors']";
            case "City": return "span[id='customer.address.city.errors']";
            case "State": return "span[id='customer.address.state.errors']";
            case "Zip Code": return "span[id='customer.address.zipCode.errors']";
            case "Social Security": return "span[id='customer.ssn.errors']";
            case "Username": return "span[id='customer.username.errors']";
            case "Password": return "span[id='customer.password.errors']";
            case "Confirm Password": return "span[id='repeatedPassword.errors']";
            default: return "span.errors";
        }
    }
}