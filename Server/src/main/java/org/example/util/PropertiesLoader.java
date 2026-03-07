package org.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    public Properties load(String fileName) {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(fileName)) {
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
