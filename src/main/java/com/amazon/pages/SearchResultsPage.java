package com.amazon.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

/**
 * SearchResultsPage - Page Object for Amazon Search Results Page.
 * Contains locators and methods for search results interactions.
 */
public class SearchResultsPage extends BasePage {

    // Locators
    private static final String SEARCH_RESULTS_CONTAINER = "div.s-main-slot";
    private static final String RESULT_ITEMS = "[data-component-type='s-search-result']";
    private static final String NO_RESULTS_SELECTOR = "span.a-size-medium.a-color-base, [data-component-type='s-no-results']";

    public SearchResultsPage(Page page) {
        super(page);
    }

    @Step("Verify no search results are displayed")
    public boolean areNoSearchResultsDisplayed() {
        try {
            page.waitForLoadState();
            page.waitForTimeout(3000);
            int count = locator(RESULT_ITEMS).count();
            if (count == 0) {
                logger.info("No search result items found");
                return true;
            }
            try {
                if (page.locator(NO_RESULTS_SELECTOR).count() > 0) {
                    logger.info("No-results selector found on page");
                    return true;
                }
            } catch (Exception ignored) { }
            String bodyText = page.locator("body").textContent();
            if (bodyText != null) {
                String lower = bodyText.toLowerCase();
                if (lower.contains("no results") || lower.contains("did not match any products")) {
                    logger.info("No-results message found on page");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.debug("Error checking no results: {}", e.getMessage());
            return false;
        }
    }

    @Step("Verify search error or empty state")
    public boolean isSearchErrorOrEmptyState() {
        try {
            page.waitForLoadState();
            page.waitForTimeout(2000);
            int count = locator(RESULT_ITEMS).count();
            if (count == 0) return true;
            String bodyText = page.locator("body").textContent();
            return bodyText != null && (bodyText.toLowerCase().contains("no results")
                    || bodyText.toLowerCase().contains("did not match any products")
                    || bodyText.toLowerCase().contains("try checking your spelling"));
        } catch (Exception e) {
            logger.debug("Error checking search state: {}", e.getMessage());
            return false;
        }
    }

    @Step("Verify search results are displayed")
    public boolean areSearchResultsDisplayed() {
        try {
            waitForSelector(SEARCH_RESULTS_CONTAINER, 15000);
            boolean displayed = locator(RESULT_ITEMS).count() > 0;
            logger.info("Search results displayed: {} (count: {})", displayed, locator(RESULT_ITEMS).count());
            return displayed;
        } catch (Exception e) {
            logger.error("Search results verification failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Finds the clickable link inside a search result item using multiple selector strategies.
     */
    private Locator findClickableLink(Locator result) {
        // Strategy 1: h2 a (most common)
        Locator link = result.locator("h2 a");
        if (link.count() > 0) {
            logger.debug("Found link using 'h2 a' selector");
            return link.first();
        }

        // Strategy 2: a with class containing 'a-link-normal' within h2's parent
        link = result.locator("a.a-link-normal.s-underline-text");
        if (link.count() > 0) {
            logger.debug("Found link using 'a.a-link-normal.s-underline-text' selector");
            return link.first();
        }

        // Strategy 3: Any anchor tag with href containing /dp/
        link = result.locator("a[href*='/dp/']");
        if (link.count() > 0) {
            logger.debug("Found link using 'a[href*=/dp/]' selector");
            return link.first();
        }

        // Strategy 4: Just the h2 element itself (clickable on Amazon)
        link = result.locator("h2");
        if (link.count() > 0) {
            logger.debug("Falling back to clicking 'h2' element directly");
            return link.first();
        }

        // Last resort: first anchor in the result
        logger.debug("Using last resort: first anchor tag in result");
        return result.locator("a").first();
    }

    @Step("Click on product matching: {productName}")
    public void clickOnProduct(String productName) {
        logger.info("Looking for product: {}", productName);
        page.waitForLoadState();
        page.waitForTimeout(2000); // Allow dynamic content to load

        // Try to find the product by matching text in search results
        Locator results = page.locator(RESULT_ITEMS);
        int count = results.count();
        logger.info("Total search results found: {}", count);

        for (int i = 0; i < count; i++) {
            Locator result = results.nth(i);
            String title = result.locator("h2").textContent().trim();
            logger.debug("Result {}: {}", i, title);

            if (title.toLowerCase().contains(productName.toLowerCase()) ||
                    productName.toLowerCase().contains(title.toLowerCase().substring(0, Math.min(title.length(), 30)))) {
                logger.info("Found matching product at index {}: {}", i, title);
                Locator link = findClickableLink(result);
                link.click();
                page.waitForLoadState();
                return;
            }
        }

        // If exact match not found, click the first result that partially matches
        logger.warn("Exact match not found. Looking for partial match...");
        String[] keywords = productName.split("\\s+");
        for (int i = 0; i < count; i++) {
            Locator result = results.nth(i);
            String title = result.locator("h2").textContent().trim().toLowerCase();

            int matchCount = 0;
            for (String keyword : keywords) {
                if (keyword.length() > 2 && title.contains(keyword.toLowerCase())) {
                    matchCount++;
                }
            }

            // If at least 40% of keywords match
            if (matchCount > keywords.length * 0.4) {
                logger.info("Partial match found at index {} (matched {}/{} keywords): {}",
                        i, matchCount, keywords.length, title);
                Locator link = findClickableLink(result);
                link.click();
                page.waitForLoadState();
                return;
            }
        }

        // Last resort: click first result
        logger.warn("No matching product found. Clicking first search result.");
        Locator link = findClickableLink(results.first());
        link.click();
        page.waitForLoadState();
    }

    @Step("Get count of search results")
    public int getSearchResultsCount() {
        return locator(RESULT_ITEMS).count();
    }

    /**
     * Gets the product URL from search results for a matching product.
     * Used for programmatic navigation in same tab.
     */
    private String getProductUrl(String productName) {
        logger.info("Getting product URL for: {}", productName);
        page.waitForLoadState();
        page.waitForTimeout(2000);

        Locator results = page.locator(RESULT_ITEMS);
        int count = results.count();
        logger.info("Total search results found: {}", count);

        for (int i = 0; i < count; i++) {
            Locator result = results.nth(i);
            String title = result.locator("h2").textContent().trim();
            logger.debug("Result {}: {}", i, title);

            if (title.toLowerCase().contains(productName.toLowerCase()) ||
                    productName.toLowerCase().contains(title.toLowerCase().substring(0, Math.min(title.length(), 30)))) {
                logger.info("Found matching product at index {}: {}", i, title);
                Locator link = findClickableLink(result);
                String href = link.getAttribute("href");
                if (href != null && !href.isEmpty()) {
                    // Make sure it's a full URL
                    if (href.startsWith("/")) {
                        href = "https://www.amazon.in" + href;
                    }
                    logger.info("Product URL: {}", href);
                    return href;
                }
            }
        }

        // If exact match not found, try partial match
        String[] keywords = productName.split("\\s+");
        for (int i = 0; i < count; i++) {
            Locator result = results.nth(i);
            String title = result.locator("h2").textContent().trim().toLowerCase();

            int matchCount = 0;
            for (String keyword : keywords) {
                if (keyword.length() > 2 && title.contains(keyword.toLowerCase())) {
                    matchCount++;
                }
            }

            if (matchCount > keywords.length * 0.4) {
                logger.info("Partial match found at index {}", i);
                Locator link = findClickableLink(result);
                String href = link.getAttribute("href");
                if (href != null && !href.isEmpty()) {
                    if (href.startsWith("/")) {
                        href = "https://www.amazon.in" + href;
                    }
                    return href;
                }
            }
        }

        // Last resort: get first result URL
        logger.warn("No matching product found. Using first search result URL.");
        Locator link = findClickableLink(results.first());
        String href = link.getAttribute("href");
        if (href != null && !href.isEmpty()) {
            if (href.startsWith("/")) {
                href = "https://www.amazon.in" + href;
            }
            return href;
        }

        throw new RuntimeException("Could not find product URL for: " + productName);
    }

    @Step("Click on product matching {productName} in same tab")
    public void clickOnProductInSameTab(String productName) {
        logger.info("Clicking on product in same tab: {}", productName);
        page.waitForLoadState();
        page.waitForTimeout(2000); // Allow dynamic content to load

        // Try to find product using role-based selector (from codegen)
        try {
            // Look for link with product name (sponsored ads often have this pattern)
            String linkText = productName;
            if (linkText.length() > 100) {
                // Truncate to reasonable length for matching
                linkText = linkText.substring(0, 100);
            }
            
            // Try clicking via role-based link selector
            try {
                page.getByRole(com.microsoft.playwright.options.AriaRole.LINK, 
                    new Page.GetByRoleOptions().setName(linkText)).click();
                logger.info("Clicked product link using role-based selector");
                page.waitForLoadState();
                return;
            } catch (Exception e) {
                logger.debug("Role-based link click failed, trying partial match");
            }
        } catch (Exception e) {
            logger.debug("Role-based approach failed: {}", e.getMessage());
        }
        
        // Fallback: Get product URL and navigate directly (prevents new tab opening)
        String productUrl = getProductUrl(productName);
        
        // Navigate directly in same tab (prevents new tab opening)
        logger.info("Navigating to product URL in same tab: {}", productUrl);
        page.navigate(productUrl);
        page.waitForLoadState();
        page.waitForTimeout(2000); // Allow page to fully load
        
        logger.info("Product page loaded in same tab successfully");
    }
    
    /**
     * Handle popup/new tab if product opens in a new window.
     * Returns the page object for the new tab, or current page if no popup.
     */
    @Step("Click on product matching {productName} and handle popup")
    public Page clickOnProductWithPopupHandling(String productName) {
        logger.info("Clicking on product with popup handling: {}", productName);
        page.waitForLoadState();
        page.waitForTimeout(2000);
        
        // Get product link
        Locator results = page.locator(RESULT_ITEMS);
        int count = results.count();
        
        for (int i = 0; i < count; i++) {
            Locator result = results.nth(i);
            String title = result.locator("h2").textContent().trim();
            
            if (title.toLowerCase().contains(productName.toLowerCase()) ||
                    productName.toLowerCase().contains(title.toLowerCase().substring(0, Math.min(title.length(), 30)))) {
                logger.info("Found matching product at index {}: {}", i, title);
                Locator link = findClickableLink(result);
                
                // Wait for popup and click
                Page newPage = page.waitForPopup(() -> {
                    link.click();
                });
                
                logger.info("Product opened in new tab/popup");
                newPage.waitForLoadState();
                return newPage;
            }
        }
        
        // If no match found, return current page
        logger.warn("Product not found, returning current page");
        return page;
    }
}
