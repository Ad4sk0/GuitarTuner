package org.tuner.tool.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public enum PropertyServiceImpl implements PropertyService {

    INSTANCE;
    private final Logger logger = Logger.getLogger(PropertyServiceImpl.class.getName());

    private final Properties properties;

    PropertyServiceImpl() {
        String propertiesFileName = "main.properties";
        properties = new Properties();
        loadProperties(propertiesFileName);
        logProperties();
    }

    private void logProperties() {
        for (var entry : properties.entrySet()) {
            logger.info(() -> String.format("%s: %s%n", entry.getKey(), entry.getValue()));
        }
    }

    private void loadProperties(String propertiesFileName) {
        URL propertiesResource = PropertyServiceImpl.class.getClassLoader().getResource(propertiesFileName);
        if (propertiesResource == null) {
            throw new IllegalStateException(String.format("Unable to find properties file: %s", propertiesFileName));
        }
        try (var file = new FileInputStream(propertiesResource.getFile())) {
            properties.load(file);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Unable to read properties file: %s", propertiesFileName));
        }
    }

    @Override
    public String get(String key) {
        return Objects.requireNonNull(properties.getProperty(key.trim()), String.format("Required property not found: %s", key));
    }

    @Override
    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    @Override
    public int getInt(String key) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(String.format("Unable to parse property %s with value %s to integer", key, get(key)));
        }
    }

    @Override
    public int getInt(String key, int defaultValue) {
        if (properties.containsKey(key)) {
            return getInt(key);
        }
        return defaultValue;
    }

    @Override
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        if (properties.containsKey(key)) {
            return getBoolean(key);
        }
        return defaultValue;
    }

    @Override
    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    @Override
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }
}
