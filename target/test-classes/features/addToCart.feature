@smoke @cart
Feature: Add Product to Cart on Amazon.in

  As a user of Amazon.in
  I want to search for a product and add it to my cart
  So that I can purchase it later

  Background:
    Given I am on the Amazon.in home page

  # ----- Positive scenarios (data-driven from JSON; CI can override via testdata.path) -----
  @positive @addToCart @optimized @dataDriven
  Scenario Outline: Add product to cart - positive (data from JSON)
    When I search for "<searchTerm>"
    Then I should see search results
    When I click on the product "<productName>" in same tab
    And I click on Add to Cart button
    Then the product should be added to the cart successfully
    And I click on item in cart link
    And the product "<productName>" should be available in cart
    And the cart count should be <expectedCartCount>

    Examples:
      | searchTerm            | productName        | expectedCartCount |
      | iPhone 17 Pro Max 2 TB| iPhone 17 Pro Max  | 1                 |
      | wireless mouse       | wireless mouse     | 1                 |

  @positive @addToCart @iphone @optimized
  Scenario: Add iPhone 17 Pro Max to Cart (Optimized - Single Tab)
    When I search for "iPhone 17 Pro Max 2 TB"
    Then I should see search results
    When I click on the product "iPhone 17 Pro Max" in same tab
    And I click on Add to Cart button
    Then the product should be added to the cart successfully
    And I click on item in cart link
    And the product "iPhone 17 Pro Max" should be available in cart
    And the cart count should be 1

  # ----- Positive scenario driven by JSON test data (scenario id) -----
  @positive @addToCart @dataDriven @json
  Scenario Outline: Add to cart - positive from JSON test data
    Given I load test scenario "<scenarioId>" from test data category "positive"
    When I search for the product from loaded scenario
    Then I should see search results
    When I open first matching product and add to cart from loaded scenario
    Then the product should be added to the cart successfully
    And I click on item in cart link
    And the product from loaded scenario should be available in cart
    And the cart count from loaded scenario should match

    Examples:
      | scenarioId  |
      | positive_1  |
      | positive_2  |

  # ----- Negative scenarios -----
  @negative @addToCart @noResults
  Scenario: Search with non-existent product - no results
    When I search for "xyznonexistentproduct12345"
    Then I should see no search results

  @negative @addToCart @emptySearch
  Scenario: Search with empty or whitespace - error or no search
    When I search for "   "
    Then I should see search error or empty state

  @negative @addToCart @cartCountMismatch
  Scenario: Cart count mismatch should fail (negative assertion)
    When I search for "iPhone 17 Pro Max 2 TB"
    Then I should see search results
    When I click on the product "iPhone 17 Pro Max" in same tab
    And I click on Add to Cart button
    Then the product should be added to the cart successfully
    And the cart count should not be 99

  # ----- Negative from JSON -----
  @negative @addToCart @dataDriven @json
  Scenario Outline: No results - negative from JSON test data
    Given I load test scenario "<scenarioId>" from test data category "negative"
    When I search for the product from loaded scenario
    Then I should see no search results or error from loaded scenario

    Examples:
      | scenarioId  |
      | negative_1  |
      | negative_2  |

  # ----- Edge scenarios -----
  @edge @addToCart @longSearchTerm
  Scenario: Add to cart with long search term (edge)
    When I search for "iPhone 17 Pro Max 2 TB"
    Then I should see search results
    When I click on the product "iPhone 17 Pro Max" in same tab
    And I click on Add to Cart button
    Then the product should be added to the cart successfully
    And the cart count should be 1

  @edge @addToCart @shortSearchTerm
  Scenario: Add to cart with short search term (edge)
    When I search for "mouse"
    Then I should see search results
    When I click on the product "mouse" in same tab
    And I click on Add to Cart button
    Then the product should be added to the cart successfully
    And the cart count should be 1

  # ----- Edge from JSON -----
  @edge @addToCart @dataDriven @json
  Scenario Outline: Add to cart - edge from JSON test data
    Given I load test scenario "<scenarioId>" from test data category "edge"
    When I search for the product from loaded scenario
    Then I should see search results
    When I open first matching product and add to cart from loaded scenario
    Then the product should be added to the cart successfully
    And the cart count from loaded scenario should match

    Examples:
      | scenarioId |
      | edge_1     |
      | edge_2     |

  # ----- Boundary scenarios -----
  @boundary @addToCart @minQuantity
  Scenario: Cart with minimum quantity one (boundary)
    When I search for "iPhone 17 Pro Max 2 TB"
    Then I should see search results
    When I click on the product "iPhone 17 Pro Max" in same tab
    And I click on Add to Cart button
    Then the product should be added to the cart successfully
    And the cart count should be 1
    And the cart should contain "iPhone 17 Pro Max" with quantity 1

  @boundary @addToCart @dataDriven @json
  Scenario Outline: Add to cart - boundary from JSON test data
    Given I load test scenario "<scenarioId>" from test data category "boundary"
    When I search for the product from loaded scenario
    Then I should see search results
    When I open first matching product and add to cart from loaded scenario
    Then the product should be added to the cart successfully
    And the cart count from loaded scenario should match

    Examples:
      | scenarioId  |
      | boundary_1  |
      | boundary_2  |
