package com.amazon.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * BasePage - Base class for all Page Objects.
 * Contains common methods and utilities for page interactions.
 */
public abstract class BasePage {

    protected final Page page;
    protected final Logger logger;

    protected BasePage(Page page) {
        this.page = page;
        this.logger = LogManager.getLogger(this.getClass());
    }

    protected void navigateTo(String url) {
        logger.info("Navigating to: {}", url);
        page.navigate(url);
        page.waitForLoadState();
    }

    protected void click(String selector) {
        logger.debug("Clicking element: {}", selector);
        page.locator(selector).click();
    }

    protected void fill(String selector, String text) {
        logger.debug("Filling '{}' in element: {}", text, selector);
        page.locator(selector).fill(text);
    }

    protected String getText(String selector) {
        String t = page.locator(selector).textContent();
        return t != null ? t : "";
    }

    protected boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    protected void waitForSelector(String selector) {
        logger.debug("Waiting for selector: {}", selector);
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE));
    }

    protected void waitForSelector(String selector, int timeout) {
        logger.debug("Waiting for selector: {} with timeout: {}ms", selector, timeout);
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeout));
    }

    protected Locator locator(String selector) {
        return page.locator(selector);
    }

    protected void pressKey(String key) {
        logger.debug("Pressing key: {}", key);
        page.keyboard().press(key);
    }

    public String getPageTitle() {
        return page.title();
    }

    public String getPageUrl() {
        return page.url();
    }
}
