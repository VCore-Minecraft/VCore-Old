/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.subsystem;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.datatypes.PlayerData;
import de.verdox.vcore.synchronization.pipeline.datatypes.ServerData;
import de.verdox.vcore.util.global.AnnotationResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public abstract class VCoreSubsystem<S extends VCorePlugin<?, ?>> {

    private final S vCorePlugin;
    private final UUID uuid;

    public VCoreSubsystem(@NotNull S vCorePlugin) {
        Objects.requireNonNull(vCorePlugin, "vCorePlugin can't be null!");
        this.vCorePlugin = vCorePlugin;
        uuid = UUID.nameUUIDFromBytes(AnnotationResolver.getDataStorageIdentifier(this.getClass()).getBytes());
    }

    public abstract boolean isActivated();

    public S getVCorePlugin() {
        return vCorePlugin;
    }

    public abstract void onSubsystemEnable();

    public abstract void onSubsystemDisable();

    public abstract Set<Class<? extends PlayerData>> playerDataClasses();

    public abstract Set<Class<? extends ServerData>> serverDataClasses();

    public UUID getUuid() {
        return uuid;
    }

    public abstract static class Bukkit extends VCoreSubsystem<VCorePlugin.Minecraft> {
        public Bukkit(de.verdox.vcore.plugin.VCorePlugin.Minecraft VCorePlugin) {
            super(VCorePlugin);
        }
    }

    public abstract static class BungeeCord extends VCoreSubsystem<VCorePlugin.BungeeCord> {
        public BungeeCord(de.verdox.vcore.plugin.VCorePlugin.BungeeCord VCorePlugin) {
            super(VCorePlugin);
        }
    }
}
