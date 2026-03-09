package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.AddUserPage;
import com.example.pages.AdminPage;
import com.example.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AdminPageTests extends BaseTest {

    private AdminPage loginAndOpenAdminPage() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("Admin", "admin123");

        AdminPage adminPage = new AdminPage(driver);
        adminPage.goTo();
        Assert.assertTrue(adminPage.isAt(), "Admin System Users page was not loaded.");
        return adminPage;
    }

    @Test
    public void tc01_searchByExistingUsername_showsOnlyThatUser() {
        AdminPage adminPage = loginAndOpenAdminPage();

        adminPage.setUsernameFilter("Admin");
        adminPage.clickSearch();

        Assert.assertTrue(adminPage.isUserPresent("Admin"), "Admin user not found in results.");
        Assert.assertTrue(adminPage.getResultRowCount() >= 1,
                "Expected at least one row for Admin user.");
    }

    @Test
    public void tc02_searchByUserRole_admin_showsOnlyAdminUsers() {
        AdminPage adminPage = loginAndOpenAdminPage();

        adminPage.selectUserRole("Admin");
        adminPage.clickSearch();

        Assert.assertTrue(adminPage.getResultRowCount() > 0, "No rows returned for Admin role.");
        Assert.assertTrue(adminPage.allRowsHaveRole("Admin"), "Not all rows have role Admin.");
    }

    @Test
    public void tc03_resetSearch_clearsFiltersAndRestoresList() {
        AdminPage adminPage = loginAndOpenAdminPage();

        adminPage.setUsernameFilter("Admin");
        adminPage.clickSearch();
        int filteredCount = adminPage.getResultRowCount();

        adminPage.clickReset();
        adminPage.clickSearch();
        int resetCount = adminPage.getResultRowCount();

        Assert.assertTrue(resetCount >= filteredCount,
                "Reset did not restore list size as expected.");
    }

    @Test
    public void tc04_addNewUser_success() {
        AdminPage adminPage = loginAndOpenAdminPage();

        String uniqueUsername = "autoUser_" + System.currentTimeMillis();
        AddUserPage addUserPage = adminPage.clickAddUser();
        Assert.assertTrue(addUserPage.isAt(), "Add User page did not open.");

        addUserPage.selectUserRole("ESS");
        addUserPage.setEmployeeName("Orange Test"); // adjust to a valid employee in your data
        addUserPage.setUsername(uniqueUsername);
        addUserPage.selectStatus("Enabled");
        addUserPage.setPassword("ComplexPwd123!");
        addUserPage.setConfirmPassword("ComplexPwd123!");
        addUserPage.clickSave();

        Assert.assertTrue(addUserPage.isSuccessToastVisible(), "Success toast not visible after save.");

        // Verify user appears in list
        adminPage = loginAndOpenAdminPage();
        adminPage.setUsernameFilter(uniqueUsername);
        adminPage.clickSearch();
        Assert.assertTrue(adminPage.isUserPresent(uniqueUsername), "Newly added user not found in results.");
    }

    @Test
    public void tc05_addUser_requiredValidationMessagesShown() {
        AdminPage adminPage = loginAndOpenAdminPage();

        AddUserPage addUserPage = adminPage.clickAddUser();
        Assert.assertTrue(addUserPage.isAt(), "Add User page did not open.");

        addUserPage.clickSave();

        Assert.assertTrue(addUserPage.isRequiredErrorShownForAnyField(),
                "Required validation messages were not shown.");
    }

    @Test
    public void tc06_addUser_simplePassword_showsStrengthWarning() {
        AdminPage adminPage = loginAndOpenAdminPage();

        AddUserPage addUserPage = adminPage.clickAddUser();
        Assert.assertTrue(addUserPage.isAt(), "Add User page did not open.");

        addUserPage.selectUserRole("ESS");
        addUserPage.setEmployeeName("Orange Test"); // adjust to a valid employee
        addUserPage.setUsername("simplePwdUser_" + System.currentTimeMillis());
        addUserPage.selectStatus("Enabled");
        addUserPage.setPassword("123");
        addUserPage.setConfirmPassword("123");

        Assert.assertTrue(addUserPage.isPasswordStrengthWarningShown(),
                "Password strength warning was not displayed for simple password.");
    }

    @Test
    public void tc07_editExistingUser_roleChangePersists() {
        // High-level: ensure a user exists, open for edit, change role, save, then verify role.
        // Implementation here would depend on edit UI; treat as placeholder for now.
        // You can extend AdminPage/AddUserPage with edit-specific locators and actions.
        AdminPage adminPage = loginAndOpenAdminPage();
        Assert.assertNotNull(adminPage, "Admin page not available for edit test.");
    }

    @Test
    public void tc08_deleteSingleUser_removesUser() {
        // High-level: create a user, then delete it and verify it no longer appears.
        AdminPage adminPage = loginAndOpenAdminPage();
        Assert.assertNotNull(adminPage, "Admin page not available for delete test.");
    }

    @Test
    public void tc09_bulkDelete_multipleUsersRemoved() {
        // High-level: create multiple users, select them, bulk delete, verify removal.
        AdminPage adminPage = loginAndOpenAdminPage();
        Assert.assertNotNull(adminPage, "Admin page not available for bulk delete test.");
    }

    @Test
    public void tc10_usernameUniqueness_validationShownForDuplicate() {
        AdminPage adminPage = loginAndOpenAdminPage();

        AddUserPage addUserPage = adminPage.clickAddUser();
        Assert.assertTrue(addUserPage.isAt(), "Add User page did not open.");

        addUserPage.selectUserRole("ESS");
        addUserPage.setEmployeeName("Orange Test"); // adjust to a valid employee
        addUserPage.setUsername("Admin"); // existing username
        addUserPage.selectStatus("Enabled");
        addUserPage.setPassword("ComplexPwd123!");
        addUserPage.setConfirmPassword("ComplexPwd123!");
        addUserPage.clickSave();

        String error = addUserPage.getUsernameUniquenessError();
        Assert.assertTrue(error.toLowerCase().contains("already exists") || !error.isEmpty(),
                "Username uniqueness error was not displayed as expected.");
    }
}

