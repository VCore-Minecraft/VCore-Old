package de.verdox.vcorepaper.subsystems;

import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;
import de.verdox.vcore.subsystem.VCoreSubsystem;

@MongoDBIdentifier(identifier = "VCoreTestSubsystem")
public class VCoreTestSubsystem extends VCoreSubsystem.Bukkit {
    public VCoreTestSubsystem(de.verdox.vcore.plugin.VCorePlugin.Minecraft VCorePlugin) {
        super(VCorePlugin);
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
