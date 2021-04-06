package de.verdox.vcore.data.datatypes;

import de.verdox.vcore.data.ServerDataSession;
import de.verdox.vcore.data.manager.ServerDataManager;
import de.verdox.vcore.data.session.SSession;
import org.redisson.api.RTopic;

import java.util.UUID;

public abstract class ServerData extends VCoreData<ServerData>{
    private final ServerDataManager< ?> serverDataManager;
    private SSession responsibleDataSession;

    public ServerData(ServerDataManager<?> serverDataManager, UUID uuid){
        super(serverDataManager.getRedisManager(), uuid);
        this.serverDataManager = serverDataManager;
        //TODO: ServerDataManager -> getDataSession -> insertObject
    }

    public ServerDataManager<?> getServerDataManager() {
        return serverDataManager;
    }

    @Override
    public RTopic getDataTopic() {
        return redisManager.getTopic(this.getClass(),getUUID());
    }

    @Override
    public void pushUpdate() {
        getResponsibleDataManager().pushToRedis(this,this.getClass(),getUUID());
    }

    public SSession getResponsibleDataManager(){
        if(responsibleDataSession == null)
            responsibleDataSession = redisManager.getPlugin().getServerDataManager().getDataHolder(getRequiredSubsystem());
        return responsibleDataSession;
    }
}
