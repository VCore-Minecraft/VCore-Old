package de.verdox.vcorepaper;

import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.data.annotations.VCorePersistentData;
import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.manager.PlayerSessionManager;
import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;
import de.verdox.vcorepaper.subsystems.VCoreTestSubsystem;

import java.util.Map;
import java.util.UUID;

@RequiredSubsystemInfo(parentSubSystem = VCoreTestSubsystem.class)
@MongoDBIdentifier(identifier = "VCorePlayerDataTest")
public class VCorePlayerDataTest extends PlayerData {

    @VCorePersistentData
    public String name;
    @VCorePersistentData
    public UUID companyUUID;

    public VCorePlayerDataTest(PlayerSessionManager<?> playerSessionManager, UUID playerUUID) {
        super(playerSessionManager, playerUUID);
    }

    @Override
    public void onLoad() {
        getRequiredSubsystem().getVCorePlugin().getServerDataManager().getDataHolder(getRequiredSubsystem()).load(Company.class,companyUUID);
    }

    @Override
    public void onCleanUp() {

    }

    @Override
    public void restoreFromDataBase(Map<String, Object> dataFromDatabase) {
        dataFromDatabase.forEach((s, o) -> System.out.println(s+"   :   "+o));
    }

    @Override
    public void restoreFromRedis(Map<String, Object> dataFromRedis) {
        dataFromRedis.forEach((s, o) -> System.out.println(s+"   :   "+o));
    }
}
