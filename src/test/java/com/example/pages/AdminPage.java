package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AdminPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Navigation / header
    private final By adminMenu = By.xpath("//span[text()='Admin']/ancestor::a");
    private final By systemUsersHeading = By.xpath("//h5[contains(@class,'oxd-text') and text()='System Users']");

    // Filters
    private final By usernameFilterInput = By.xpath("//label[text()='Username']/parent::div/following-sibling::div//input");
    private final By userRoleDropdown = By.xpath("//label[text()='User Role']/parent::div/following-sibling::div//div[contains(@class,'oxd-select-text')]");
    private final By statusDropdown = By.xpath("//label[text()='Status']/parent::div/following-sibling::div//div[contains(@class,'oxd-select-text')]");
    private final By employeeNameInput = By.xpath("//label[text()='Employee Name']/parent::div/following-sibling::div//input");
    private final By searchButton = By.xpath("//button[@type='submit' and .//span[text()='Search']]");
    private final By resetButton = By.xpath("//button[contains(@class,'oxd-button--ghost') and .//span[text()='Reset']]");

    // Table
    private final By tableRows = By.cssSelector("div.oxd-table-body div.oxd-table-card");
    private final By noRecordsRow = By.xpath("//span[text()='No Records Found']");
    private final By headerSelectAllCheckbox = By.cssSelector("div.oxd-table-header input[type='checkbox']");

    // Buttons / actions
    private final By addButton = By.xpath("//button[.//i[contains(@class,'bi-plus')]]");

    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void goTo() {
        wait.until(ExpectedConditions.elementToBeClickable(adminMenu)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(systemUsersHeading));
    }

    public boolean isAt() {
        boolean headingVisible = !driver.findElements(systemUsersHeading).isEmpty();
        return driver.getCurrentUrl().contains("/admin/viewSystemUsers") && headingVisible;
    }

    public void setUsernameFilter(String username) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameFilterInput));
        input.clear();
        input.sendKeys(username);
    }

    public void setEmployeeName(String employeeName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(employeeNameInput));
        input.clear();
        input.sendKeys(employeeName);
        // In real tests you might select from autocomplete suggestions here
    }

    public void selectUserRole(String roleVisibleText) {
        selectOptionFromDropdown(userRoleDropdown, roleVisibleText);
    }

    public void selectStatus(String statusVisibleText) {
        selectOptionFromDropdown(statusDropdown, statusVisibleText);
    }

    private void selectOptionFromDropdown(By dropdownLocator, String visibleText) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
        dropdown.click();
        By optionLocator = By.xpath("//div[@role='listbox']//span[text()='" + visibleText + "']");
        wait.until(ExpectedConditions.elementToBeClickable(optionLocator)).click();
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(tableRows),
                ExpectedConditions.visibilityOfElementLocated(noRecordsRow)
        ));
    }

    public void clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
    }

    public int getResultRowCount() {
        List<WebElement> rows = driver.findElements(tableRows);
        if (rows.isEmpty() && !driver.findElements(noRecordsRow).isEmpty()) {
            return 0;
        }
        return rows.size();
    }

    public boolean isUserPresent(String username) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            String rowUsername = row.findElement(By.cssSelector("div.oxd-table-cell:nth-child(2)")).getText().trim();
            if (rowUsername.equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean allRowsHaveRole(String role) {
        List<WebElement> rows = driver.findElements(tableRows);
        if (rows.isEmpty()) {
            return false;
        }
        for (WebElement row : rows) {
            String rowRole = row.findElement(By.cssSelector("div.oxd-table-cell:nth-child(3)")).getText().trim();
            if (!rowRole.equalsIgnoreCase(role)) {
                return false;
            }
        }
        return true;
    }

    public void selectUserCheckbox(String username) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            String rowUsername = row.findElement(By.cssSelector("div.oxd-table-cell:nth-child(2)")).getText().trim();
            if (rowUsername.equalsIgnoreCase(username)) {
                WebElement checkbox = row.findElement(By.cssSelector("div.oxd-table-cell input[type='checkbox']"));
                if (!checkbox.isSelected()) {
                    checkbox.click();
                }
                return;
            }
        }
    }

    public void selectUsersCheckboxes(String... usernames) {
        for (String username : usernames) {
            selectUserCheckbox(username);
        }
    }

    public void selectAllUsers() {
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(headerSelectAllCheckbox));
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    public AddUserPage clickAddUser() {
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
        return new AddUserPage(driver);
    }

    public void deleteUser(String username) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            String rowUsername = row.findElement(By.cssSelector("div.oxd-table-cell:nth-child(2)")).getText().trim();
            if (rowUsername.equalsIgnoreCase(username)) {
                WebElement deleteButton = row.findElement(By.cssSelector("button[title='Delete']"));
                deleteButton.click();
                confirmDelete();
                return;
            }
        }
    }

    public void bulkDeleteSelectedUsers() {
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//i[contains(@class,'bi-trash')]]")
        ));
        deleteButton.click();
        confirmDelete();
    }

    private void confirmDelete() {
        By yesButton = By.xpath("//button[contains(@class,'oxd-button--label-danger')]");
        wait.until(ExpectedConditions.elementToBeClickable(yesButton)).click();
        // Wait for table to refresh after deletion
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(tableRows),
                ExpectedConditions.visibilityOfElementLocated(noRecordsRow)
        ));
    }
}

