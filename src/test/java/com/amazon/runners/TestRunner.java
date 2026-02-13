package com.amazon.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * TestRunner - Cucumber Test Runner with JUnit.
 * Configures feature files location, step definitions, and plugins.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.amazon.stepdefinitions"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "junit:target/cucumber-reports/cucumber.xml",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.amazon.plugins.FlakyAndRcaPlugin"
        },
        monochrome = true,
        dryRun = false,
        tags = "@addToCart"
)
public class TestRunner {
    // JUnit will automatically pick this up and execute all scenarios matching the configuration
}
