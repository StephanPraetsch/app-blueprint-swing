package com.stephan.appblueprintswing.adapters.outbound.sqlite;

import com.stephan.appblueprintswing.application.ports.ClickCountRepository;
import com.stephan.appblueprintswing.application.ports.ClickCountRepositoryFactory;

public final class SqliteClickCountRepositoryFactory implements ClickCountRepositoryFactory {
    @Override
    public ClickCountRepository create(String databasePath) {
        return new SqliteClickCountRepository(databasePath);
    }
}

