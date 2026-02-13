# Amazon.in Playwright Automation Framework

A comprehensive end-to-end test automation framework for Amazon.in e-commerce platform built with Java 17+, Playwright, Cucumber BDD, and Allure Reporting.

## 🚀 Features

- **BDD Framework:** Cucumber-based behavior-driven development
- **Multi-Browser Support:** Chromium, Firefox, WebKit
- **Parallel Execution:** Thread-safe parallel test execution
- **Rich Reporting:** Allure reports with screenshots and videos
- **Page Object Model:** Maintainable and scalable test structure
- **CI/CD Ready:** GitHub Actions and Jenkins integration
- **API Testing:** Playwright APIRequestContext support
- **Comprehensive Logging:** Log4j2 with detailed logs

## 📋 Prerequisites

- **Java:** JDK 17 or higher
- **Maven:** 3.6+ 
- **Node.js:** (Optional, for Playwright CLI tools)
- **Git:** For version control

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17+ |
| Automation Tool | Playwright | 1.49.0 |
| BDD Framework | Cucumber | 7.20.1 |
| Test Runner | JUnit | 4.13.2 |
| Reporting | Allure | 2.29.1 |
| Allure Maven Plugin | allure-maven | 2.14.0 |
| Logging | Log4j2 | 2.24.3 |
| JSON (test data) | Jackson | 2.18.2 |
| Build Tool | Maven | 3.x (wrapper: mvnw) |

## 📁 Project Structure

```
PlaywrightJava/
├── src/
│   ├── main/java/com/amazon/
│   │   ├── config/          # ConfigReader, TestDataReader
│   │   ├── factory/         # PlaywrightFactory (browser management)
│   │   ├── pages/           # Page Object Model (BasePage, HomePage, SearchResultsPage, ProductPage, CartPage)
│   │   └── utils/           # LoggerUtil and helpers
│   └── test/java/com/amazon/
│       ├── api/             # API tests (Playwright APIRequestContext)
│       ├── plugins/         # FlakyAndRcaPlugin (flaky detection & RCA report)
│       ├── runners/         # TestRunner (Cucumber JUnit runner)
│       └── stepdefinitions/ # Cucumber step definitions & Hooks
├── src/test/resources/
│   ├── features/            # Cucumber feature files (e.g. addToCart.feature)
│   ├── testdata/            # JSON test data (e.g. add-to-cart-data.json)
│   ├── config.properties    # Base URL, browser, timeouts, screenshots, testdata.path
│   ├── allure.properties    # Allure report config
│   └── log4j2.xml           # Log4j2 configuration
├── .github/workflows/       # GitHub Actions CI (ci.yml)
├── Jenkinsfile             # Jenkins pipeline
├── pom.xml                  # Maven configuration
├── mvnw, mvnw.cmd           # Maven Wrapper
├── set-java-maven-env.ps1   # PowerShell env setup (Windows)
├── INSTALL_JDK_MAVEN.md     # JDK & Maven install guide
└── MCP_CONFIGURATION.md     # MCP / Cursor configuration
```

## 🚦 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/sathyamoorthysps-tech/PlaywrightJava1.git
cd PlaywrightJava1
```
Or use the Maven Wrapper: `./mvnw` (Linux/macOS) or `mvnw.cmd` (Windows) instead of `mvn`.

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Install Playwright Browsers (required before first run)
```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"
```

### 4. Configure Settings
Edit `src/test/resources/config.properties`:
```properties
base.url=https://www.amazon.in
browser=chromium
headless=false
default.timeout=30000
navigation.timeout=60000
screenshot.on.failure=true
video.record=false
trace.record=false
testdata.path=testdata/add-to-cart-data.json
```
Override at runtime: `-Dbrowser=firefox -Dheadless=true` or `-Dtestdata.path=testdata/my-data.json`. CI can set `TESTDATA_PATH` env var.

### 5. Run Tests
The default runner (`TestRunner`) is configured with tag `@addToCart`. Tests run in a single thread by default.
```bash
# Run default suite (@addToCart)
mvn test

# Run specific test runner
mvn test -Dtest=TestRunner

# Run with specific tags
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@positive and @addToCart"

# Run with browser/headless override
mvn test -Dbrowser=firefox -Dheadless=true
```
For multi-browser runs, use the GitHub Actions matrix or run the above with different `-Dbrowser=` values.

### 6. Generate Reports
```bash
# Generate and open Allure report
mvn allure:serve

# Generate report only (no browser)
mvn allure:report
```
Cucumber reports are generated automatically: `target/cucumber-reports/cucumber.html`, `cucumber.json`, `cucumber.xml`.

## 📝 Writing Tests

### Feature File Example
```gherkin
@smoke @cart
Feature: Add Product to Cart

  Scenario: Add iPhone to cart
    Given I am on the Amazon.in home page
    When I search for "iPhone 17 Pro Max"
    Then I should see search results
    When I click on the product "iPhone 17 Pro Max" in same tab
    And I click on Add to Cart button
    Then the product should be added to the cart successfully
    And the cart count should be 1
```

### Step Definition Example
```java
@When("I search for {string}")
public void iSearchFor(String productName) {
    homePage.searchForProduct(productName);
}
```

## 🎯 Test Execution

### Run All Tests
```bash
mvn test
```

### Run by Tags
Available tags in features include `@smoke`, `@cart`, `@positive`, `@negative`, `@addToCart`, `@optimized`, `@dataDriven`, `@json`, `@edge`, `@boundary`, `@iphone`, etc.
```bash
# Run smoke tests
mvn test -Dcucumber.filter.tags="@smoke"

# Run positive add-to-cart tests
mvn test -Dcucumber.filter.tags="@positive and @addToCart"

# Run multiple tags
mvn test -Dcucumber.filter.tags="@smoke and @cart"
```

### Run Specific Feature
```bash
mvn test -Dcucumber.features="src/test/resources/features/addToCart.feature"
```

### Browser / Headless Override
```bash
mvn test -Dbrowser=webkit -Dheadless=true
```
CI runs tests in a matrix (chromium, firefox, webkit) with headless mode.

## 📊 Reporting

### Allure Reports
```bash
# Generate and serve Allure report
mvn allure:serve

# Generate report only
mvn allure:report
```

**Report Location:** `target/allure-results/`

### Cucumber Reports
**Location:** `target/cucumber-reports/`
- HTML: `cucumber.html`
- JSON: `cucumber.json`
- XML: `cucumber.xml`

### Logs
**Location:** `target/logs/automation.log`

## 🔧 Configuration

### Browser and Test Data
Edit `src/test/resources/config.properties`:
```properties
browser=chromium          # Options: chromium, firefox, webkit
headless=false           # true/false
default.timeout=30000    # milliseconds
screenshot.on.failure=true
video.record=false
trace.record=false
testdata.path=testdata/add-to-cart-data.json
```
Override via system properties: `-Dbrowser=firefox -Dheadless=true -Dtestdata.path=...` or env `TESTDATA_PATH`. Default test execution is single-threaded; CI uses a browser matrix for multi-browser runs.

## 🏗️ Architecture

The framework follows **Page Object Model (POM)** pattern:

- **Pages:** `src/main/java/com/amazon/pages/`
  - `BasePage.java` - Base class with common methods
  - `HomePage.java` - Home page interactions
  - `SearchResultsPage.java` - Search results page
  - `ProductPage.java` - Product detail page
  - `CartPage.java` - Shopping cart page

- **Step Definitions:** `src/test/java/com/amazon/stepdefinitions/`
  - `AddToCartSteps.java` - Add to cart step definitions
  - `Hooks.java` - Cucumber lifecycle hooks

- **Factory:** `src/main/java/com/amazon/factory/`
  - `PlaywrightFactory.java` - Browser management (ThreadLocal for thread safety)

- **Config:** `src/main/java/com/amazon/config/`
  - `ConfigReader.java` - Reads config.properties
  - `TestDataReader.java` - Loads JSON test data for data-driven scenarios

- **Plugins:** `src/test/java/com/amazon/plugins/`
  - `FlakyAndRcaPlugin.java` - Flaky scenario detection and Root Cause Analysis report

- **API:** `src/test/java/com/amazon/api/`
  - `ApiTest.java` - Playwright APIRequestContext API tests

For detailed architecture, see [AUTOMATION_ARCHITECTURE.md](AUTOMATION_ARCHITECTURE.md)

## 🧪 Test Case Generation

Use AI prompts to generate test cases. See [TEST_CASE_GENERATION.md](TEST_CASE_GENERATION.md) for templates and examples.

### Example Prompt:
```
Generate positive test cases for product search on Amazon.in.
Use Gherkin BDD format with @positive and @search tags.
Include Background steps and verification steps.
```

## 📚 Documentation

- **[REQUIREMENTS.md](REQUIREMENTS.md)** - Project requirements and specifications
- **[AUTOMATION_ARCHITECTURE.md](AUTOMATION_ARCHITECTURE.md)** - Detailed architecture documentation
- **[TEST_CASE_GENERATION.md](TEST_CASE_GENERATION.md)** - Guide for generating test cases
- **[TESTING_GUIDE.md](TESTING_GUIDE.md)** - Testing best practices and guidelines
- **[INSTALL_JDK_MAVEN.md](INSTALL_JDK_MAVEN.md)** - Install JDK 17 and Maven on Windows
- **[MCP_CONFIGURATION.md](MCP_CONFIGURATION.md)** - MCP / Cursor configuration

## 🔄 CI/CD Integration

### GitHub Actions
Workflow file: `.github/workflows/ci.yml`

**Triggers:** Push to `main` or `develop`; pull requests to `main`; `workflow_dispatch`.

**Jobs:** Runs tests in a matrix (chromium, firefox, webkit) with JDK 17, installs Playwright browsers, runs `mvn clean test -Dbrowser=... -Dheadless=true`, then generates Allure report and uploads artifacts (allure-results, cucumber-reports, logs).

### Jenkins
Pipeline file: `Jenkinsfile`

**Parameters:** `BROWSER` (chromium/firefox/webkit), `HEADLESS` (default true).

**Stages:** Checkout → Install Dependencies (`mvn clean install -DskipTests`) → Install Playwright Browsers → Run Tests (`mvn test -Dbrowser=... -Dheadless=...`) → Generate Allure Report. Post: publish Allure results, archive cucumber reports and logs.

## 🐛 Troubleshooting

### Common Issues

**Issue:** Tests fail with timeout
```bash
# Solution: Increase timeout in config.properties
default.timeout=60000
```

**Issue:** Browser not launching
```bash
# Solution: Install Playwright browsers (use Maven exec)
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"
```

**Issue:** Allure attachments not showing (e.g. on Java 25+)
The project uses an Allure AspectJ profile for JDK 17–22; on Java 25+ the agent is skipped to avoid class version errors. Allure report still generates; for full attachment support use JDK 17.

## 📈 Best Practices

1. **Page Object Model:** Keep page logic in page classes
2. **Explicit Waits:** Use explicit waits instead of hard sleeps
3. **Data Externalization:** Externalize test data
4. **Tag Strategy:** Use consistent tagging strategy
5. **Error Handling:** Implement proper error handling
6. **Screenshots:** Capture screenshots on failures
7. **Logging:** Use appropriate log levels
8. **Code Review:** Review code before committing

## 🤝 Contributing

1. Create a feature branch
2. Write tests for new features
3. Ensure all tests pass
4. Update documentation
5. Submit a pull request

## 📄 License

This project is for internal use only.

## 👥 Team

- **Framework:** Playwright Java Automation Team
- **Maintained by:** QA Automation Team

## 📞 Support

For issues or questions:
- Check documentation files
- Review logs in `target/logs/`
- Check Allure reports for detailed failure information

## 🔗 Useful Links

- [Playwright Documentation](https://playwright.dev/java/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Allure Framework](https://docs.qameta.io/allure/)
- [Maven Documentation](https://maven.apache.org/)

---

**Last Updated:** February 13, 2026  
**Version:** 1.0-SNAPSHOT
