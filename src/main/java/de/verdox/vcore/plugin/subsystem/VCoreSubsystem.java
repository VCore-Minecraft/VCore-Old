package de.verdox.vcore.plugin.subsystem;

import de.verdox.vcore.synchronization.pipeline.datatypes.PlayerData;
import de.verdox.vcore.synchronization.pipeline.datatypes.ServerData;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.exceptions.SubsystemDeactivatedException;

import java.util.Set;
import java.util.UUID;

public abstract class VCoreSubsystem<S extends VCorePlugin<?,?>> {

    private final S vCorePlugin;
    private final UUID uuid;

    public VCoreSubsystem(S vCorePlugin){
        this.vCorePlugin = vCorePlugin;
        if(GlobalStorage.getDataStorageIdentifier(this.getClass()) == null)
            throw new RuntimeException(getClass().getName()+" has no MongoDBAnnotation");
        uuid = UUID.nameUUIDFromBytes(GlobalStorage.getDataStorageIdentifier(this.getClass()).getBytes());
    }

    public abstract boolean isActivated();

    public S getVCorePlugin() {
        return vCorePlugin;
    }

    public abstract static class Bukkit extends VCoreSubsystem<VCorePlugin.Minecraft> {
        public Bukkit(de.verdox.vcore.plugin.VCorePlugin.Minecraft VCorePlugin) {
            super(VCorePlugin);
        }
    }

    public abstract static class BungeeCord extends VCoreSubsystem<VCorePlugin.BungeeCord> {
        public BungeeCord(de.verdox.vcore.plugin.VCorePlugin.BungeeCord VCorePlugin) {
            super(VCorePlugin);
        }
    }

    public abstract void onSubsystemEnable();
    public abstract void onSubsystemDisable();

    public static void checkSubsystem(VCoreSubsystem<?> subsystem) throws SubsystemDeactivatedException {
        if(subsystem == null)
            return;
        if(!subsystem.isActivated())
            throw new SubsystemDeactivatedException("Subsystem "+subsystem.getClass().getName()+" is not activated!");
    }

    public abstract Set<Class<? extends PlayerData>> playerDataClasses();
    public abstract Set<Class<? extends ServerData>> serverDataClasses();

    public UUID getUuid() {
        return uuid;
    }
}
