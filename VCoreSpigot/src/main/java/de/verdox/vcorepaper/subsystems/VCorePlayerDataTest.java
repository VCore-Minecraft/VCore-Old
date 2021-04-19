package de.verdox.vcorepaper.subsystems;

import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.data.annotations.VCorePersistentData;
import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.manager.PlayerSessionManager;
import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;

import java.util.UUID;

@RequiredSubsystemInfo(parentSubSystem = VCoreTestSubsystem.class)
@MongoDBIdentifier(identifier = "VCorePlayerDataTest")
public class VCorePlayerDataTest extends PlayerData {

    @VCorePersistentData
    public String name = "Jürgen";
    @VCorePersistentData
    public UUID companyUUID;

    public VCorePlayerDataTest(PlayerSessionManager<?> playerSessionManager, UUID playerUUID) {
        super(playerSessionManager, playerUUID);
    }

    @Override
    public void onLoad() {
        Company company = (Company) getPlugin().getServerDataManager().load(Company.class,companyUUID);
        System.out.println("Loaded Name was: "+name);
        System.out.println("Changing Name");
        name = UUID.randomUUID().toString();
        System.out.println("Changed to: "+name);
        pushUpdate();
    }

    @Override
    public void onCleanUp() {

    }
}
