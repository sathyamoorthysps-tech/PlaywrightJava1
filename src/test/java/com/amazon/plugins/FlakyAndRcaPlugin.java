package com.amazon.plugins;

import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestRunFinished;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cucumber plugin: captures scenario failures (after retries) and generates
 * Flaky tag list + Root Cause Analysis (RCA) report.
 * Used with Surefire rerunFailingTestsCount=2: failures in this run = failed after 2 retries = flaky.
 */
public class FlakyAndRcaPlugin implements io.cucumber.plugin.EventListener {

    private static final Logger logger = LogManager.getLogger(FlakyAndRcaPlugin.class);
    private static final String OUTPUT_DIR = System.getProperty("project.build.directory", "target");
    private static final String FLAKY_JSON = "cucumber-flaky-failures.json";
    private static final String RCA_REPORT = "rca-report.md";
    private static final String FLAKY_TAGS_TXT = "flaky-scenarios.txt";

    private final List<FailureRecord> failures = new ArrayList<>();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseFinished.class, this::onTestCaseFinished);
        publisher.registerHandlerFor(TestRunFinished.class, this::onTestRunFinished);
    }

    private void onTestCaseFinished(TestCaseFinished event) {
        Result result = event.getResult();
        if (result.getError() != null || "FAILED".equalsIgnoreCase(String.valueOf(result.getStatus()))) {
            FailureRecord record = toFailureRecord(event);
            failures.add(record);
            logger.warn("Scenario failed (will be tagged as flaky if still failing after retries): {}", record.scenarioName);
        }
    }

    private FailureRecord toFailureRecord(TestCaseFinished event) {
        TestCase tc = event.getTestCase();
        Result result = event.getResult();
        String scenarioName = tc.getName();
        String uri = tc.getUri().toString();
        int line = tc.getLocation().getLine();
        String tags = tc.getTags().stream().map(Object::toString).collect(Collectors.joining(", "));
        String errorMessage = "";
        String stackTrace = "";
        if (result.getError() != null) {
            Throwable t = result.getError();
            errorMessage = t.getMessage() != null ? t.getMessage() : t.getClass().getName();
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            stackTrace = sw.toString();
        }
        return new FailureRecord(scenarioName, uri, line, tags, errorMessage, stackTrace, event.getInstant());
    }

    private void onTestRunFinished(TestRunFinished event) {
        if (failures.isEmpty()) {
            logger.info("No failed scenarios in this run; no flaky/RCA report generated.");
            return;
        }
        Path base = Paths.get(OUTPUT_DIR);
        try {
            Files.createDirectories(base);
            writeFlakyJson(base);
            writeFlakyTagsTxt(base);
            writeRcaReport(base);
            logger.info("Flaky/RCA reports written to {} ({} failure(s))", base.toAbsolutePath(), failures.size());
        } catch (IOException e) {
            logger.error("Failed to write flaky/RCA reports: {}", e.getMessage());
        }
    }

    private void writeFlakyJson(Path base) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"flakyScenarios\": [\n");
        for (int i = 0; i < failures.size(); i++) {
            FailureRecord r = failures.get(i);
            sb.append("    {\n");
            sb.append("      \"scenarioName\": ").append(escape(r.scenarioName)).append(",\n");
            sb.append("      \"uri\": ").append(escape(r.uri)).append(",\n");
            sb.append("      \"line\": ").append(r.line).append(",\n");
            sb.append("      \"tags\": ").append(escape(r.tags)).append(",\n");
            sb.append("      \"errorMessage\": ").append(escape(r.errorMessage)).append(",\n");
            sb.append("      \"timestamp\": ").append(escape(r.timestamp.toString())).append("\n");
            sb.append("    }").append(i < failures.size() - 1 ? "," : "").append("\n");
        }
        sb.append("  ],\n  \"tag\": \"@flaky\",\n  \"description\": \"Scenarios that failed after 2 retries (3 attempts total)\"\n}\n");
        Files.write(base.resolve(FLAKY_JSON), sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeFlakyTagsTxt(Path base) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# Flaky scenarios (failed after 2 retries) - tag these with @flaky");
        lines.add("");
        for (FailureRecord r : failures) {
            lines.add(r.uri + ":" + r.line + " " + r.scenarioName);
        }
        Files.write(base.resolve(FLAKY_TAGS_TXT), lines, StandardCharsets.UTF_8);
    }

    private void writeRcaReport(Path base) throws IOException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        StringBuilder md = new StringBuilder();
        md.append("# Root Cause Analysis (RCA) – Failed scenarios\n\n");
        md.append("**Generated:** ").append(fmt.format(Instant.now())).append("\n\n");
        md.append("These scenarios **failed after 2 retries** (3 attempts total) and are tagged as **flaky**.\n\n");
        md.append("---\n\n");

        for (int i = 0; i < failures.size(); i++) {
            FailureRecord r = failures.get(i);
            md.append("## ").append(i + 1).append(". ").append(r.scenarioName).append("\n\n");
            md.append("| Field | Value |\n");
            md.append("|-------|-------|\n");
            md.append("| **Feature / Location** | `").append(r.uri).append(":").append(r.line).append("` |\n");
            md.append("| **Tags** | ").append(r.tags.isEmpty() ? "-" : r.tags).append(" |\n");
            md.append("| **Timestamp** | ").append(r.timestamp != null ? fmt.format(r.timestamp) : "-").append(" |\n");
            md.append("| **Error** | ").append(escapeMd(r.errorMessage)).append(" |\n\n");
            md.append("### Stack trace\n\n```\n").append(escapeMd(r.stackTrace)).append("\n```\n\n");
            md.append("---\n\n");
        }

        md.append("## Summary\n\n");
        md.append("- **Total flaky scenarios:** ").append(failures.size()).append("\n");
        md.append("- **Tag:** These scenarios are marked as **@flaky** (failed after 2 retries). Add `@flaky` to the scenario in the feature file for filtering.\n");
        md.append("- **Recommendation:** Review selectors, waits, and environment; triage and fix or exclude from CI.\n");
        Files.write(base.resolve(RCA_REPORT), md.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String escape(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }

    private static String escapeMd(String s) {
        if (s == null) return "";
        return s.replace("|", "\\|").replace("\n", " ").trim();
    }

    private static class FailureRecord {
        final String scenarioName;
        final String uri;
        final int line;
        final String tags;
        final String errorMessage;
        final String stackTrace;
        final Instant timestamp;

        FailureRecord(String scenarioName, String uri, int line, String tags,
                      String errorMessage, String stackTrace, Instant timestamp) {
            this.scenarioName = scenarioName;
            this.uri = uri;
            this.line = line;
            this.tags = tags;
            this.errorMessage = errorMessage;
            this.stackTrace = stackTrace;
            this.timestamp = timestamp;
        }
    }
}
