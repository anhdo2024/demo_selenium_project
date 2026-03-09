package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginTests extends BaseTest {

    private LoginPage createLoginPage() {
        return new LoginPage(driver);
    }

    @Test
    public void validLoginShouldNavigateToDashboard() {
        LoginPage loginPage = createLoginPage();
        Assert.assertTrue(loginPage.isAt(), "User is not on the login page.");

        loginPage.loginAs("Admin", "admin123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By dashboardHeaderLocator = By.cssSelector("h6.oxd-text.oxd-text--h6.oxd-topbar-header-breadcrumb-module");

        WebElement dashboardHeader = wait.until(
                ExpectedConditions.visibilityOfElementLocated(dashboardHeaderLocator)
        );

        Assert.assertEquals(dashboardHeader.getText().trim(), "Dashboard", "Dashboard header text did not match.");
    }

    @Test
    public void invalidLoginShouldShowErrorMessage() {
        LoginPage loginPage = createLoginPage();
        Assert.assertTrue(loginPage.isAt(), "User is not on the login page.");

        loginPage.loginAs("Admin", "wrongPassword");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> loginPage.isErrorDisplayed());

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message was not displayed for invalid login.");
        Assert.assertTrue(
                loginPage.getErrorMessage().toLowerCase().contains("invalid"),
                "Unexpected error message text: " + loginPage.getErrorMessage()
        );
    }
}

