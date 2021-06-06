/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.datatypes;

import de.verdox.vcore.data.manager.ServerDataManager;
import de.verdox.vcore.data.session.SSession;

import java.util.UUID;

public abstract class ServerData extends VCoreData{
    private final ServerDataManager<?> serverDataManager;
    private SSession responsibleDataSession;

    public ServerData(ServerDataManager<?> serverDataManager, UUID uuid){
        super(serverDataManager.getRedisManager(), uuid);
        this.serverDataManager = serverDataManager;
    }

    public final ServerDataManager<?> getServerDataManager() {
        return serverDataManager;
    }

    @Override
    public SSession getResponsibleDataSession() {
        if(responsibleDataSession == null)
            responsibleDataSession = (SSession) redisManager.getPlugin().getServerDataManager().getSession(getRequiredSubsystem().getUuid());
        return responsibleDataSession;
    }
}
