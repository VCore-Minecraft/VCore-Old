package de.verdox.vcorepaper.custom.entities;

import de.verdox.vcorepaper.custom.CustomData;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public abstract class EntityCustomData<T> extends CustomData<T> {

    public EntityCustomData() {
        super();
    }

    public boolean onDeath(Entity entity){
        return false;
    }
    public boolean onDamageReceive(Entity entity){
        return false;
    }
    public boolean onDamageEntity(Entity entity){
        return false;
    }
    public boolean onPhysicalInteraction(Entity entity, Block block){
        return false;
    }
}