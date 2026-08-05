package com.stephan.appblueprintswing.application.ports;

public interface ClickCountRepositoryFactory {
    ClickCountRepository create(String databasePath);
}

