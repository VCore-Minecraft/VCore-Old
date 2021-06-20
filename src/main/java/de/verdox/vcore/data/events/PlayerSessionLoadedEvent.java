/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.events;

import de.verdox.vcore.data.session.PlayerSession;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:48
 */
public class PlayerSessionLoadedEvent {
    private final PlayerSession playerSession;
    private long timeStamp;

    public PlayerSessionLoadedEvent(@Nonnull PlayerSession playerSession, long timeStamp){

        this.playerSession = playerSession;
        this.timeStamp = timeStamp;
    }

    public PlayerSession getPlayerSession() {
        return playerSession;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
