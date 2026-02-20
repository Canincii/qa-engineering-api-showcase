package com.dummyjson.config;

import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigurationManager {

    private static ConfigurationManager instance;
    private final Properties properties;

    private ConfigurationManager() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                log.error("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (Exception ex) {
            log.error("Error loading config properties", ex);
        }

        // Override with system properties if any (for CI/CD flexibility)
        properties.putAll(System.getProperties());
    }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public int getInt(String key) {
        String prop = properties.getProperty(key);
        return (prop != null) ? Integer.parseInt(prop) : 0;
    }
}
