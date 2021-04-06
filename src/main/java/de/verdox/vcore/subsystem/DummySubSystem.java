package de.verdox.vcore.subsystem;

import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;
import de.verdox.vcore.plugin.VCorePlugin;

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
}
