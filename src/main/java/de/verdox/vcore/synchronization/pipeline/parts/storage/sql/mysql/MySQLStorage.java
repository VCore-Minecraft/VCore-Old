package de.verdox.vcore.synchronization.pipeline.parts.storage.sql.mysql;

import com.zaxxer.hikari.HikariDataSource;
import de.verdox.vcore.synchronization.pipeline.parts.storage.sql.SQLStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.03.2022 23:39
 */
public class MySQLStorage extends SQLStorage {

    private final HikariDataSource hikariDataSource;

    public MySQLStorage(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {
        this.hikariDataSource.close();
    }

    @Override
    public @NotNull Connection connection() {
        try {
            return this.hikariDataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to retrieve connection from pool", e);
        }
    }

    @Override
    public int executeUpdate(@NotNull String query, @NotNull Object... objects) {
        try (Connection con = this.connection(); PreparedStatement statement = con.prepareStatement(query)) {
            // write all parameters
            for (int i = 0; i < objects.length; i++) {
                statement.setString(i + 1, Objects.toString(objects[i]));
            }

            // execute the statement
            return statement.executeUpdate();
        } catch (SQLException exception) {
            System.out.println("Exception while executing database update");
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public <T> T executeQuery(@NotNull String query, @NotNull Function<ResultSet, T> callback, @Nullable T def, @NotNull Object... objects) {
        try (Connection con = this.connection(); PreparedStatement statement = con.prepareStatement(query)) {
            // write all parameters
            for (int i = 0; i < objects.length; i++) {
                statement.setString(i + 1, Objects.toString(objects[i]));
            }

            // execute the statement, apply to the result handler
            try (var resultSet = statement.executeQuery()) {
                return callback.apply(resultSet);
            }
        } catch (Throwable throwable) {
            System.out.println("Exception while executing database query");
            throwable.printStackTrace();
        }

        return def;
    }
}
