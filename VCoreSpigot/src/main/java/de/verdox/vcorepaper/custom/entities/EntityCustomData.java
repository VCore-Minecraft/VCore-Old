package de.verdox.vcorepaper.custom.entities;

import de.verdox.vcorepaper.custom.CustomData;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public abstract class EntityCustomData<T> extends CustomData<T> {

    public EntityCustomData() {
        super();
    }

    public abstract boolean onDeath(Entity entity);
    public abstract boolean onDamageReceive(Entity entity);
    public abstract boolean onDamageEntity(Entity entity);
    public abstract boolean onPhysicalInteraction(Entity entity, Block block);
}