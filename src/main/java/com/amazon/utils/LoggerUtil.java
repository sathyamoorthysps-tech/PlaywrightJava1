package com.amazon.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * LoggerUtil - Centralized logging utility using Log4j2.
 * Provides convenient static methods for logging across the framework.
 */
public class LoggerUtil {

    private LoggerUtil() {
        // Utility class - prevent instantiation
    }

    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }

    public static void info(Logger logger, String message) {
        logger.info(message);
    }

    public static void debug(Logger logger, String message) {
        logger.debug(message);
    }

    public static void warn(Logger logger, String message) {
        logger.warn(message);
    }

    public static void error(Logger logger, String message) {
        logger.error(message);
    }

    public static void error(Logger logger, String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}
