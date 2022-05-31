package de.verdox.vcore.synchronization.pipeline.parts.storage.sql;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.synchronization.pipeline.parts.storage.RemoteStorage;
import de.verdox.vcore.util.global.AnnotationResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.03.2022 22:51
 */
public abstract class SQLStorage implements GlobalStorage, RemoteStorage {

    protected static final String TABLE_COLUMN_KEY = "UUID";
    protected static final String TABLE_COLUMN_VAL = "Document";

    @Override
    public JsonElement loadData(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        return executeQuery(
                String.format("SELECT %s FROM `%s` WHERE %s = ?", TABLE_COLUMN_VAL, tableName(dataClass), TABLE_COLUMN_KEY),
                resultSet -> {
                    try {
                        return resultSet.next() ? JsonParser.parseString(resultSet.getString(TABLE_COLUMN_VAL)) : null;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return null;
                    }
                },
                null,
                objectUUID.toString()
        );
    }

    @Override
    public boolean dataExist(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        return executeQuery(
                String.format("SELECT %s FROM `%s` WHERE %s = ?", TABLE_COLUMN_KEY, tableName(dataClass), TABLE_COLUMN_KEY),
                resultSet -> {
                    try {
                        return resultSet.next();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                },
                false,
                objectUUID.toString());
    }

    @Override
    public void save(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID, @NotNull JsonElement dataToSave) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        Objects.requireNonNull(dataToSave, "dataToSave can't be null!");
        if (!dataExist(dataClass, objectUUID)) {
            executeUpdate(
                    "INSERT INTO `" + tableName(dataClass) + "` (" + TABLE_COLUMN_KEY + "," + TABLE_COLUMN_VAL + ") VALUES (?, ?);",
                    objectUUID.toString(), dataToSave
            );
        } else {
            executeUpdate(
                    "UPDATE `" + tableName(dataClass) + "` SET " + TABLE_COLUMN_VAL + "=? WHERE " + TABLE_COLUMN_KEY + "=?",
                    dataToSave, objectUUID.toString()
            );
        }
    }

    @Override
    public boolean remove(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        return executeUpdate(
                String.format("DELETE FROM `%s` WHERE %s = ?", tableName(dataClass), TABLE_COLUMN_KEY),
                objectUUID.toString()
        ) != -1;
    }

    @Override
    public Set<UUID> getSavedUUIDs(@NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        return executeQuery(
                String.format("SELECT %s FROM `%s`;", TABLE_COLUMN_KEY, tableName(dataClass)),
                resultSet -> {
                    Set<UUID> keys = new HashSet<>();
                    try {
                        while (resultSet.next()) {
                            keys.add(UUID.fromString(resultSet.getString(TABLE_COLUMN_KEY)));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    return keys;
                }, new HashSet<>());
    }

    private String tableName(@NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        var name = AnnotationResolver.getDataStorageIdentifier(dataClass);
        createTableIfNotExists(dataClass, name);
        return name;
    }

    private void createTableIfNotExists(@NotNull Class<? extends VCoreData> dataClass, @NotNull String name) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(name, "name can't be null!");
        executeUpdate(String.format(
                "CREATE TABLE IF NOT EXISTS `%s` (%s VARCHAR(64) PRIMARY KEY, %s TEXT);",
                name,
                TABLE_COLUMN_KEY,
                TABLE_COLUMN_VAL
        ));
    }


    @NotNull
    public abstract Connection connection();

    public abstract int executeUpdate(@NotNull String query, @NotNull Object... objects);

    public abstract <T> T executeQuery(
            @NotNull String query,
            @NotNull Function<ResultSet, T> callback,
            @Nullable T def,
            @NotNull Object... objects
    );
}
