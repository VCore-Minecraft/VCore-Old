/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.player;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.PipelineImpl;
import de.verdox.vcore.synchronization.pipeline.datatypes.PlayerData;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerPreSessionLoadEvent;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerPreSessionUnloadEvent;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerSessionLoadedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.06.2021 00:33
 */
public class PlayerDataManager implements SystemLoadable {
    protected final VCorePlugin<?, ?> plugin;
    private final PipelineImpl pipelineImpl;
    private final boolean loaded;

    public PlayerDataManager(@NotNull PipelineImpl pipelineImpl) {
        Objects.requireNonNull(pipelineImpl, "pipelineManager can't be null!");
        this.pipelineImpl = pipelineImpl;
        this.plugin = pipelineImpl.getPlugin();
        loaded = true;
    }

    protected final void loginPipeline(@NotNull UUID player) {
        Objects.requireNonNull(player, "player can't be null!");
        if (plugin instanceof VCoreCoreInstance)
            plugin.consoleMessage("&eHandling Player Join &b" + player, false);
        plugin.createTaskBatch().wait(400, TimeUnit.MILLISECONDS).doAsync(() -> {
            plugin.getServices().eventBus.post(new PlayerPreSessionLoadEvent(plugin, player));
            plugin.getServices().getSubsystemManager().getActivePlayerDataClasses()
                    .parallelStream()
                    .forEach(aClass -> {
                        PlayerData playerData = pipelineImpl.load(aClass, player, Pipeline.LoadingStrategy.LOAD_PIPELINE, true);
                        playerData.onConnect(player);
                    });
            plugin.getServices().eventBus.post(new PlayerSessionLoadedEvent(plugin, player, System.currentTimeMillis()));
        }).executeBatch();
    }

    protected final void logoutPipeline(@NotNull UUID player) {
        Objects.requireNonNull(player, "player can't be null!");
        plugin.getServices().eventBus.post(new PlayerPreSessionUnloadEvent(plugin, player));
        plugin.createTaskBatch()
                .doAsync(() -> plugin.getServices().getSubsystemManager().getActivePlayerDataClasses()
                        .parallelStream()
                        .forEach(aClass -> {
                            PlayerData data = pipelineImpl.getLocalCache().getData(aClass, player);
                            data.onDisconnect(player);
                            data.save(true);
                        })).executeBatch();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void shutdown() {

    }
}
