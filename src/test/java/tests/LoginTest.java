package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;

/**
 * Login test suite for https://www.saucedemo.com (public demo site).
 *
 * Demonstrates:
 * - Page Object Model usage
 * - Allure annotations (@Feature, @Story, @Severity, @Description)
 * - Data-driven testing with @DataProvider
 * - Positive and negative test cases
 * - Assertion best practices
 */
@Feature("Authentication")
public class LoginTest extends BaseTest {

    // ── Positive Tests ────────────────────────────────────────────────────────

    @Test(description = "Valid credentials should redirect to products page")
    @Story("Successful Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that a standard user can log in with valid credentials and lands on the inventory page.")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage();

        Assert.assertTrue(loginPage.isLoaded(), "Login page should be displayed");

        loginPage.loginAs("standard_user", "secret_sauce");

        Assert.assertTrue(
                driver.getCurrentUrl().contains("inventory"),
                "After login, URL should contain 'inventory'. Actual: " + driver.getCurrentUrl()
        );
    }

    @Test(description = "Locked-out user should see an appropriate error")
    @Story("Blocked Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a locked-out user is shown a descriptive error message and cannot proceed.")
    public void testLockedOutUser() {
        LoginPage loginPage = new LoginPage();

        loginPage.loginAs("locked_out_user", "secret_sauce");

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be visible");
        Assert.assertTrue(
                loginPage.getErrorMessage().contains("locked out"),
                "Error should mention the account is locked out"
        );
    }

    // ── Negative / Data-driven Tests ──────────────────────────────────────────

    @Test(dataProvider = "invalidCredentials",
          description  = "Invalid credentials should display an error message")
    @Story("Failed Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that various invalid credential combinations all result in a visible error.")
    public void testInvalidCredentials(String username, String password, String scenario) {
        LoginPage loginPage = new LoginPage();

        loginPage.loginAs(username, password);

        Assert.assertTrue(
                loginPage.isErrorDisplayed(),
                "Error message should be displayed for scenario: " + scenario
        );
        Assert.assertFalse(
                driver.getCurrentUrl().contains("inventory"),
                "User should NOT be redirected to inventory for scenario: " + scenario
        );
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentialsProvider() {
        return new Object[][] {
                { "",               "",               "Empty username and password"   },
                { "standard_user",  "",               "Missing password"              },
                { "",               "secret_sauce",   "Missing username"              },
                { "wrong_user",     "secret_sauce",   "Wrong username"                },
                { "standard_user",  "wrong_password", "Wrong password"                },
                { "STANDARD_USER",  "secret_sauce",   "Username is case-sensitive"    },
        };
    }

    // ── Edge Cases ────────────────────────────────────────────────────────────

    @Test(description = "Login page title should be correct")
    @Story("Page Load")
    @Severity(SeverityLevel.MINOR)
    public void testLoginPageTitle() {
        Assert.assertEquals(
                driver.getTitle(),
                "Swag Labs",
                "Login page title mismatch"
        );
    }

    @Test(description = "Password field should mask input")
    @Story("Security")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the password field has type='password' to prevent shoulder-surfing.")
    public void testPasswordFieldMasked() {
        LoginPage loginPage = new LoginPage();
        loginPage.enterPassword("anypassword");

        String inputType = driver.findElement(
                org.openqa.selenium.By.id("password")).getAttribute("type");
        Assert.assertEquals(inputType, "password", "Password field should be of type 'password'");
    }
}
