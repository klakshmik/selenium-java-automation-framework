package framework.drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Thread-safe WebDriver factory using ThreadLocal.
 * Supports Chrome, Firefox, and Edge — headless mode for CI.
 *
 * Usage:
 *   DriverManager.initDriver("chrome");
 *   WebDriver driver = DriverManager.getDriver();
 *   DriverManager.quitDriver();
 */
public class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {
        // Utility class — no instantiation
    }

    /**
     * Initialise a WebDriver instance for the given browser.
     * Respects the HEADLESS env variable (set to "true" in CI).
     */
    public static void initDriver(String browser) {
        boolean headless = Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "false"));
        log.info("Initialising {} driver (headless={})", browser, headless);

        WebDriver driver = switch (browser.toLowerCase().trim()) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) options.addArguments("--headless");
                yield new FirefoxDriver(options);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) options.addArguments("--headless");
                yield new EdgeDriver(options);
            }
            default -> {  // chrome
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) options.addArguments("--headless=new");
                options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
                yield new ChromeDriver(options);
            }
        };

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
        log.info("Driver initialised successfully: {}", driver.getClass().getSimpleName());
    }

    /**
     * Returns the WebDriver for the current thread.
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("Driver not initialised. Call DriverManager.initDriver() first.");
        }
        return driver;
    }

    /**
     * Quits the driver and removes it from the thread-local store.
     * Always call this in @AfterMethod to prevent browser leaks.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            log.info("Driver quit and removed from ThreadLocal.");
        }
    }
}
