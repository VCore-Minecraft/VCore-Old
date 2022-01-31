/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.block.flags;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 22:19
 */
public enum VBlockFlag {
    DENY_BLOCK_GROW_EVENT("flag_deny_grow"),
    DENY_BLOCK_EXPLODE_EVENT("flag_deny_explosionDamage"),
    DENY_BLOCK_DROP_ITEMS_EVENT("flag_deny_dropItems"),
    DENY_BLOCK_LIQUID_EVENT("flag_deny_liquidEvent"),
    DENY_BLOCK_PISTON_EVENT("flag_deny_pistonEvent"),
    DENY_BLOCK_BURN_EVENT("flag_deny_burnEvent"),
    DENY_BLOCK_LEAVES_DECAY_EVENT("flag_deny_leavesDecay"),
    PRESERVE_DATA_ON_BREAK("flag_deny_deleteDataOnBreak"),
    ;
    private final String nbtTag;

    VBlockFlag(String nbtTag) {
        this.nbtTag = nbtTag;
    }

    public String getNbtTag() {
        return nbtTag;
    }
}
