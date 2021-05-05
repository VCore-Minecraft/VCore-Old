package de.verdox.vcore.subsystem;

import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;
import de.verdox.vcore.plugin.VCorePlugin;

import java.util.Set;

@MongoDBIdentifier(identifier = "VCoreIntern")
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
