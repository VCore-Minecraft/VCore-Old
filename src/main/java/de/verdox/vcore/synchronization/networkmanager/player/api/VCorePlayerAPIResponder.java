/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api;

import de.verdox.vcore.synchronization.networkmanager.player.api.querytypes.ServerLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 20:08
 */
public interface VCorePlayerAPIResponder {
    ServerLocation playerLocationQueryResponse(@Nonnull UUID uuid);
    void teleportResponse(@Nonnull UUID uuid, @Nonnull ServerLocation serverLocation);
    void kickResponse(@Nonnull UUID uuid);
    void changeServerResponse(@Nonnull UUID uuid);
}
