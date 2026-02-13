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
| Logging | Log4j2 | 2.24.3 |
| Build Tool | Maven | 3.x |

## 📁 Project Structure

```
playwright-automation-framework/
├── src/
│   ├── main/java/com/amazon/
│   │   ├── config/          # Configuration management
│   │   ├── factory/         # Browser factory
│   │   ├── pages/           # Page Object Model classes
│   │   └── utils/           # Utility classes
│   └── test/java/com/amazon/
│       ├── api/             # API tests
│       ├── runners/         # Test runners
│       └── stepdefinitions/ # Cucumber step definitions
├── src/test/resources/
│   ├── features/            # Cucumber feature files
│   └── config.properties    # Configuration file
├── .github/workflows/       # GitHub Actions CI/CD
├── Jenkinsfile             # Jenkins pipeline
└── pom.xml                 # Maven configuration
```

## 🚦 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd playwright-automation-framework
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Configure Settings
Edit `src/test/resources/config.properties`:
```properties
base.url=https://www.amazon.in
browser=chromium
headless=false
default.timeout=30000
```

### 4. Run Tests
```bash
# Run all tests
mvn test

# Run specific test runner
mvn test -Dtest=TestRunner

# Run with specific tags
mvn test -Dcucumber.filter.tags="@smoke"

# Run in parallel
mvn test -Dparallel=methods -DthreadCount=3
```

### 5. Generate Reports
```bash
# Generate Allure report
mvn allure:serve

# Generate Cucumber HTML report
# Reports available at: target/cucumber-reports/cucumber.html
```

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
```bash
# Run smoke tests
mvn test -Dcucumber.filter.tags="@smoke"

# Run positive tests
mvn test -Dcucumber.filter.tags="@positive"

# Run multiple tags
mvn test -Dcucumber.filter.tags="@smoke and @cart"
```

### Run Specific Feature
```bash
mvn test -Dcucumber.features="src/test/resources/features/addToCart.feature"
```

### Parallel Execution
```bash
mvn test -Dparallel=methods -DthreadCount=3
```

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

### Browser Configuration
Edit `src/test/resources/config.properties`:
```properties
# Browser options
browser=chromium          # Options: chromium, firefox, webkit
headless=false           # true/false
default.timeout=30000    # milliseconds
```

### Parallel Execution
Configure in `pom.xml`:
```xml
<parallel>methods</parallel>
<threadCount>3</threadCount>
```

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
  - `PlaywrightFactory.java` - Browser management

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

## 🔄 CI/CD Integration

### GitHub Actions
Workflow file: `.github/workflows/ci.yml`

**Triggers:**
- Push to main branch
- Pull requests

**Jobs:**
- Build
- Test
- Generate reports

### Jenkins
Pipeline file: `Jenkinsfile`

**Stages:**
- Checkout
- Build
- Test
- Publish Reports

## 🐛 Troubleshooting

### Common Issues

**Issue:** Tests fail with timeout
```bash
# Solution: Increase timeout in config.properties
default.timeout=60000
```

**Issue:** Browser not launching
```bash
# Solution: Install Playwright browsers
npx playwright install chromium
```

**Issue:** Parallel execution conflicts
```bash
# Solution: Ensure ThreadLocal is used in PlaywrightFactory
# Already implemented in the framework
```

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

**Last Updated:** February 12, 2026  
**Version:** 1.0-SNAPSHOT
