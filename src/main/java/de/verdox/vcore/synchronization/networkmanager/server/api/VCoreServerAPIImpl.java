/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.server.api;

import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.messaging.MessagingService;
import de.verdox.vcore.synchronization.messaging.instructions.InstructionService;
import de.verdox.vcore.synchronization.networkmanager.player.scheduling.VCorePlayerTaskScheduler;
import de.verdox.vcore.synchronization.networkmanager.server.ServerInstance;
import de.verdox.vcore.synchronization.networkmanager.server.api.instructions.updates.RemoteServerShutdownUpdate;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.08.2021 20:39
 */
public class VCoreServerAPIImpl implements VCoreServerAPI {

    protected final VCorePlugin<?, ?> plugin;
    private final MessagingService<?> messagingService;
    private final InstructionService instructionService;
    protected VCorePlayerTaskScheduler vCorePlayerTaskScheduler;

    public VCoreServerAPIImpl(@NotNull VCoreCoreInstance<?, ?> plugin) {
        Objects.requireNonNull(plugin, "plugin can't be null!");
        this.plugin = plugin;
        this.messagingService = plugin.getServices().getMessagingService();
        this.instructionService = messagingService.getInstructionService();
        this.vCorePlayerTaskScheduler = new VCorePlayerTaskScheduler(plugin);
        registerStandardInstructions();
    }

    private void registerStandardInstructions() {
        instructionService.registerInstructionType(10, RemoteServerShutdownUpdate.class);
    }

    @Override
    public void remoteShutdown(String serverName, boolean ignoreSelf) {
        RemoteServerShutdownUpdate remoteServerShutdownUpdate = new RemoteServerShutdownUpdate(UUID.randomUUID());
        remoteServerShutdownUpdate.withData(serverName.toLowerCase(Locale.ROOT), ignoreSelf);
        instructionService.sendInstruction(remoteServerShutdownUpdate, serverName.toLowerCase(Locale.ROOT));
    }

    @Override
    public ServerInstance getServer(String serverName) {
        UUID serverUUID = plugin.getCoreInstance().getNetworkManager().getServerCache().getServerUUID(serverName);
        return plugin.getCoreInstance().getServices().getPipeline().load(ServerInstance.class, serverUUID, Pipeline.LoadingStrategy.LOAD_PIPELINE);
    }
}
