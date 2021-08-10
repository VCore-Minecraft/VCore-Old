/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.block.flags;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 22:19
 */
public enum VBlockFlag {
    BLOCK_GROW_EVENT("flag_deny_grow"),
    BLOCK_EXPLODE_EVENT("flag_deny_explosionDamage"),
    BLOCK_DROP_ITEMS_EVENT("flag_deny_dropItems"),
    BLOCK_LIQUID_EVENT("flag_deny_liquidEvent"),
    BLOCK_GRAVITY_EVENT("flag_deny_gravity"),
    BLOCK_PISTON_EVENT("flag_deny_pistonEvent"),
    BLOCK_BURN_EVENT("flag_deny_burnEvent"),
    BLOCK_LEAVES_DECAY_EVENT("flag_deny_leavesDecay"),
    PRESERVE_DATA_ON_BREAK("flag_deny_deleteDataOnBreak"),
    ;
    private final String nbtTag;

    VBlockFlag(String nbtTag){
        this.nbtTag = nbtTag;
    }

    public String getNbtTag() {
        return nbtTag;
    }
}
