/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.files.config;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.files.config.serialization.VCoreDeserializer;
import de.verdox.vcore.plugin.files.config.serialization.VCoreSerializable;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public abstract class VCoreConfig<T> {
    protected final VCorePlugin<?, ?> plugin;
    protected String fileName;
    protected String pluginDirectory;
    protected File file;
    protected T config;
    protected boolean initialized = false;

    public VCoreConfig(@NotNull VCorePlugin<?, ?> plugin, @NotNull File file) {
        Objects.requireNonNull(plugin, "plugin can't be null!");
        Objects.requireNonNull(file, "file can't be null!");
        this.plugin = plugin;
        this.file = file;
        this.fileName = FilenameUtils.removeExtension(file.getName());
        String[] split = file.getPath().split(plugin.getPluginDataFolder().getPath());
        this.pluginDirectory = split[split.length - 1];
    }

    public VCoreConfig(@NotNull VCorePlugin<?, ?> plugin, @NotNull String fileName, @NotNull String pluginDirectory) {
        Objects.requireNonNull(plugin, "plugin can't be null!");
        Objects.requireNonNull(fileName, "fileName can't be null!");
        Objects.requireNonNull(pluginDirectory, "pluginDirectory can't be null!");
        this.plugin = plugin;
        this.file = new File(plugin.getPluginDataFolder() + pluginDirectory, fileName);
        this.fileName = fileName;
        this.pluginDirectory = pluginDirectory;
    }

    public File getFile() {
        return file;
    }

    public abstract T getConfig();

    public abstract void save();

    public abstract void delete();

    public abstract void saveSerializable(String path, VCoreSerializable vCoreSerializable);

    public abstract <S extends VCoreSerializable> S getSerializable(Class<S> serializableClass, String path, VCoreDeserializer<? extends S> vCoreDeserializer);

    public void init() {
        this.config = create();
        if (initialized)
            return;
        setupConfig();
        save();
        onInit();
        initialized = true;
    }

    public abstract T create();

    public abstract void onInit();

    public abstract void setupConfig();

    public VCorePlugin<?, ?> getPlugin() {
        return plugin;
    }

}
