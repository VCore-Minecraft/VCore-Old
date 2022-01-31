/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.events.paper.blockevents;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 22:51
 */
public class BlockChangeStateEvent extends BlockEvent {
    private static final HandlerList handlers = new HandlerList();
    private final boolean materialChanged;
    private final BlockData oldBlockState;
    private final BlockData newBlockState;
    private final Cause cause;
    private boolean cancelled;

    public BlockChangeStateEvent(@NotNull Block theBlock, BlockData oldBlockState, BlockData newBlockState, @NotNull Cause cause) {
        super(theBlock);
        this.cause = cause;
        this.materialChanged = !oldBlockState.getMaterial().equals(newBlockState.getMaterial());
        this.oldBlockState = oldBlockState;
        this.newBlockState = newBlockState;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Cause getCause() {
        return cause;
    }

    public boolean isMaterialChanged() {
        return materialChanged;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public BlockData getOldBlockState() {
        return oldBlockState;
    }

    public BlockData getNewBlockState() {
        return newBlockState;
    }

    public enum Cause {
        PLAYER_BLOCK_BREAK,
        PLAYER_BLOCK_PLACE,
        PLAYER_EMPTY_BUCKET,
        PLAYER_FILL_BUCKET,
        PISTON_RETRACT,
        PISTON_EXTEND,
        FLUID_FLOW,
        DRAGON_EGG_TELEPORT,
        BLOCK_EXPLODE,
        ENTITY_CHANGE,
        TRIGGERED_DESTRUCTION,
        GROW,
        FADE,
        LEAVES_DECAY,
        SPONGE_ABSORB,
    }
}
