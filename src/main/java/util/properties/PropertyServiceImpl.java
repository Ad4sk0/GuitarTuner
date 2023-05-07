package util.properties;

import java.io.*;
import java.net.*;
import java.util.*;

public enum PropertyServiceImpl implements PropertyService {

    INSTANCE;

    private final Properties properties;

    PropertyServiceImpl() {
        String propertiesFileName = "main.properties";
        properties = new Properties();
        loadProperties(propertiesFileName);
    }

    private void loadProperties(String propertiesFileName) {
        URL propertiesResource = PropertyServiceImpl.class.getClassLoader().getResource(propertiesFileName);
        if (propertiesResource == null) {
            throw new IllegalStateException(String.format("Unable to find properties file: %s", propertiesFileName));
        }
        try {
            properties.load(new FileInputStream(propertiesResource.getFile()));
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Unable to read properties file: %s", propertiesFileName));
        }
    }

    @Override
    public String get(String key) {
        return Objects.requireNonNull(properties.getProperty(key), String.format("Required property not found: %s", key));
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
}
