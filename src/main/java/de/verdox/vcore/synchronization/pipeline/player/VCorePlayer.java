/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.player;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 15.06.2021 01:07
 */
public class VCorePlayer implements Serializable {

    private final UUID playerUUID;
    private final String displayName;
    private final AtomicBoolean clearable = new AtomicBoolean(false);

    VCorePlayer(@Nonnull UUID playerUUID,@Nonnull String displayName){
        this.playerUUID = playerUUID;
        this.displayName = displayName;
    }

    public boolean isClearable() {
        return clearable.get();
    }

    public void setClearable(boolean clearable) {
        this.clearable.set(clearable);
    }

    public boolean isOnThisServer(){
        return toBukkitPlayer() != null;
    }

    public Player toBukkitPlayer(){
        try{
            return Bukkit.getPlayer(playerUUID);
        }
        catch (Exception e){
            return null;
        }
    }

    public ProxiedPlayer toBungeePlayer(){
        try{
            return ProxyServer.getInstance().getPlayer(playerUUID);
        }
        catch (Exception e){
            return null;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
