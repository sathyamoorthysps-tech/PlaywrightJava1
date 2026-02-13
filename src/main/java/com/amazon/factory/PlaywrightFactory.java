package com.amazon.factory;

import com.amazon.config.ConfigReader;
import com.microsoft.playwright.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

/**
 * PlaywrightFactory - Manages Playwright browser lifecycle.
 * Supports Chromium, Firefox, and WebKit browsers.
 * Thread-safe for parallel execution using ThreadLocal.
 */
public class PlaywrightFactory {

    private static final Logger logger = LogManager.getLogger(PlaywrightFactory.class);
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    private final ConfigReader config;

    public PlaywrightFactory() {
        this.config = ConfigReader.getInstance();
    }

    /**
     * Initializes Playwright, launches browser, creates context and page.
     *
     * @return Page object ready for interaction
     */
    public Page initBrowser() {
        String browserName = config.getBrowser().toLowerCase();
        boolean headless = config.isHeadless();

        logger.info("Initializing Playwright with browser: {} (headless: {})", browserName, headless);

        Playwright playwright = Playwright.create();
        playwrightThreadLocal.set(playwright);

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(100);

        Browser browser;
        switch (browserName) {
            case "firefox":
                logger.info("Launching Firefox browser");
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "webkit":
                logger.info("Launching WebKit browser");
                browser = playwright.webkit().launch(launchOptions);
                break;
            case "chromium":
            default:
                logger.info("Launching Chromium browser");
                browser = playwright.chromium().launch(launchOptions);
                break;
        }
        browserThreadLocal.set(browser);

        // Context options - Set viewport to match screen resolution
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setDeviceScaleFactor(1.0);

        if (config.isVideoRecord()) {
            contextOptions.setRecordVideoDir(Paths.get("target/videos/"));
            contextOptions.setRecordVideoSize(1280, 720);
        }

        BrowserContext context = browser.newContext(contextOptions);
        contextThreadLocal.set(context);

        if (config.isTraceRecord()) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }

        context.setDefaultTimeout(config.getDefaultTimeout());
        context.setDefaultNavigationTimeout(config.getNavigationTimeout());

        Page page = context.newPage();
        pageThreadLocal.set(page);

        // Set viewport to ensure proper screen fitting
        page.setViewportSize(1920, 1080);
        
        logger.info("Browser initialized successfully with viewport: 1920x1080");
        return page;
    }

    /**
     * Returns the current thread's Page instance.
     */
    public static Page getPage() {
        return pageThreadLocal.get();
    }

    /**
     * Returns the current thread's BrowserContext instance.
     */
    public static BrowserContext getContext() {
        return contextThreadLocal.get();
    }

    /**
     * Takes a screenshot and returns the byte array.
     */
    public static byte[] takeScreenshot() {
        Page page = getPage();
        if (page != null) {
            return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        }
        return new byte[0];
    }

    /**
     * Closes all Playwright resources for the current thread.
     */
    public void tearDown() {
        logger.info("Tearing down Playwright resources");

        try {
            if (config.isTraceRecord() && contextThreadLocal.get() != null) {
                contextThreadLocal.get().tracing().stop(new Tracing.StopOptions()
                        .setPath(Paths.get("target/traces/trace.zip")));
            }

            Page page = pageThreadLocal.get();
            if (page != null && !page.isClosed()) {
                page.close();
                pageThreadLocal.remove();
            }

            BrowserContext context = contextThreadLocal.get();
            if (context != null) {
                context.close();
                contextThreadLocal.remove();
            }

            Browser browser = browserThreadLocal.get();
            if (browser != null) {
                browser.close();
                browserThreadLocal.remove();
            }

            Playwright playwright = playwrightThreadLocal.get();
            if (playwright != null) {
                playwright.close();
                playwrightThreadLocal.remove();
            }

            logger.info("Playwright resources closed successfully");
        } catch (Exception e) {
            logger.error("Error during teardown: {}", e.getMessage());
        }
    }
}
