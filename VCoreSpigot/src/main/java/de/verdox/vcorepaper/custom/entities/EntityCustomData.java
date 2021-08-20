/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.entities;

import de.verdox.vcorepaper.custom.CustomData;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import javax.annotation.Nonnull;

public abstract class EntityCustomData<T> extends CustomData<T> {

    public EntityCustomData() {
        super();
    }

    public void onPlayerRightClick(@Nonnull PlayerInteractAtEntityEvent event, @Nonnull VCoreEntity vCoreEntity) {
    }
}