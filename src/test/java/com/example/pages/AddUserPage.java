package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.List;

public class AddUserPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By addUserHeading = By.xpath("//h6[contains(@class,'oxd-text') and text()='Add User']");

    private final By userRoleDropdown = By.xpath("//label[text()='User Role']/parent::div/following-sibling::div//div[contains(@class,'oxd-select-text')]");
    private final By statusDropdown = By.xpath("//label[text()='Status']/parent::div/following-sibling::div//div[contains(@class,'oxd-select-text')]");

    private final By employeeNameInput = By.xpath("//label[text()='Employee Name']/parent::div/following-sibling::div//input");
    private final By usernameInput = By.xpath("//label[text()='Username']/parent::div/following-sibling::div//input");
    private final By passwordInput = By.xpath("//label[text()='Password']/parent::div/following-sibling::div//input");
    private final By confirmPasswordInput = By.xpath("//label[text()='Confirm Password']/parent::div/following-sibling::div//input");
    //private final By employeeNameText = By.xpath("//label[text()='Employee Name']/parent::div/following-sibling::div//input");

    private final By saveButton = By.xpath("//button[contains(@class,'oxd-button') and normalize-space()='Save']");
    private final By cancelButton = By.xpath("//button[contains(@class,'oxd-button') and normalize-space()='Cancel']");


    private final By fieldErrorMessages = By.cssSelector("span.oxd-input-field-error-message");
    private final By toastMessage = By.cssSelector("div.oxd-toast");
    private final By usernameExistsError = By.xpath("//span[contains(text(),'Already exists')]");
    private final By passwordStrengthIndicator = By.xpath("//span[contains(@class,'oxd-input-group__message')]");

    public AddUserPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(addUserHeading));
    }

    public boolean isAt() {
        return !driver.findElements(addUserHeading).isEmpty();
    }

    private void selectDropdownOption(By dropdown, String visibleText) {
        WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(dropdown));
        dropdownElement.click();
        By optionLocator = By.xpath("//div[@role='listbox']//span[text()='" + visibleText + "']");
        wait.until(ExpectedConditions.elementToBeClickable(optionLocator)).click();
    }

    public void selectUserRole(String roleVisibleText) {
        selectDropdownOption(userRoleDropdown, roleVisibleText);
    }

    public void selectStatus(String statusVisibleText) {
        selectDropdownOption(statusDropdown, statusVisibleText);
    }

    // public void setEmployeeName(String name) {
    //     WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(employeeNameInput));
    //     input.clear();
    //     input.sendKeys(name);
    //     // In real tests you may need to select from autocomplete suggestions
    // }

    public void setEmployeeName(String employeeName) {

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='Employee Name']/parent::div/following-sibling::div//input")
        ));
    
        input.clear();
        input.sendKeys(employeeName);
    
        // wait for suggestion dropdown
        By suggestion = By.xpath("//div[@role='listbox']//span");
    
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestion)).click();
    }
    
    // public void setUsername(String username) {
    //     WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
    //     input.clear();
    //     input.sendKeys(username);
    // }

    public void setUsername(String username) {

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='Username']/parent::div/following-sibling::div//input")
        ));
    
        usernameInput.clear();
        usernameInput.sendKeys(username);
    
        // trigger validation (blur event)
        usernameInput.sendKeys(Keys.TAB);
    }

    public void setPassword(String password) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        input.clear();
        input.sendKeys(password);
    }

    public void setConfirmPassword(String password) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPasswordInput));
        input.clear();
        input.sendKeys(password);
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
    }

    public boolean isRequiredErrorShownForAnyField() {
        List<WebElement> errors = driver.findElements(fieldErrorMessages);
        return !errors.isEmpty();
    }

    public boolean isPasswordStrengthWarningShown() {
        return !driver.findElements(passwordStrengthIndicator).isEmpty();
    }

    public String getUsernameUniquenessError() {
        try {
            WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[text()='Username']/ancestor::div[contains(@class,'oxd-input-group')]//span")
            ));
            return error.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isSuccessToastVisible() {
        return !driver.findElements(toastMessage).isEmpty();
    }
}