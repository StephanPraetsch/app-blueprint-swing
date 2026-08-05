package com.stephan.appblueprintswing.adapters.outbound.sqlite;

import com.stephan.appblueprintswing.application.ports.ClickCountRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class SqliteClickCountRepository implements ClickCountRepository {
    private final Connection connection;

    public SqliteClickCountRepository(String databasePath) {
        try {
            Path path = Path.of(databasePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            this.connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            initializeSchema();
        } catch (SQLException | RuntimeException | java.io.IOException exception) {
            throw new IllegalStateException("Failed to initialize SQLite repository", exception);
        }
    }

    @Override
    public synchronized int getCurrent() {
        String sql = "SELECT count FROM click_counter WHERE id = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
            return 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to read click count", exception);
        }
    }

    @Override
    public synchronized int incrementAndGet() {
        updateCount("UPDATE click_counter SET count = count + 1 WHERE id = 1");
        return getCurrent();
    }

    @Override
    public synchronized int resetAndGet() {
        updateCount("UPDATE click_counter SET count = 0 WHERE id = 1");
        return getCurrent();
    }

    @Override
    public synchronized void close() {
        try {
            connection.close();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to close SQLite connection", exception);
        }
    }

    private void initializeSchema() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS click_counter (id INTEGER PRIMARY KEY CHECK(id = 1), count INTEGER NOT NULL)");
            statement.execute("INSERT INTO click_counter (id, count) VALUES (1, 0) ON CONFLICT(id) DO NOTHING");
        }
    }

    private void updateCount(String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to update click count", exception);
        }
    }
}

