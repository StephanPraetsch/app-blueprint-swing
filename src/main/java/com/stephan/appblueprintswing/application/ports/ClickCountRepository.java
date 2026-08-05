package com.stephan.appblueprintswing.application.ports;

public interface ClickCountRepository {
    int getCurrent();

    int incrementAndGet();

    int resetAndGet();

    void close();
}

