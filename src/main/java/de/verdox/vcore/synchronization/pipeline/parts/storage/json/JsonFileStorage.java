package de.verdox.vcore.synchronization.pipeline.parts.storage.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import jodd.io.FileNameUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.03.2022 19:18
 */
public class JsonFileStorage implements GlobalStorage {
    private final Gson gson;
    private final Path path;

    public JsonFileStorage(Path path) {
        this.path = path;
        this.gson = new GsonBuilder().serializeNulls().create();
    }

    @Override
    public JsonElement loadData(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        try {
            return loadFromFile(dataClass, objectUUID);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean dataExist(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        return Files.exists(getFilePath(dataClass, objectUUID));
    }

    @Override
    public void save(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID, @NotNull JsonElement dataToSave) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        try {
            saveJsonToFile(dataClass, objectUUID, dataToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean remove(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        if (!dataExist(dataClass, objectUUID))
            return false;
        try {
            Files.deleteIfExists(getFilePath(dataClass, objectUUID));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Set<UUID> getSavedUUIDs(@NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Path parentFolder = getParentFolder(dataClass);
        if (!parentFolder.toFile().exists())
            return Set.of();
        try {
            return Files.walk(parentFolder, 1)
                    .skip(1)
                    .filter(path1 -> FileNameUtil.getExtension(path1.getFileName().toString()).equals(".json"))
                    .map(path1 -> FileNameUtil.getBaseName(path1.toString()))
                    .map(UUID::fromString)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
            return Set.of();
        }
    }

    private void saveJsonToFile(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID, @NotNull JsonElement dataToSave) throws IOException {
        if (dataToSave.isJsonNull())
            return;
        Path path = getFilePath(dataClass, objectUUID);

        File file = new File(path.toUri());
        if (!file.exists()) {
            if (!file.getParentFile().mkdirs() || !file.createNewFile())
                throw new RuntimeException("Could not create files for JsonFileStorage [" + path + "]");
        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(dataToSave, writer);
        }
    }

    private JsonElement loadFromFile(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) throws IOException {
        Path path = getFilePath(dataClass, objectUUID);
        File file = new File(path.toUri());
        if (!file.exists())
            throw new RuntimeException("Savefile does not exist for " + dataClass.getSimpleName() + ":" + objectUUID);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
            return JsonParser.parseReader(bufferedReader);
        }
    }

    private Path getParentFolder(@NotNull Class<? extends VCoreData> dataClass) {
        return Path.of(this.path + "//" + getStoragePath(dataClass, getSuffix(dataClass), "//"));
    }

    private Path getFilePath(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        return Path.of(getParentFolder(dataClass) + "//" + objectUUID + ".json");
    }
}
