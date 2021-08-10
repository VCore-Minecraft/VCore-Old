package de.verdox.vcorepaper.custom.old_blocks.enums;

import de.verdox.vcorepaper.custom.old_blocks.VBlock;

public enum VBlockEventPermission {

    BLOCK_GROW_EVENT("vBlockPropertiesBlockGrowEvent"),
    BLOCK_EXPLODE_EVENT("vBlockPropertiesBlockExplodeEvent"),
    BLOCK_DROP_ITEMS_EVENT("vBlockPropertiesBlockDropItemsEvent"),
    BLOCK_LIQUID_EVENT("vBlockPropertiesBlockLiquidEvent"),
    BLOCK_GRAVITY_EVENT("vBlockPropertiesBlockGravityEvent"),
    BLOCK_PISTON_EVENT("vBlockPropertiesBlockPistonEvent"),
    BLOCK_BURN_EVENT("vBlockPropertiesBlockBurnEvent"),
    BLOCK_LEAVES_DECAY_EVENT("vBlockPropertiesBlockLeavesDecayEvent"),
    DELETE_DATA_ON_BREAK("vBlockPropertiesDeleteDataOnBreak"),
    ;
    private String nbtKey;

    VBlockEventPermission(String nbtKey){
        this.nbtKey = nbtKey;
    }

    public boolean isAllowed(VBlock vBlock){
        if(!vBlock.getNBTCompound().hasKey(nbtKey))
            return true;
        return vBlock.getNBTCompound().getBoolean(nbtKey);
    }

    public void setAllowed(VBlock vBlock, boolean value){
        vBlock.getNBTCompound().setObject(nbtKey,value);
    }
}
