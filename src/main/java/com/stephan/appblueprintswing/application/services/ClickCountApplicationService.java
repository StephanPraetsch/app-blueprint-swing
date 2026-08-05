package com.stephan.appblueprintswing.application.services;

import com.stephan.appblueprintswing.application.ports.ClickCountRepository;
import com.stephan.appblueprintswing.application.ports.ClickCountRepositoryFactory;
import com.stephan.appblueprintswing.application.ports.DatabaseConfig;

public final class ClickCountApplicationService {
    private final DatabaseConfig databasePathStore;
    private final ClickCountRepositoryFactory repositoryFactory;
    private String activeRepositoryPath;
    private ClickCountRepository activeRepository;

    public ClickCountApplicationService(DatabaseConfig databasePathStore, ClickCountRepositoryFactory repositoryFactory) {
        this.databasePathStore = databasePathStore;
        this.repositoryFactory = repositoryFactory;
    }

    public synchronized String getDatabasePath() {
        return databasePathStore.getDatabasePath();
    }

    public synchronized void setDatabasePath(String databasePath) {
        databasePathStore.setDatabasePath(databasePath);
        resetRepository();
    }

    public synchronized int getCurrentClickCount() {
        return getRepository().getCurrent();
    }

    public synchronized int incrementClickCount() {
        return getRepository().incrementAndGet();
    }

    public synchronized int resetClickCount() {
        return getRepository().resetAndGet();
    }

    public synchronized void shutdown() {
        resetRepository();
    }

    private ClickCountRepository getRepository() {
        String configuredPath = databasePathStore.getDatabasePath();
        if (activeRepository == null || !configuredPath.equals(activeRepositoryPath)) {
            resetRepository();
            activeRepository = repositoryFactory.create(configuredPath);
            activeRepositoryPath = configuredPath;
        }
        return activeRepository;
    }

    private void resetRepository() {
        if (activeRepository != null) {
            activeRepository.close();
            activeRepository = null;
            activeRepositoryPath = null;
        }
    }
}

