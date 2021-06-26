package de.verdox.vcore.plugin.subsystem;

import de.verdox.vcore.synchronization.pipeline.annotations.DataStorageIdentifier;
import de.verdox.vcore.synchronization.pipeline.datatypes.PlayerData;
import de.verdox.vcore.synchronization.pipeline.datatypes.ServerData;
import de.verdox.vcore.plugin.VCorePlugin;

import java.util.Set;

@DataStorageIdentifier(identifier = "VCoreIntern")
public class DummySubSystem extends VCoreSubsystem<VCorePlugin<?,?>>{
    public DummySubSystem(VCorePlugin<?, ?> vCorePlugin) {
        super(vCorePlugin);
    }

    @Override
    public boolean isActivated() {
        return true;
    }

    @Override
    public void onSubsystemEnable() {

    }

    @Override
    public void onSubsystemDisable() {

    }

    @Override
    public Set<Class<? extends PlayerData>> playerDataClasses() {
        return null;
    }

    @Override
    public Set<Class<? extends ServerData>> serverDataClasses() {
        return null;
    }
}
