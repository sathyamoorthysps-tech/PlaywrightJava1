# Testing Guide

## Table of Contents
1. [Overview](#overview)
2. [Getting Started](#getting-started)
3. [Running Tests](#running-tests)
4. [Writing Tests](#writing-tests)
5. [Best Practices](#best-practices)
6. [Debugging](#debugging)
7. [Troubleshooting](#troubleshooting)
8. [CI/CD Integration](#cicd-integration)

---

## Overview

This guide provides comprehensive instructions for writing, executing, and maintaining tests in the Amazon.in Playwright Automation Framework.

---

## Getting Started

### Prerequisites Check
```bash
# Check Java version (should be 17+)
java -version

# Check Maven version (should be 3.6+)
mvn -version

# Check Git version
git --version
```

### Initial Setup
```bash
# Clone repository
git clone <repository-url>
cd playwright-automation-framework

# Install dependencies
mvn clean install

# Verify installation
mvn test -Dtest=TestRunner
```

### Configuration
Edit `src/test/resources/config.properties`:
```properties
base.url=https://www.amazon.in
browser=chromium
headless=false
default.timeout=30000
```

---

## Running Tests

### Basic Test Execution

#### Run All Tests
```bash
mvn test
```

#### Run Specific Test Runner
```bash
mvn test -Dtest=TestRunner
```

#### Run with Specific Tags
```bash
# Run smoke tests
mvn test -Dcucumber.filter.tags="@smoke"

# Run positive tests
mvn test -Dcucumber.filter.tags="@positive"

# Run negative tests
mvn test -Dcucumber.filter.tags="@negative"

# Run multiple tags (AND)
mvn test -Dcucumber.filter.tags="@smoke and @cart"

# Run multiple tags (OR)
mvn test -Dcucumber.filter.tags="@smoke or @regression"
```

#### Run Specific Feature File
```bash
mvn test -Dcucumber.features="src/test/resources/features/addToCart.feature"
```

#### Run Specific Scenario
```bash
mvn test -Dcucumber.features="src/test/resources/features/addToCart.feature" \
        -Dcucumber.filter.tags="@iphone"
```

### Parallel Execution

#### Run Tests in Parallel
```bash
mvn test -Dparallel=methods -DthreadCount=3
```

#### Configure Parallel Execution
Edit `pom.xml`:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>3</threadCount>
        <perCoreThreadCount>true</perCoreThreadCount>
    </configuration>
</plugin>
```

### Browser-Specific Execution

#### Run on Chromium (Default)
```bash
mvn test
```

#### Run on Firefox
```bash
# Update config.properties: browser=firefox
mvn test
```

#### Run on WebKit
```bash
# Update config.properties: browser=webkit
mvn test
```

### Headless Mode

#### Run in Headless Mode
```bash
# Update config.properties: headless=true
mvn test
```

#### Run in Headed Mode (Default)
```bash
# Update config.properties: headless=false
mvn test
```

---

## Writing Tests

### Feature File Structure

```gherkin
@tag1 @tag2
Feature: Feature Name

  As a [user type]
  I want to [action]
  So that [benefit]

  Background:
    Given [common prerequisite]

  @scenario-tag
  Scenario: Scenario Name
    Given [initial context]
    When [action]
    Then [expected outcome]
    And [additional verification]
```

### Step Definitions

#### Create Step Definition Class
```java
package com.amazon.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class MySteps {
    
    @Given("I am on the Amazon.in home page")
    public void iAmOnTheAmazonInHomePage() {
        // Implementation
    }
    
    @When("I search for {string}")
    public void iSearchFor(String productName) {
        // Implementation
    }
    
    @Then("I should see search results")
    public void iShouldSeeSearchResults() {
        // Implementation
    }
}
```

#### Use Page Objects
```java
@When("I search for {string}")
public void iSearchFor(String productName) {
    homePage = new HomePage(page);
    homePage.searchForProduct(productName);
}
```

### Page Object Model

#### Create Page Class
```java
package com.amazon.pages;

public class MyPage extends BasePage {
    
    private static final String ELEMENT_SELECTOR = "#element-id";
    
    public MyPage(Page page) {
        super(page);
    }
    
    public void performAction() {
        click(ELEMENT_SELECTOR);
    }
    
    public boolean verifyElement() {
        return isVisible(ELEMENT_SELECTOR);
    }
}
```

### Data-Driven Testing

#### Scenario Outline
```gherkin
@data-driven
Scenario Outline: Test with multiple data sets
  Given I am on the Amazon.in home page
  When I search for "<product>"
  Then I should see "<expected-result>"
  
  Examples:
    | product           | expected-result |
    | iPhone 17 Pro Max | Search Results  |
    | Laptop            | Search Results  |
    | Book              | Search Results  |
```

### Tags Strategy

#### Tag Categories
- **@smoke** - Critical path tests
- **@regression** - Full regression suite
- **@positive** - Happy path scenarios
- **@negative** - Error scenarios
- **@boundary** - Boundary tests
- **@integration** - End-to-end tests
- **@cart** - Cart functionality
- **@search** - Search functionality

#### Usage Example
```gherkin
@smoke @cart @positive
Scenario: Add product to cart
  # Test steps
```

---

## Best Practices

### 1. Test Design

#### ✅ Do's
- Write clear, descriptive scenario names
- Use Given-When-Then structure
- Keep scenarios atomic and focused
- Use Background for common setup
- Externalize test data
- Use appropriate tags

#### ❌ Don'ts
- Don't write complex conditional logic in scenarios
- Don't hard-code test data
- Don't write long scenarios
- Don't skip verification steps
- Don't use ambiguous step names

### 2. Page Object Model

#### ✅ Do's
- One class per page
- Locators as constants
- Methods represent user actions
- Return page objects for chaining
- Keep page logic separate from test logic

#### ❌ Don't's
- Don't put assertions in page objects
- Don't hard-code waits
- Don't mix page objects
- Don't create deep inheritance hierarchies

### 3. Wait Strategies

#### ✅ Do's
- Use explicit waits
- Wait for network idle when needed
- Use `waitForSelector()` for elements
- Handle dynamic content properly

#### ❌ Don'ts
- Don't use `Thread.sleep()`
- Don't use hard-coded timeouts
- Don't ignore wait failures

### 4. Error Handling

#### ✅ Do's
- Use try-catch for critical operations
- Provide meaningful error messages
- Capture screenshots on failures
- Log errors appropriately

#### ❌ Don'ts
- Don't swallow exceptions
- Don't use generic error messages
- Don't ignore failures

### 5. Code Organization

#### ✅ Do's
- Follow package structure
- Use meaningful names
- Add Javadoc comments
- Keep methods focused
- Reuse common methods

#### ❌ Don'ts
- Don't create god classes
- Don't duplicate code
- Don't use magic numbers
- Don't ignore code style

---

## Debugging

### Enable Debug Logging

#### Update log4j2.xml
```xml
<Logger name="com.amazon" level="DEBUG"/>
```

### Run in Debug Mode
```bash
# Run with debug logging
mvn test -Dlog4j.configurationFile=src/test/resources/log4j2-debug.xml
```

### Screenshots
Screenshots are automatically captured:
- On test failures
- At key steps (configured in step definitions)
- Location: `target/screenshots/`

### Video Recording
Enable in `config.properties`:
```properties
video.record=true
```
Videos saved to: `target/videos/`

### Trace Recording
Enable in `config.properties`:
```properties
trace.record=true
```
Traces saved to: `target/traces/`

### View Trace
```bash
# After test execution
npx playwright show-trace target/traces/trace.zip
```

### Browser DevTools
Run in headed mode to see browser:
```properties
headless=false
```

### Breakpoints
Use IDE debugger:
1. Set breakpoint in step definition
2. Run test in debug mode
3. Step through execution

---

## Troubleshooting

### Common Issues

#### Issue 1: Tests Timeout
**Symptoms:** Tests fail with timeout errors

**Solutions:**
```properties
# Increase timeout in config.properties
default.timeout=60000
navigation.timeout=120000
```

#### Issue 2: Element Not Found
**Symptoms:** `TimeoutException: Waiting for selector`

**Solutions:**
- Verify selector is correct
- Check if element is in iframe
- Wait for element to be visible
- Use explicit waits

#### Issue 3: Browser Not Launching
**Symptoms:** `BrowserType.launch()` fails

**Solutions:**
```bash
# Install Playwright browsers
npx playwright install chromium
npx playwright install firefox
npx playwright install webkit
```

#### Issue 4: Parallel Execution Conflicts
**Symptoms:** Tests interfere with each other

**Solutions:**
- Ensure ThreadLocal is used (already implemented)
- Use unique test data per thread
- Avoid shared state

#### Issue 5: Allure Report Not Generated
**Symptoms:** No Allure report after test execution

**Solutions:**
```bash
# Generate report explicitly
mvn allure:report

# Serve report
mvn allure:serve
```

#### Issue 6: Cucumber Steps Not Found
**Symptoms:** `Step undefined` errors

**Solutions:**
- Verify step definition package in TestRunner
- Check glue path: `glue = {"com.amazon.stepdefinitions"}`
- Ensure step definitions match feature file steps

---

## CI/CD Integration

### GitHub Actions

#### Workflow File
`.github/workflows/ci.yml`

#### Manual Trigger
```bash
# Push to trigger workflow
git push origin main
```

#### View Results
- Go to GitHub Actions tab
- View workflow run
- Download artifacts

### Jenkins

#### Pipeline File
`Jenkinsfile`

#### Run Pipeline
1. Create Jenkins job
2. Point to Jenkinsfile
3. Run pipeline
4. View reports

### Local CI Simulation
```bash
# Clean build
mvn clean test

# Generate reports
mvn allure:report

# Check exit code
echo $?
```

---

## Test Execution Reports

### Allure Reports
```bash
# Generate and serve
mvn allure:serve

# Generate only
mvn allure:report
```

**Location:** `target/allure-results/`

**Features:**
- Test execution history
- Screenshots on failures
- Step-by-step details
- Test categorization

### Cucumber Reports
**Location:** `target/cucumber-reports/`

**Formats:**
- HTML: `cucumber.html`
- JSON: `cucumber.json`
- XML: `cucumber.xml`

### Logs
**Location:** `target/logs/automation.log`

**Levels:**
- DEBUG: Detailed debugging information
- INFO: General information
- WARN: Warning messages
- ERROR: Error messages

---

## Performance Optimization

### Parallel Execution
```bash
# Run 3 tests in parallel
mvn test -DthreadCount=3
```

### Headless Mode
```properties
# Faster execution
headless=true
```

### Selective Execution
```bash
# Run only smoke tests
mvn test -Dcucumber.filter.tags="@smoke"
```

---

## Maintenance

### Regular Tasks
1. **Update Dependencies:** Check for updates monthly
2. **Review Tests:** Review and update tests quarterly
3. **Clean Reports:** Clean old reports periodically
4. **Update Documentation:** Keep docs up to date

### Code Review Checklist
- [ ] Tests follow naming conventions
- [ ] Page objects are properly structured
- [ ] Error handling is implemented
- [ ] Logging is appropriate
- [ ] Documentation is updated
- [ ] All tests pass
- [ ] Code is formatted consistently

---

## References

- [Playwright Java Docs](https://playwright.dev/java/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Allure Framework](https://docs.qameta.io/allure/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)

---

## Quick Reference

### Common Commands
```bash
# Run tests
mvn test

# Run with tags
mvn test -Dcucumber.filter.tags="@smoke"

# Generate Allure report
mvn allure:serve

# Clean and test
mvn clean test

# Install dependencies
mvn clean install
```

### Common Tags
- `@smoke` - Smoke tests
- `@regression` - Regression tests
- `@positive` - Positive tests
- `@negative` - Negative tests
- `@cart` - Cart tests
- `@search` - Search tests

### Configuration Properties
- `base.url` - Application URL
- `browser` - Browser type
- `headless` - Headless mode
- `default.timeout` - Default timeout
- `video.record` - Video recording
- `trace.record` - Trace recording

---

**Last Updated:** February 12, 2026
