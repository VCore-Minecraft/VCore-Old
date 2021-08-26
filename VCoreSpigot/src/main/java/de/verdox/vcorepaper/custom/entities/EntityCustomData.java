/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.entities;

import de.verdox.vcorepaper.custom.CustomData;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

public abstract class EntityCustomData<T> extends CustomData<T> {

    public EntityCustomData() {
        super();
    }

    public void onPlayerRightClick(@NotNull PlayerInteractAtEntityEvent event, @NotNull VCoreEntity vCoreEntity) {
    }
}