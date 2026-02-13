package com.amazon.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Loads add-to-cart test data from JSON. Supports classpath resource or file path for CI/CD.
 * Use -Dtestdata.path=path/to/file.json or env TESTDATA_PATH to override.
 */
public class TestDataReader {

    private static final Logger logger = LogManager.getLogger(TestDataReader.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ConfigReader config = ConfigReader.getInstance();
    private JsonNode root;

    public TestDataReader() {
        load();
    }

    private void load() {
        String path = config.getTestDataPath();
        boolean ci = config.isCi();
        logger.info("Loading test data from path: {} (CI: {})", path, ci);

        try {
            InputStream in = null;
            if (path.contains("/") || path.contains("\\")) {
                Path filePath = Paths.get(path);
                if (Files.exists(filePath)) {
                    in = Files.newInputStream(filePath);
                    logger.info("Loaded test data from file: {}", filePath.toAbsolutePath());
                }
            }
            if (in == null) {
                in = getClass().getClassLoader().getResourceAsStream(path);
                if (in != null) {
                    logger.info("Loaded test data from classpath: {}", path);
                }
            }
            if (in == null) {
                throw new RuntimeException("Test data not found: " + path);
            }
            try (InputStream stream = in) {
                root = MAPPER.readTree(stream);
            }
        } catch (IOException e) {
            logger.error("Failed to load test data from {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to load test data", e);
        }
    }

    /** Get environment block (default or ci). */
    public JsonNode getEnvironment() {
        String env = config.isCi() ? "ci" : "default";
        return Optional.ofNullable(root.get("environments")).map(e -> e.get(env)).orElse(MAPPER.createObjectNode());
    }

    /** Get first scenario from "positive" array by id, or by index. */
    public JsonNode getPositiveScenario(String id) {
        return getScenario("positive", id);
    }

    public JsonNode getNegativeScenario(String id) {
        return getScenario("negative", id);
    }

    public JsonNode getEdgeScenario(String id) {
        return getScenario("edge", id);
    }

    public JsonNode getBoundaryScenario(String id) {
        return getScenario("boundary", id);
    }

    /** Get any scenario by id from any category. */
    public JsonNode getScenarioById(String scenarioId) {
        for (String category : new String[]{"positive", "negative", "edge", "boundary"}) {
            JsonNode node = getScenario(category, scenarioId);
            if (node != null) return node;
        }
        return null;
    }

    private JsonNode getScenario(String category, String id) {
        JsonNode array = root.get(category);
        if (array == null || !array.isArray()) return null;
        for (JsonNode item : array) {
            JsonNode idNode = item.get("id");
            if (idNode != null && id.equals(idNode.asText())) return item;
        }
        return null;
    }

    public static String text(JsonNode node, String key) {
        if (node == null) return null;
        JsonNode v = node.get(key);
        return v == null ? null : v.asText();
    }

    public static int number(JsonNode node, String key, int defaultValue) {
        if (node == null) return defaultValue;
        JsonNode v = node.get(key);
        return v == null ? defaultValue : v.asInt(defaultValue);
    }

    public static boolean bool(JsonNode node, String key, boolean defaultValue) {
        if (node == null) return defaultValue;
        JsonNode v = node.get(key);
        return v == null ? defaultValue : v.asBoolean(defaultValue);
    }
}
