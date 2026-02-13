package com.amazon.api;

import com.amazon.config.ConfigReader;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * ApiTest - Demonstrates API testing using Playwright APIRequestContext.
 * Tests Amazon.in API endpoints for availability and response validation.
 */
@Feature("API Testing")
public class ApiTest {

    private static final Logger logger = LogManager.getLogger(ApiTest.class);
    private Playwright playwright;
    private APIRequestContext apiContext;
    private final ConfigReader config = ConfigReader.getInstance();

    @Before
    public void setUp() {
        logger.info("Setting up API test context");
        playwright = Playwright.create();

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        apiContext = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(config.getBaseUrl())
                .setExtraHTTPHeaders(headers));

        logger.info("API context created with base URL: {}", config.getBaseUrl());
    }

    @After
    public void tearDown() {
        if (apiContext != null) {
            apiContext.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
        logger.info("API test context disposed");
    }

    @Test
    @Story("Verify Amazon.in Homepage Accessibility")
    @Description("Validates that the Amazon.in homepage returns HTTP 200 status")
    public void testAmazonHomePageIsAccessible() {
        logger.info("Testing Amazon.in homepage accessibility");
        APIResponse response = apiContext.get("/");

        logger.info("Response status: {}", response.status());
        assertTrue("Amazon.in should return HTTP 200", response.ok());
        assertEquals("Status code should be 200", 200, response.status());

        String contentType = response.headers().get("content-type");
        logger.info("Content-Type: {}", contentType);
        assertNotNull("Content-Type header should be present", contentType);
    }

    @Test
    @Story("Verify Amazon.in Search API")
    @Description("Validates that the Amazon.in search endpoint is accessible")
    public void testAmazonSearchEndpoint() {
        logger.info("Testing Amazon.in search endpoint");
        APIResponse response = apiContext.get("/s?k=iPhone+17+Pro+Max");

        logger.info("Search response status: {}", response.status());
        assertTrue("Search endpoint should return HTTP 200", response.ok());

        String body = response.text();
        assertNotNull("Response body should not be null", body);
        assertTrue("Response should contain search-related content", body.length() > 0);
        logger.info("Search response body length: {} chars", body.length());
    }
}
