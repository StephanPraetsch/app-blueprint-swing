package com.stephan.appblueprintswing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.stephan.appblueprintswing.adapters.outbound.settings.DatabaseConfigLocalSettings;
import com.stephan.appblueprintswing.adapters.outbound.sqlite.SqliteClickCountRepositoryFactory;
import com.stephan.appblueprintswing.application.services.ClickCountApplicationService;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ClickCountApplicationServiceTest {
    @TempDir
    Path tempDir;

    private ClickCountApplicationService service;

    @AfterEach
    void tearDown() {
        if (service != null) {
            service.shutdown();
        }
    }

    @Test
    void shouldIncrementAndResetCount() {
        Path settingsFilePath = tempDir.resolve("settings.properties");
        Path defaultDatabasePath = tempDir.resolve("counter.sqlite");

        DatabaseConfigLocalSettings config = new DatabaseConfigLocalSettings(settingsFilePath, defaultDatabasePath.toString());
        service = new ClickCountApplicationService(config, new SqliteClickCountRepositoryFactory());

        assertEquals(0, service.getCurrentClickCount());
        assertEquals(1, service.incrementClickCount());
        assertEquals(2, service.incrementClickCount());
        assertEquals(0, service.resetClickCount());
    }
}

