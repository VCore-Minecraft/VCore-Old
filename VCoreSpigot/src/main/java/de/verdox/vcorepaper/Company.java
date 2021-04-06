package de.verdox.vcorepaper;

import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.manager.ServerDataManager;
import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.data.annotations.VCorePersistentData;
import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;
import de.verdox.vcorepaper.subsystems.VCoreTestSubsystem;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@MongoDBIdentifier(identifier = "Company")
@RequiredSubsystemInfo(parentSubSystem = VCoreTestSubsystem.class)
public class Company extends ServerData {

    @VCorePersistentData
    private String companyName = "TEST";
    @VCorePersistentData
    private Set<UUID> members;
    @VCorePersistentData
    private UUID ownerUUID;

    public Company(ServerDataManager<?> serverDataManager, UUID uuid) {
        super(serverDataManager, uuid);
    }

    @Override
    public void restoreFromDataBase(Map<String, Object> dataFromDatabase) {

    }

    @Override
    public void restoreFromRedis(Map<String, Object> dataFromRedis) {

    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onCleanUp() {

    }
}
