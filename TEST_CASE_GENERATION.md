# Test Case Generation Guide

## Table of Contents
1. [Overview](#overview)
2. [Test Case Types](#test-case-types)
3. [BDD Test Case Format](#bdd-test-case-format)
4. [Prompt Templates](#prompt-templates)
5. [Test Case Examples](#test-case-examples)
6. [Best Practices](#best-practices)
7. [Test Case Template](#test-case-template)

---

## Overview

This guide provides templates and prompts for generating test cases for the Amazon.in automation framework. Use these templates with AI assistants (like ChatGPT, Claude, or Cursor) to generate comprehensive test scenarios.

---

## Test Case Types

### 1. Positive Test Cases
- **Purpose:** Verify that the system works as expected under normal conditions
- **Focus:** Happy path scenarios
- **Example:** User successfully adds a product to cart

### 2. Negative Test Cases
- **Purpose:** Verify error handling and edge cases
- **Focus:** Invalid inputs, error scenarios
- **Example:** User tries to add out-of-stock product

### 3. Boundary Test Cases
- **Purpose:** Test limits and boundaries
- **Focus:** Minimum/maximum values, edge conditions
- **Example:** Add maximum quantity of items to cart

### 4. Integration Test Cases
- **Purpose:** Verify interactions between components
- **Focus:** End-to-end workflows
- **Example:** Complete purchase flow

---

## BDD Test Case Format

### Gherkin Syntax
```gherkin
Feature: Feature Name
  As a [user type]
  I want to [action]
  So that [benefit]

  Background:
    Given [prerequisite]

  @tag1 @tag2
  Scenario: Scenario Name
    Given [initial context]
    When [action]
    Then [expected outcome]
    And [additional verification]
```

---

## Prompt Templates

### Template 1: Generate Positive Test Cases

```
Generate positive test cases for [feature name] on Amazon.in e-commerce platform.

Requirements:
- Use Gherkin BDD format
- Include Background steps
- Add appropriate tags (@positive, @smoke, @regression)
- Cover all happy path scenarios
- Include verification steps

Feature: [Feature Name]
Base URL: https://www.amazon.in

Generate test cases for:
1. [Scenario 1]
2. [Scenario 2]
3. [Scenario 3]
```

### Template 2: Generate Negative Test Cases

```
Generate negative test cases for [feature name] on Amazon.in.

Requirements:
- Use Gherkin BDD format
- Focus on error scenarios and invalid inputs
- Add @negative tag
- Include error message verification
- Test edge cases

Scenarios to cover:
1. Invalid input handling
2. Error message display
3. System behavior on failures
4. Boundary violations
```

### Template 3: Generate Boundary Test Cases

```
Generate boundary test cases for [feature name] on Amazon.in.

Requirements:
- Test minimum and maximum values
- Test edge conditions
- Add @boundary tag
- Verify system behavior at limits

Boundaries to test:
1. Minimum values
2. Maximum values
3. Boundary conditions
4. Edge cases
```

### Template 4: Generate Complete Test Suite

```
Generate a complete test suite for [feature name] on Amazon.in e-commerce platform.

Include:
1. Positive test cases (happy path)
2. Negative test cases (error scenarios)
3. Boundary test cases (limits and edges)
4. Integration test cases (end-to-end flows)

Format: Gherkin BDD
Tags: @positive, @negative, @boundary, @integration
Base URL: https://www.amazon.in

Feature: [Feature Name]
  As a [user type]
  I want to [action]
  So that [benefit]
```

---

## Test Case Examples

### Example 1: Add to Cart (Positive)

```gherkin
@smoke @cart @positive @addToCart
Scenario: Add product to cart successfully
  Given I am on the Amazon.in home page
  When I search for "iPhone 17 Pro Max"
  Then I should see search results
  When I click on the product "iPhone 17 Pro Max" in same tab
  And I click on Add to Cart button
  Then the product should be added to the cart successfully
  And I click on item in cart link
  And the product "iPhone 17 Pro Max" should be available in cart
  And the cart count should be 1
```

### Example 2: Search (Negative)

```gherkin
@negative @search
Scenario: Search with empty query should show error
  Given I am on the Amazon.in home page
  When I search for ""
  Then I should see an error message "Please enter a search keyword"
  And search results should not be displayed
```

### Example 3: Cart Quantity (Boundary)

```gherkin
@boundary @cart
Scenario: Add maximum quantity of items to cart
  Given I am on the Amazon.in home page
  When I search for "iPhone 17 Pro Max"
  And I click on the product "iPhone 17 Pro Max" in same tab
  And I set quantity to 999
  And I click on Add to Cart button
  Then the cart count should be 999
  And I should see a warning message about maximum quantity
```

### Example 4: Product Selection (Integration)

```gherkin
@integration @e2e
Scenario: Complete product selection and cart flow
  Given I am on the Amazon.in home page
  When I search for "laptop"
  Then I should see search results
  When I filter results by "Brand: Dell"
  And I sort results by "Price: Low to High"
  And I click on the first product
  Then product details page should be displayed
  When I click on Add to Cart button
  Then the product should be added to the cart successfully
  And I click on item in cart link
  Then cart page should display the selected product
  And product price should match the product page
```

---

## Best Practices

### 1. Naming Conventions
- **Feature Names:** Clear and descriptive
- **Scenario Names:** Action-oriented, specific
- **Step Names:** Use Given-When-Then format
- **Tags:** Use consistent tag naming

### 2. Scenario Structure
- **Given:** Set up initial state
- **When:** Perform action
- **Then:** Verify outcome
- **And/But:** Additional steps

### 3. Tag Strategy
- **@smoke:** Critical path tests
- **@regression:** Full regression suite
- **@positive:** Happy path scenarios
- **@negative:** Error scenarios
- **@boundary:** Boundary tests
- **@integration:** End-to-end tests
- **@cart:** Cart-related tests
- **@search:** Search-related tests

### 4. Data Management
- Use data tables for multiple test data
- Externalize test data when possible
- Use scenario outlines for data-driven tests

### 5. Maintainability
- Keep scenarios focused and atomic
- Avoid complex conditional logic
- Use Background for common setup
- Reuse step definitions

---

## Test Case Template

### Standard Template

```gherkin
@[tag1] @[tag2]
Feature: [Feature Name]

  As a [user type]
  I want to [action]
  So that [benefit]

  Background:
    Given [common prerequisite]

  @[scenario-tag]
  Scenario: [Scenario Name]
    Given [initial context]
    When [action is performed]
    Then [expected outcome]
    And [additional verification]

  @[scenario-tag]
  Scenario Outline: [Scenario Name with Data]
    Given [initial context]
    When [action with <parameter>]
    Then [expected outcome]
    
    Examples:
      | parameter | expected |
      | value1    | result1  |
      | value2    | result2  |
```

### Detailed Template

```gherkin
@[feature-tag1] @[feature-tag2]
Feature: [Feature Name]
  Description: [Brief description of the feature]
  
  As a [user type]
  I want to [action]
  So that [benefit]

  Background:
    Given I am on the Amazon.in home page
    And I am logged in as a registered user

  @[scenario-tag] @[priority]
  Scenario: [Scenario Name]
    Description: [What this scenario tests]
    
    Given [prerequisite step 1]
    And [prerequisite step 2]
    When [action step 1]
    And [action step 2]
    Then [verification step 1]
    And [verification step 2]
    But [negative verification if needed]

  @[scenario-tag]
  Scenario Outline: [Data-driven scenario]
    Given [initial context]
    When I perform action with "<test-data>"
    Then I should see "<expected-result>"
    
    Examples:
      | test-data | expected-result |
      | data1     | result1         |
      | data2     | result2         |
```

---

## AI Prompt Examples

### Prompt 1: Generate Test Cases for New Feature

```
I need to create test cases for [feature name] on Amazon.in.

Context:
- Framework: Playwright Java with Cucumber BDD
- Base URL: https://www.amazon.in
- Existing step definitions: AddToCartSteps, Hooks

Generate:
1. 5 positive test cases
2. 3 negative test cases
3. 2 boundary test cases

Format: Gherkin BDD format
Tags: Use @positive, @negative, @boundary
Include: Background steps, verification steps, appropriate tags

Feature: [Feature Name]
  As a customer
  I want to [action]
  So that [benefit]
```

### Prompt 2: Expand Existing Test Suite

```
I have an existing test suite for add-to-cart functionality. 
Generate additional test cases to expand coverage:

Current scenarios:
- Add product to cart successfully
- Verify cart count

Generate:
1. Test cases for cart management (update quantity, remove item)
2. Test cases for multiple products in cart
3. Test cases for cart persistence
4. Test cases for cart validation

Use same format and step definitions as existing tests.
```

### Prompt 3: Generate Negative Test Cases

```
Generate negative test cases for [feature] on Amazon.in.

Focus on:
- Invalid inputs
- Error handling
- Edge cases
- System failures

Include verification of:
- Error messages
- System behavior
- User feedback

Format: Gherkin BDD with @negative tag
```

---

## Step Definition Mapping

### Common Step Definitions

```java
// Navigation
@Given("I am on the Amazon.in home page")
@Given("I navigate to {string}")

// Search
@When("I search for {string}")
@Then("I should see search results")

// Product Selection
@When("I click on the product {string}")
@When("I click on the product {string} in same tab")

// Cart Operations
@When("I click on Add to Cart button")
@When("I click on item in cart link")
@Then("the product {string} should be available in cart")
@Then("the cart count should be {int}")

// Verification
@Then("the product should be added to the cart successfully")
@Then("I should see {string}")
```

---

## Test Data Management

### Scenario Outline Example

```gherkin
@data-driven @cart
Scenario Outline: Add different products to cart
  Given I am on the Amazon.in home page
  When I search for "<product-name>"
  And I click on the product "<product-name>" in same tab
  And I click on Add to Cart button
  Then the product "<product-name>" should be available in cart
  And the cart count should be <expected-count>

  Examples:
    | product-name              | expected-count |
    | iPhone 17 Pro Max         | 1             |
    | Samsung Galaxy S24        | 1             |
    | MacBook Pro               | 1             |
```

---

## Checklist for Test Case Generation

- [ ] Feature name is clear and descriptive
- [ ] User story format (As a... I want... So that...)
- [ ] Background steps for common setup
- [ ] Appropriate tags assigned
- [ ] Given-When-Then structure followed
- [ ] Verification steps included
- [ ] Test data externalized (if needed)
- [ ] Step definitions exist or can be created
- [ ] Test case is atomic and focused
- [ ] Edge cases covered
- [ ] Error scenarios included

---

## References

- [Cucumber Gherkin Reference](https://cucumber.io/docs/gherkin/reference/)
- [BDD Best Practices](https://cucumber.io/docs/bdd/)
- [Test Case Design Techniques](https://www.guru99.com/test-case-design-techniques.html)

---

## Quick Reference

### Common Tags
- `@smoke` - Critical path tests
- `@regression` - Full regression suite
- `@positive` - Happy path scenarios
- `@negative` - Error scenarios
- `@boundary` - Boundary tests
- `@integration` - End-to-end tests
- `@cart` - Cart functionality
- `@search` - Search functionality
- `@iphone` - iPhone-specific tests

### Common Steps
- `Given I am on the Amazon.in home page`
- `When I search for "{string}"`
- `When I click on Add to Cart button`
- `Then the product should be added to the cart successfully`
- `And the cart count should be {int}`
