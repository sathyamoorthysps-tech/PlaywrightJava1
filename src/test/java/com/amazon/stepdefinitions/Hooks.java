package com.amazon.stepdefinitions;

import com.amazon.factory.PlaywrightFactory;
import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;

/**
 * Hooks - Cucumber lifecycle hooks for setup and teardown.
 * Manages browser initialization and cleanup for each scenario.
 */
public class Hooks {

    private static final Logger logger = LogManager.getLogger(Hooks.class);
    private static final ThreadLocal<Page> pageHolder = new ThreadLocal<>();
    private static final ThreadLocal<PlaywrightFactory> factoryHolder = new ThreadLocal<>();
    /** @deprecated Use {@link #getPage()} for thread-safe access in parallel runs. */
    @Deprecated
    public static Page page;

    /** Returns the current thread's Page (thread-safe for parallel execution). */
    public static Page getPage() {
        Page p = pageHolder.get();
        if (p != null) return p;
        return page; // fallback for single-thread
    }

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        logger.info("========================================");
        logger.info("Starting Scenario: {}", scenario.getName());
        logger.info("Tags: {}", scenario.getSourceTagNames());
        logger.info("========================================");

        PlaywrightFactory factory = new PlaywrightFactory();
        Page p = factory.initBrowser();
        factoryHolder.set(factory);
        pageHolder.set(p);
        page = p; // keep for backward compat when single-threaded
        logger.info("Browser initialized for scenario: {}", scenario.getName());
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        logger.info("Finishing Scenario: {} - Status: {}", scenario.getName(), scenario.getStatus());

        // Take screenshot on failure
        if (scenario.isFailed()) {
            logger.error("Scenario FAILED: {}", scenario.getName());
            try {
                byte[] screenshot = PlaywrightFactory.takeScreenshot();
                if (screenshot.length > 0) {
                    scenario.attach(screenshot, "image/png", "failure-screenshot");
                    Allure.addAttachment("Failure Screenshot",
                            "image/png",
                            new ByteArrayInputStream(screenshot),
                            ".png");
                    logger.info("Failure screenshot captured");
                }
            } catch (Exception e) {
                logger.error("Failed to capture screenshot: {}", e.getMessage());
            }
        }

        // Teardown browser for this thread
        PlaywrightFactory factory = factoryHolder.get();
        if (factory != null) {
            factory.tearDown();
            factoryHolder.remove();
        }
        pageHolder.remove();

        logger.info("========================================");
        logger.info("Scenario Completed: {}", scenario.getName());
        logger.info("========================================");
    }
}
