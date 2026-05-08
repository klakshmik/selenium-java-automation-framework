package framework.utils;

import framework.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Centralised wait utilities built on Selenium's FluentWait.
 *
 * Default timeout and polling interval are read from config.properties,
 * so they can be adjusted per environment without code changes.
 *
 * All methods return the WebElement they waited for, enabling fluent chaining:
 *   wait.untilClickable(By.id("btn")).click();
 */
public class WaitUtils {

    private static final Logger log = LogManager.getLogger(WaitUtils.class);

    private final WebDriver driver;
    private final int defaultTimeoutSecs;
    private final int pollingIntervalMs;

    public WaitUtils(WebDriver driver) {
        this.driver             = driver;
        this.defaultTimeoutSecs = ConfigReader.getInt("wait.timeout.seconds", 15);
        this.pollingIntervalMs  = ConfigReader.getInt("wait.polling.ms", 500);
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    private FluentWait<WebDriver> fluent(int timeoutSecs) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSecs))
                .pollingEvery(Duration.ofMillis(pollingIntervalMs))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    private WebDriverWait explicit(int timeoutSecs) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSecs));
    }

    // ── Visibility ────────────────────────────────────────────────────────────

    public WebElement untilVisible(By locator) {
        log.debug("Waiting for visibility: {}", locator);
        return fluent(defaultTimeoutSecs).until(
                ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement untilVisible(WebElement element) {
        return fluent(defaultTimeoutSecs).until(
                ExpectedConditions.visibilityOf(element));
    }

    public WebElement untilVisible(By locator, int timeoutSecs) {
        return fluent(timeoutSecs).until(
                ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ── Presence ──────────────────────────────────────────────────────────────

    public WebElement untilPresent(By locator) {
        log.debug("Waiting for presence: {}", locator);
        return fluent(defaultTimeoutSecs).until(
                ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ── Clickability ──────────────────────────────────────────────────────────

    public WebElement untilClickable(By locator) {
        log.debug("Waiting for clickable: {}", locator);
        return fluent(defaultTimeoutSecs).until(
                ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement untilClickable(WebElement element) {
        return fluent(defaultTimeoutSecs).until(
                ExpectedConditions.elementToBeClickable(element));
    }

    // ── Invisibility ──────────────────────────────────────────────────────────

    public boolean untilInvisible(By locator) {
        log.debug("Waiting for invisibility: {}", locator);
        return fluent(defaultTimeoutSecs).until(
                ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean untilInvisible(By locator, int timeoutSecs) {
        return fluent(timeoutSecs).until(
                ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ── Text Conditions ───────────────────────────────────────────────────────

    public boolean untilTextPresent(By locator, String text) {
        log.debug("Waiting for text '{}' in: {}", text, locator);
        return fluent(defaultTimeoutSecs).until(
                ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public boolean untilUrlContains(String fragment) {
        log.debug("Waiting for URL to contain: {}", fragment);
        return explicit(defaultTimeoutSecs).until(
                ExpectedConditions.urlContains(fragment));
    }

    // ── Alert ─────────────────────────────────────────────────────────────────

    public Alert untilAlertPresent() {
        return explicit(defaultTimeoutSecs).until(
                ExpectedConditions.alertIsPresent());
    }

    // ── Page Load ─────────────────────────────────────────────────────────────

    /**
     * Waits until document.readyState == "complete".
     * Useful after navigations that don't change the URL.
     */
    public void untilPageLoaded() {
        explicit(30).until(d ->
                ((JavascriptExecutor) d)
                        .executeScript("return document.readyState")
                        .equals("complete"));
    }

    // ── Hard pause (use sparingly) ────────────────────────────────────────────

    /**
     * Use ONLY when absolutely necessary (e.g. waiting for an animation).
     * Always prefer condition-based waits above.
     */
    public void hardWait(long milliseconds) {
        log.warn("Using hardWait({} ms) — consider replacing with a condition-based wait", milliseconds);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
