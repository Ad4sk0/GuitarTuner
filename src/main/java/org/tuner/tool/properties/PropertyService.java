package org.tuner.tool.properties;

public interface PropertyService {
    String get(String key);

    String get(String key, String defaultValue);

    int getInt(String key);

    int getInt(String key, int defaultValue);

    boolean getBoolean(String key);

    boolean getBoolean(String key, boolean defaultValue);

    void set(String key, String value);

    boolean containsProperty(String key);
}
