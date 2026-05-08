package framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads configuration from src/test/resources/config.properties.
 * Environment variables take precedence over property file values,
 * making it easy to override settings in CI pipelines.
 *
 * Usage:
 *   String url     = ConfigReader.get("base.url");
 *   String browser = ConfigReader.get("browser");
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            log.info("Configuration loaded from {}", CONFIG_PATH);
        } catch (IOException e) {
            log.error("Failed to load config.properties from {}: {}", CONFIG_PATH, e.getMessage());
            throw new RuntimeException("Could not load configuration file: " + CONFIG_PATH, e);
        }
    }

    private ConfigReader() {}

    /**
     * Retrieves a property value.
     * Environment variables take precedence (useful for CI secret injection).
     *
     * @param key Property key
     * @return Value string
     * @throws RuntimeException if the key is not found in properties or env
     */
    public static String get(String key) {
        // Check env vars first (CI override)
        String envValue = System.getenv(key.toUpperCase().replace(".", "_"));
        if (envValue != null && !envValue.isBlank()) {
            log.debug("Using env override for key '{}': {}", key, envValue);
            return envValue;
        }

        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new RuntimeException("Missing configuration key: '" + key + "'. " +
                    "Check config.properties or set the " + key.toUpperCase().replace(".", "_") + " env variable.");
        }
        return value.trim();
    }

    /**
     * Retrieves a property value with a fallback default.
     */
    public static String get(String key, String defaultValue) {
        try {
            return get(key);
        } catch (RuntimeException e) {
            log.debug("Key '{}' not found, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Convenience method for boolean properties.
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }

    /**
     * Convenience method for integer properties.
     */
    public static int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }
}
