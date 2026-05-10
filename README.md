# 🧪 Selenium Java Automation Framework

![CI](https://github.com/klakshmik/selenium-java-automation-framework/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Selenium](https://img.shields.io/badge/Selenium-4.18-green?logo=selenium)
![TestNG](https://img.shields.io/badge/TestNG-7.9-blue)
![Allure](https://img.shields.io/badge/Allure-2.25-yellow)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

A **production-grade, thread-safe** test automation framework built with Java, Selenium 4, TestNG, and Allure Reports — following the Page Object Model pattern and designed for easy CI/CD integration.

---

## ✨ Features

| Feature | Details |
|---|---|
| **Page Object Model** | Clean separation of locators, actions, and assertions |
| **Thread-safe WebDriver** | `ThreadLocal` driver management for parallel test execution |
| **Smart Waits** | `FluentWait` wrappers — no `Thread.sleep()` anti-patterns |
| **Multi-browser** | Chrome, Firefox, Edge via WebDriverManager (no manual driver downloads) |
| **Headless CI mode** | Auto-detects `HEADLESS=true` env variable |
| **Allure Reports** | Steps, screenshots on failure, severity, and story mapping |
| **Data-driven Tests** | TestNG `@DataProvider` for parameterised scenarios |
| **Config Management** | Properties file + environment variable override |
| **GitHub Actions CI** | Matrix pipeline runs Chrome + Firefox on every push |

---

## 🏗️ Project Structure

```
selenium-java-automation-framework/
│
├── .github/
│   └── workflows/
│       └── ci.yml                    # GitHub Actions pipeline
│
├── src/
│   ├── main/java/framework/
│   │   ├── base/
│   │   │   └── BasePage.java         # POM base: common actions + Allure steps
│   │   ├── config/
│   │   │   └── ConfigReader.java     # Properties + env var config loader
│   │   ├── drivers/
│   │   │   └── DriverManager.java    # ThreadLocal WebDriver factory
│   │   └── utils/
│   │       └── WaitUtils.java        # FluentWait helpers
│   │
│   └── test/java/
│       ├── pages/
│       │   └── LoginPage.java        # Example Page Object
│       └── tests/
│           ├── BaseTest.java         # @BeforeMethod/@AfterMethod + screenshot
│           └── LoginTest.java        # Sample test suite (positive + negative)
│
├── src/test/resources/
│   ├── config.properties             # Environment config
│   └── testng.xml                    # Suite definition
│
└── pom.xml                           # Maven dependencies
```

---

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Chrome / Firefox installed (or use headless mode)

### Clone & Run

```bash
git clone https://github.com/klakshmik/selenium-java-automation-framework.git
cd selenium-java-automation-framework

# Run all tests (Chrome, headed)
mvn clean test

# Run headless
HEADLESS=true mvn clean test

# Run on Firefox
mvn clean test -Dbrowser=firefox

# Generate Allure report
mvn allure:serve
```

---

## 📊 Allure Report

After running tests, generate and open the report:

```bash
mvn allure:serve
```

The report includes:
- ✅ Test results by feature and story
- 📸 Automatic screenshots on failure
- 📋 Step-by-step execution log
- 🔥 Severity breakdown (Blocker → Minor)

![Allure Report Preview](https://allurereport.org/assets/img/allure-report.jpg)

---

## ⚙️ Configuration

All settings live in `src/test/resources/config.properties`:

```properties
base.url=https://www.saucedemo.com
browser=chrome
wait.timeout.seconds=15
wait.polling.ms=500
```

**Override any value with an environment variable:**

```bash
# Key mapping: base.url → BASE_URL, browser → BROWSER
export BASE_URL=https://staging.myapp.com
export BROWSER=firefox
mvn clean test
```

This makes it trivial to point the suite at different environments without touching code.

---

## 🔄 CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/ci.yml`) runs on every push to `main` and on daily schedule:

```
Push to main / PR
        │
        ▼
┌───────────────┐    ┌─────────────────┐
│  Chrome Tests │    │  Firefox Tests  │
│  (headless)   │    │  (headless)     │
└───────┬───────┘    └────────┬────────┘
        │                     │
        └──────────┬──────────┘
                   ▼
          Allure Reports uploaded
          as build artifacts
```

---

## 🧩 Adding a New Page Object

1. Create `src/test/java/pages/YourPage.java` extending `BasePage`
2. Define private `By` locators
3. Expose public action methods with `@Step` annotations

```java
public class DashboardPage extends BasePage {

    private final By welcomeMessage = By.cssSelector(".welcome-banner");
    private final By logoutButton   = By.id("logout-btn");

    @Step("Get welcome message text")
    public String getWelcomeMessage() {
        return getText(welcomeMessage);
    }

    @Step("Click Logout")
    public LoginPage logout() {
        click(logoutButton);
        return new LoginPage();
    }
}
```

---

## 🧵 Parallel Execution

Parallel mode is configured in `testng.xml`:

```xml
<suite name="Suite" parallel="methods" thread-count="2">
```

The `ThreadLocal<WebDriver>` in `DriverManager` ensures each thread gets its own browser instance — no shared state, no race conditions.

---

## 📦 Tech Stack

| Tool | Version | Purpose |
|---|---------|---|
| Java | 17      | Language |
| Selenium | 4.18.1  | Browser automation |
| TestNG | 7.9.0   | Test runner + assertions |
| Allure | 2.25.0  | Reporting |
| WebDriverManager | 5.7.0   | Automatic driver management |
| Log4j2 | 2.23.0  | Logging |
| JavaFaker | 1.0.2   | Test data generation |
| Maven | 3.8+    | Build & dependency management |
| GitHub Actions | —       | CI/CD pipeline |

---

## 📄 License

MIT — free to use, adapt, and extend.

---

*Built by [Lakshmi Kuchimanchi](https://www.linkedin.com/in/klakshmik) · SDET with 7+ years in enterprise test automation*
