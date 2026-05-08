package framework.base;

import framework.drivers.DriverManager;
import framework.utils.WaitUtils;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * Base class for all Page Objects.
 *
 * Provides:
 * - Selenium fluent wait wrappers (via WaitUtils)
 * - Common actions: click, type, select, hover, scroll, screenshot
 * - Allure @Step annotations for automatic report steps
 * - PageFactory initialisation
 *
 * All page classes should extend this.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitUtils wait;
    private static final Logger log = LogManager.getLogger(BasePage.class);

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait   = new WaitUtils(driver);
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Step("Navigate to URL: {url}")
    public void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ── Core Actions ──────────────────────────────────────────────────────────

    @Step("Click element: {locator}")
    protected void click(By locator) {
        log.debug("Clicking: {}", locator);
        wait.untilClickable(locator).click();
    }

    @Step("Click element")
    protected void click(WebElement element) {
        log.debug("Clicking WebElement: {}", element);
        wait.untilClickable(element).click();
    }

    @Step("Type '{text}' into element: {locator}")
    protected void type(By locator, String text) {
        log.debug("Typing '{}' into: {}", text, locator);
        WebElement el = wait.untilVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    @Step("Clear and type '{text}' into element")
    protected void type(WebElement element, String text) {
        wait.untilVisible(element).clear();
        element.sendKeys(text);
    }

    @Step("Get text of element: {locator}")
    protected String getText(By locator) {
        return wait.untilVisible(locator).getText().trim();
    }

    @Step("Get attribute '{attribute}' of element: {locator}")
    protected String getAttribute(By locator, String attribute) {
        return wait.untilPresent(locator).getAttribute(attribute);
    }

    @Step("Check if element is displayed: {locator}")
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    // ── Dropdowns ─────────────────────────────────────────────────────────────

    @Step("Select dropdown option '{visibleText}' by visible text")
    protected void selectByVisibleText(By locator, String visibleText) {
        log.debug("Selecting '{}' from dropdown: {}", visibleText, locator);
        new Select(wait.untilVisible(locator)).selectByVisibleText(visibleText);
    }

    @Step("Select dropdown option by value '{value}'")
    protected void selectByValue(By locator, String value) {
        new Select(wait.untilVisible(locator)).selectByValue(value);
    }

    // ── Advanced Interactions ─────────────────────────────────────────────────

    @Step("Hover over element: {locator}")
    protected void hover(By locator) {
        new Actions(driver).moveToElement(wait.untilVisible(locator)).perform();
    }

    @Step("Scroll element into view: {locator}")
    protected void scrollIntoView(By locator) {
        WebElement el = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
    }

    @Step("Press ENTER key on element: {locator}")
    protected void pressEnter(By locator) {
        wait.untilVisible(locator).sendKeys(Keys.ENTER);
    }

    // ── Screenshots ───────────────────────────────────────────────────────────

    /**
     * Captures a screenshot as bytes (used by Allure listener automatically,
     * but can also be called manually).
     */
    public byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    // ── JavaScript Helpers ────────────────────────────────────────────────────

    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    protected void jsClick(By locator) {
        WebElement el = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }
}
