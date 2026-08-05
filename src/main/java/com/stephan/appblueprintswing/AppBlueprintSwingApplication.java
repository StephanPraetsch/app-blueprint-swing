package com.stephan.appblueprintswing;

import com.stephan.appblueprintswing.adapters.outbound.settings.DatabaseConfigLocalSettings;
import com.stephan.appblueprintswing.adapters.outbound.sqlite.SqliteClickCountRepositoryFactory;
import com.stephan.appblueprintswing.application.services.ClickCountApplicationService;
import com.stephan.appblueprintswing.ui.MainFrame;

import java.nio.file.Path;

import javax.swing.SwingUtilities;

public class AppBlueprintSwingApplication {

    public static void main(String[] args) {
        Path appDirectory = Path.of(System.getProperty("user.home"), ".app-blueprint-swing");
        Path defaultDatabasePath = appDirectory.resolve("app-blueprint-swing.sqlite");
        Path settingsFilePath = appDirectory.resolve("settings.properties");

        DatabaseConfigLocalSettings databaseConfig = new DatabaseConfigLocalSettings(settingsFilePath, defaultDatabasePath.toString());
        SqliteClickCountRepositoryFactory repositoryFactory = new SqliteClickCountRepositoryFactory();
        ClickCountApplicationService clickCountService = new ClickCountApplicationService(databaseConfig, repositoryFactory);

        Runtime.getRuntime().addShutdownHook(new Thread(clickCountService::shutdown));

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(clickCountService);
            frame.setVisible(true);
        });
    }
}

