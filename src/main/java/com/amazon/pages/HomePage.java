package com.amazon.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * HomePage - Page Object for Amazon.in Home Page.
 * Contains locators and methods for home page interactions.
 */
public class HomePage extends BasePage {

    // Locators (keeping for fallback)
    private static final String SEARCH_BOX = "#twotabsearchtextbox";
    private static final String SEARCH_BUTTON = "#nav-search-submit-button";
    private static final String AMAZON_LOGO = "#nav-logo-sprites";
    private static final String CART_ICON = "#nav-cart";

    public HomePage(Page page) {
        super(page);
    }

    @Step("Navigate to Amazon.in home page")
    public HomePage navigateToHomePage(String url) {
        navigateTo(url);
        logger.info("Navigated to Amazon.in home page");
        return this;
    }

    @Step("Search for product: {productName}")
    public HomePage searchForProduct(String productName) {
        logger.info("Searching for product: {}", productName);
        
        // Use getByRole for more reliable selector (based on codegen)
        try {
            // Try role-based selector first (more reliable)
            page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions()
                    .setName("Search Amazon.in"))
                    .click();
            page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions()
                    .setName("Search Amazon.in"))
                    .fill(productName);
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName("Go")
                    .setExact(true))
                    .click();
            logger.info("Search submitted using role-based selectors");
        } catch (Exception e) {
            // Fallback to ID-based selectors
            logger.debug("Role-based search failed, using ID selectors: {}", e.getMessage());
            waitForSelector(SEARCH_BOX);
            fill(SEARCH_BOX, productName);
            click(SEARCH_BUTTON);
        }
        
        page.waitForLoadState();
        logger.info("Search submitted successfully");
        return this;
    }

    @Step("Verify home page is displayed")
    public boolean isHomePageDisplayed() {
        try {
            waitForSelector(SEARCH_BOX, 10000);
            boolean displayed = isVisible(SEARCH_BOX);
            logger.info("Home page displayed: {}", displayed);
            return displayed;
        } catch (Exception e) {
            logger.error("Home page verification failed: {}", e.getMessage());
            return false;
        }
    }

    @Step("Click on cart icon")
    public void clickCartIcon() {
        logger.info("Clicking on cart icon");
        click(CART_ICON);
        page.waitForLoadState();
    }

    public String getPageTitleText() {
        return getPageTitle();
    }
}
