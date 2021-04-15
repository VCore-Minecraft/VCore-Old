package de.verdox.vcore.data.datatypes;

import de.verdox.vcore.data.manager.PlayerSessionManager;
import de.verdox.vcore.data.session.PlayerSession;
import de.verdox.vcore.plugin.VCorePlugin;
import org.redisson.api.RTopic;

import java.util.*;

public abstract class PlayerData extends VCoreData {

    private final PlayerSessionManager<?> playerSessionManager;

    public PlayerData(PlayerSessionManager<?> playerSessionManager, UUID playerUUID){
        super(playerSessionManager.getRedisManager(), playerUUID);
        if(VCorePlugin.getMongoDBIdentifier(this.getClass()) == null)
            throw new RuntimeException(getClass().getName()+" has no MongoDBAnnotation");
        this.playerSessionManager = playerSessionManager;
    }

    public final void pushUpdate(){
        playerSessionManager.getSession(getUUID()).pushToRedis(this,this.getClass(),getUUID());
    }

    public final PlayerSession getResponsiblePlayerSession(){
        return playerSessionManager.getSession(getUUID());
    }
}
