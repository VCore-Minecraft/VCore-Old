/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.entities;

import de.verdox.vcore.nbt.CustomData;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

public abstract class EntityCustomData<T> extends CustomData<T> {

    public EntityCustomData() {
        super();
    }

    public void onPlayerRightClick(@NotNull PlayerInteractAtEntityEvent event, @NotNull VCoreEntity vCoreEntity) {
    }
}