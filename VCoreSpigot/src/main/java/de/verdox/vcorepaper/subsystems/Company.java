package de.verdox.vcorepaper.subsystems;

import de.verdox.vcore.data.annotations.*;
import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.manager.ServerDataManager;
import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;

import java.util.Set;
import java.util.UUID;

@MongoDBIdentifier(identifier = "Company")
@RequiredSubsystemInfo(parentSubSystem = VCoreTestSubsystem.class)
@VCoreDataContext(dataContext = DataContext.GLOBAL, preloadStrategy = PreloadStrategy.LOAD_ON_NEED)

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
