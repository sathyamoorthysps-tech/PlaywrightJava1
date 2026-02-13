# Automation Architecture Document

## Table of Contents
1. [Overview](#overview)
2. [Architecture Diagram](#architecture-diagram)
3. [Framework Layers](#framework-layers)
4. [Project Structure](#project-structure)
5. [Design Patterns](#design-patterns)
6. [Component Details](#component-details)
7. [Data Flow](#data-flow)
8. [Execution Flow](#execution-flow)
9. [Configuration Management](#configuration-management)
10. [Browser Management](#browser-management)
11. [Parallel Execution](#parallel-execution)
12. [Reporting Architecture](#reporting-architecture)

---

## Overview

This document describes the architecture of the Amazon.in Playwright Automation Framework. The framework is built using Java 17+, Playwright, Cucumber BDD, and follows industry best practices for maintainability, scalability, and reliability.

### Key Principles
- **Separation of Concerns:** Clear separation between test logic, page objects, and utilities
- **Reusability:** Reusable components and methods
- **Maintainability:** Easy to update and extend
- **Scalability:** Support for parallel execution and multiple browsers
- **Reliability:** Robust error handling and retry mechanisms

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Test Execution Layer                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Cucumber     │  │   JUnit      │  │   Maven      │     │
│  │  Features     │  │   Runner     │  │   Surefire   │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Step Definitions Layer                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ AddToCart    │  │    Hooks     │  │   API Test  │     │
│  │   Steps      │  │              │  │             │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Page Object Layer                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  BasePage    │  │  HomePage   │  │ ProductPage  │     │
│  │              │  │ SearchResults│  │  CartPage    │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Factory Layer                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Playwright   │  │   Browser    │  │   Context    │     │
│  │   Factory    │  │  Management  │  │  Management   │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Playwright API Layer                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Chromium    │  │   Firefox    │  │   WebKit     │     │
│  │              │  │              │  │              │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

---

## Framework Layers

### 1. Test Execution Layer
**Location:** `src/test/java/com/amazon/runners/`

- **TestRunner.java:** Cucumber JUnit runner configuration
- **Responsibilities:**
  - Feature file discovery
  - Step definition mapping
  - Plugin configuration (Allure, HTML, JSON, XML)
  - Tag filtering

### 2. Step Definitions Layer
**Location:** `src/test/java/com/amazon/stepdefinitions/`

- **AddToCartSteps.java:** Step definitions for add-to-cart scenarios
- **Hooks.java:** Cucumber lifecycle hooks (@Before, @After)
- **Responsibilities:**
  - Mapping Gherkin steps to Java methods
  - Test orchestration
  - Screenshot capture
  - Allure step attachments

### 3. Page Object Layer
**Location:** `src/main/java/com/amazon/pages/`

- **BasePage.java:** Base class with common methods
- **HomePage.java:** Amazon home page interactions
- **SearchResultsPage.java:** Search results page interactions
- **ProductPage.java:** Product detail page interactions
- **CartPage.java:** Shopping cart page interactions

**Responsibilities:**
- Element locators
- Page-specific actions
- Page state verification
- Reusable page methods

### 4. Factory Layer
**Location:** `src/main/java/com/amazon/factory/`

- **PlaywrightFactory.java:** Browser and context management
- **Responsibilities:**
  - Browser initialization
  - Context creation
  - Page creation
  - Thread-safe browser management
  - Resource cleanup

### 5. Configuration Layer
**Location:** `src/main/java/com/amazon/config/`

- **ConfigReader.java:** Configuration management
- **Responsibilities:**
  - Reading `config.properties`
  - Providing configuration values
  - Singleton pattern implementation

### 6. Utilities Layer
**Location:** `src/main/java/com/amazon/utils/`

- **LoggerUtil.java:** Logging utilities
- **Responsibilities:**
  - Logging configuration
  - Log formatting

---

## Project Structure

```
playwright-automation-framework/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── amazon/
│   │               ├── config/
│   │               │   └── ConfigReader.java
│   │               ├── factory/
│   │               │   └── PlaywrightFactory.java
│   │               ├── pages/
│   │               │   ├── BasePage.java
│   │               │   ├── HomePage.java
│   │               │   ├── SearchResultsPage.java
│   │               │   ├── ProductPage.java
│   │               │   └── CartPage.java
│   │               └── utils/
│   │                   └── LoggerUtil.java
│   │
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── amazon/
│       │           ├── api/
│       │           │   └── ApiTest.java
│       │           ├── runners/
│       │           │   └── TestRunner.java
│       │           └── stepdefinitions/
│       │               ├── AddToCartSteps.java
│       │               └── Hooks.java
│       │
│       └── resources/
│           ├── features/
│           │   └── addToCart.feature
│           └── config.properties
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── target/
│   ├── allure-results/
│   ├── cucumber-reports/
│   ├── logs/
│   └── videos/
│
├── pom.xml
├── Jenkinsfile
├── README.md
├── REQUIREMENTS.md
├── AUTOMATION_ARCHITECTURE.md
├── TEST_CASE_GENERATION.md
└── TESTING_GUIDE.md
```

---

## Design Patterns

### 1. Page Object Model (POM)
**Purpose:** Encapsulate page-specific logic and locators

**Implementation:**
- Each page has its own class extending `BasePage`
- Locators are defined as private static final constants
- Methods represent user actions on the page
- Page state verification methods

**Example:**
```java
public class HomePage extends BasePage {
    private static final String SEARCH_BOX = "#twotabsearchtextbox";
    
    public HomePage searchForProduct(String productName) {
        fill(SEARCH_BOX, productName);
        click(SEARCH_BUTTON);
        return this;
    }
}
```

### 2. Factory Pattern
**Purpose:** Centralize browser creation logic

**Implementation:**
- `PlaywrightFactory` creates browser instances
- Supports multiple browser types (Chromium, Firefox, WebKit)
- Configurable browser options

**Example:**
```java
public Page initBrowser() {
    Browser browser = playwright.chromium().launch(options);
    BrowserContext context = browser.newContext(contextOptions);
    return context.newPage();
}
```

### 3. Singleton Pattern
**Purpose:** Single instance of configuration reader

**Implementation:**
- `ConfigReader` uses singleton pattern
- Ensures consistent configuration access

**Example:**
```java
public class ConfigReader {
    private static ConfigReader instance;
    
    public static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }
}
```

### 4. ThreadLocal Pattern
**Purpose:** Thread-safe parallel execution

**Implementation:**
- `PlaywrightFactory` uses ThreadLocal for browser instances
- Each thread has its own browser context
- Prevents thread interference

**Example:**
```java
private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

public Page getPage() {
    return pageThreadLocal.get();
}
```

---

## Component Details

### BasePage
**Purpose:** Common functionality for all page objects

**Key Methods:**
- `navigateTo(String url)` - Navigate to URL
- `click(String selector)` - Click element
- `fill(String selector, String text)` - Fill input field
- `getText(String selector)` - Get element text
- `waitForSelector(String selector)` - Wait for element
- `isVisible(String selector)` - Check element visibility
- `screenshot()` - Capture screenshot

### PlaywrightFactory
**Purpose:** Browser lifecycle management

**Key Methods:**
- `initBrowser()` - Initialize browser, context, and page
- `tearDown()` - Clean up resources
- Browser type selection (Chromium, Firefox, WebKit)
- Viewport configuration
- Video recording setup
- Trace recording setup

### ConfigReader
**Purpose:** Configuration management

**Key Properties:**
- `baseUrl` - Application base URL
- `browser` - Browser type
- `headless` - Headless mode flag
- `defaultTimeout` - Default wait timeout
- `videoRecord` - Video recording flag
- `traceRecord` - Trace recording flag

### Hooks
**Purpose:** Test lifecycle management

**Key Methods:**
- `@Before` - Setup before each scenario
  - Initialize browser
  - Set up logging
  - Attach scenario metadata
- `@After` - Cleanup after each scenario
  - Capture screenshot on failure
  - Close browser
  - Generate trace files
  - Attach artifacts to Allure

---

## Data Flow

```
┌─────────────┐
│ Feature File│
│  (.feature) │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ TestRunner  │
│  (JUnit)    │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Hooks     │
│  (@Before)  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Step Defs   │
│  (Cucumber) │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Page Obj   │
│   Methods   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Playwright  │
│    API      │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Browser   │
│  (Chromium) │
└─────────────┘
```

---

## Execution Flow

### 1. Test Initialization
```
TestRunner → Hooks.@Before → PlaywrightFactory.initBrowser()
```

### 2. Test Execution
```
Feature Step → Step Definition → Page Object Method → Playwright API → Browser
```

### 3. Test Completion
```
Hooks.@After → Screenshot Capture → Trace Generation → Browser Cleanup → Report Generation
```

---

## Configuration Management

### Configuration File
**Location:** `src/test/resources/config.properties`

**Properties:**
```properties
# Application Configuration
base.url=https://www.amazon.in

# Browser Configuration
browser=chromium
headless=false

# Timeouts (ms)
default.timeout=30000
navigation.timeout=60000

# Screenshots
screenshot.on.failure=true

# Video Recording
video.record=false

# Trace Recording
trace.record=false
```

### Environment Variables
- Can override properties via environment variables
- Format: `PROPERTY_NAME=value`

---

## Browser Management

### Browser Initialization
1. Read browser type from config
2. Create Playwright instance
3. Launch browser with options
4. Create browser context
5. Create page instance
6. Set default timeout

### Browser Options
- **Headless Mode:** Configurable
- **Viewport:** 1920x1080 (default)
- **Slow Mo:** 100ms (for debugging)
- **Video Recording:** Optional
- **Trace Recording:** Optional

### Thread Safety
- Each thread has its own browser instance
- ThreadLocal ensures isolation
- No shared state between threads

---

## Parallel Execution

### Configuration
**Location:** `pom.xml`

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

### Execution Strategy
- **Parallel Mode:** Methods (scenarios)
- **Thread Count:** 3 (configurable)
- **Per Core Thread Count:** Enabled

### Thread Isolation
- Each scenario runs in its own thread
- ThreadLocal browser instances
- Independent test execution

---

## Reporting Architecture

### Allure Reports
**Location:** `target/allure-results/`

**Features:**
- Test execution history
- Step-by-step details
- Screenshots on failures
- Video attachments
- Test categorization by tags
- Timeline visualization

**Generate Command:**
```bash
mvn allure:serve
```

### Cucumber Reports
**Location:** `target/cucumber-reports/`

**Formats:**
- HTML: `cucumber.html`
- JSON: `cucumber.json`
- XML: `cucumber.xml`

### Log Files
**Location:** `target/logs/`

**File:** `automation.log`
- DEBUG, INFO, WARN, ERROR levels
- Timestamped entries
- Thread-safe logging

---

## Best Practices

### 1. Page Object Model
- One class per page
- Locators as constants
- Methods represent user actions
- Return page objects for method chaining

### 2. Wait Strategies
- Use explicit waits
- Avoid hard-coded sleeps
- Wait for network idle when needed
- Handle dynamic content

### 3. Error Handling
- Try-catch blocks for critical operations
- Meaningful error messages
- Screenshots on failures
- Retry logic for flaky tests

### 4. Code Organization
- Clear package structure
- Meaningful class and method names
- Javadoc comments
- Consistent formatting

### 5. Test Data
- Externalize test data
- Use data tables in Cucumber
- Avoid hard-coded values
- Support data-driven testing

---

## Future Enhancements

1. **API Testing:** Expand API test coverage
2. **Visual Testing:** Integrate visual regression testing
3. **Performance Testing:** Add performance metrics
4. **Database Testing:** Database validation capabilities
5. **Mobile Testing:** Mobile browser support
6. **Cloud Execution:** Selenium Grid / BrowserStack integration

---

## References

- [Playwright Java Documentation](https://playwright.dev/java/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Allure Framework](https://docs.qameta.io/allure/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
