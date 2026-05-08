package pages;

import framework.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object for the Login page.
 *
 * Demonstrates the POM pattern:
 * - Locators are private and encapsulated here
 * - Public methods expose user-facing actions
 * - No assertions here — those belong in test classes
 */
public class LoginPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By usernameField    = By.id("user-name");
    private final By passwordField    = By.id("password");
    private final By loginButton      = By.id("login-button");
    private final By errorMessage     = By.cssSelector("[data-test='error']");
    private final By loginContainer   = By.className("login-container");

    // ── Page Actions ──────────────────────────────────────────────────────────

    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    @Step("Click Login button")
    public void clickLogin() {
        click(loginButton);
    }

    /**
     * Convenience method: fills credentials and submits in one call.
     */
    @Step("Login with username: {username}")
    public void loginAs(String username, String password) {
        enterUsername(username)
                .enterPassword(password)
                .clickLogin();
    }

    // ── State Queries ─────────────────────────────────────────────────────────

    @Step("Get login error message text")
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    @Step("Check if error message is displayed")
    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    @Step("Check if login page is loaded")
    public boolean isLoaded() {
        return isDisplayed(loginContainer);
    }
}
