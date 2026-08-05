package com.stephan.appblueprintswing.adapters.outbound.settings;

import com.stephan.appblueprintswing.application.ports.DatabaseConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DatabaseConfigLocalSettings implements DatabaseConfig {
    private static final String DATABASE_PATH_KEY = "databasePath";

    private final Path settingsFilePath;
    private final String defaultDatabasePath;

    public DatabaseConfigLocalSettings(Path settingsFilePath, String defaultDatabasePath) {
        this.settingsFilePath = settingsFilePath;
        this.defaultDatabasePath = defaultDatabasePath;
    }

    @Override
    public synchronized String getDatabasePath() {
        Properties properties = readSettings();
        String configuredPath = properties.getProperty(DATABASE_PATH_KEY, "").trim();
        return configuredPath.isEmpty() ? defaultDatabasePath : configuredPath;
    }

    @Override
    public synchronized void setDatabasePath(String databasePath) {
        String normalizedPath = databasePath == null ? "" : databasePath.trim();
        if (normalizedPath.isEmpty()) {
            throw new IllegalArgumentException("Database path must not be empty.");
        }

        Properties properties = readSettings();
        properties.setProperty(DATABASE_PATH_KEY, normalizedPath);
        writeSettings(properties);
    }

    private Properties readSettings() {
        Properties properties = new Properties();
        if (!Files.exists(settingsFilePath)) {
            return properties;
        }

        try (InputStream inputStream = Files.newInputStream(settingsFilePath)) {
            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            return new Properties();
        }
    }

    private void writeSettings(Properties properties) {
        try {
            Path parent = settingsFilePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (OutputStream outputStream = Files.newOutputStream(settingsFilePath)) {
                properties.store(outputStream, "App Blueprint Swing settings");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist settings", exception);
        }
    }
}

