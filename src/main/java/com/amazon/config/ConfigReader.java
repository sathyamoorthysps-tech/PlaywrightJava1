package com.amazon.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader - Reads configuration properties from config.properties file.
 * Implements Singleton pattern for efficient resource management.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private final Properties properties;

    private ConfigReader() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.error("config.properties file not found in classpath");
                throw new RuntimeException("config.properties not found");
            }
            properties.load(input);
            logger.info("Configuration properties loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading config.properties: {}", e.getMessage());
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    /**
     * Get property with CI/CD override order: System property > Environment variable > config file.
     * Environment variable key is derived from property key (e.g. base.url -> BASE_URL).
     */
    public String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            String envKey = key.replace(".", "_").toUpperCase();
            value = System.getenv(envKey);
        }
        if (value == null) {
            value = properties.getProperty(key);
        }
        return value;
    }

    /** Path to JSON test data file. Override in CI with -Dtestdata.path or env TESTDATA_PATH. */
    public String getTestDataPath() {
        return getProperty("testdata.path", "testdata/add-to-cart-data.json");
    }

    /** Whether running in CI (e.g. env CI=true or JENKINS_HOME set). Used to pick environment in test data. */
    public boolean isCi() {
        return Boolean.parseBoolean(getProperty("CI", "false"))
                || System.getenv("JENKINS_HOME") != null
                || System.getenv("GITHUB_ACTIONS") != null;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public String getBaseUrl() {
        return getProperty("base.url");
    }

    public String getBrowser() {
        return getProperty("browser", "chromium");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    public int getDefaultTimeout() {
        return Integer.parseInt(getProperty("default.timeout", "30000"));
    }

    public int getNavigationTimeout() {
        return Integer.parseInt(getProperty("navigation.timeout", "60000"));
    }

    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure", "true"));
    }

    public boolean isVideoRecord() {
        return Boolean.parseBoolean(getProperty("video.record", "false"));
    }

    public boolean isTraceRecord() {
        return Boolean.parseBoolean(getProperty("trace.record", "false"));
    }
}
