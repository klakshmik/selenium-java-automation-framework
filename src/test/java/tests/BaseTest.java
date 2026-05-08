package tests;

import framework.config.ConfigReader;
import framework.drivers.DriverManager;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Base class for all test classes.
 *
 * Responsibilities:
 * - Initialise and tear down WebDriver per test method (thread-safe)
 * - Navigate to base URL before each test
 * - Capture screenshot on failure and attach to Allure report
 * - Read browser from testng.xml parameter (defaults to config.properties)
 */
public class BaseTest {

    protected WebDriver driver;
    private static final Logger log = LogManager.getLogger(BaseTest.class);

    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(@Optional String browser) {
        String resolvedBrowser = (browser != null && !browser.isBlank())
                ? browser
                : ConfigReader.get("browser", "chrome");

        log.info("===== Starting test on: {} =====", resolvedBrowser);
        DriverManager.initDriver(resolvedBrowser);
        driver = DriverManager.getDriver();
        driver.get(ConfigReader.get("base.url"));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("Test FAILED: {}", result.getName());
            attachScreenshot(result.getName());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            log.info("Test PASSED: {}", result.getName());
        } else {
            log.warn("Test SKIPPED: {}", result.getName());
        }
        DriverManager.quitDriver();
    }

    // ── Allure Attachment ─────────────────────────────────────────────────────

    @Attachment(value = "Screenshot on failure: {testName}", type = "image/png")
    private byte[] attachScreenshot(String testName) {
        try {
            return ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error("Could not capture screenshot: {}", e.getMessage());
            return new byte[0];
        }
    }
}
