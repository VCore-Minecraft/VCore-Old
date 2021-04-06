package de.verdox.vcore.data.datatypes;

import de.verdox.vcore.data.manager.PlayerSessionManager;
import de.verdox.vcore.plugin.VCorePlugin;
import org.redisson.api.RTopic;

import java.util.*;

public abstract class PlayerData extends VCoreData<PlayerData> {

    private final PlayerSessionManager<?> playerSessionManager;

    //TODO: Statt eines Konstruktors einfach eine Methode die mittels dem Redis schmarn die Werte updaten kann
    // > bei der Reflection und instantiation im Session Manager einfach per UUID initialisieren
    // > gleiches für eine Datenbankfunktion, die kann dann von irgend einem MySQL Ding ausgeführt werden :D

    public PlayerData(PlayerSessionManager<?> playerSessionManager, UUID playerUUID){
        super(playerSessionManager.getRedisManager(), playerUUID);
        if(VCorePlugin.getMongoDBIdentifier(this.getClass()) == null)
            throw new RuntimeException(getClass().getName()+" has no MongoDBAnnotation");
        this.playerSessionManager = playerSessionManager;
    }

    @Override
    public RTopic getDataTopic() {
        return redisManager.getRedissonClient().getTopic(redisManager.generateSubsystemKey(VCorePlugin.findDependSubsystemClass(getClass()),getUUID())+":"+VCorePlugin.getMongoDBIdentifier(getClass()));
    }

    public final void pushUpdate(){
        playerSessionManager.getSession(getUUID()).pushToRedis(this,this.getClass(),getUUID());
    }
}
