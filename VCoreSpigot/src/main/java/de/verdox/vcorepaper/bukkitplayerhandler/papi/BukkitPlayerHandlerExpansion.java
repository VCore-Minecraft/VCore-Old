/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.papi;

import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.bukkitplayerhandler.playerdata.PlayerHandlerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 06.07.2021 18:39
 */
public class BukkitPlayerHandlerExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "vcorepaper";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Verdox";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        UUID playerUUID = player.getUniqueId();
        PlayerHandlerData playerHandlerData = VCorePaper.getInstance().getServices().getPipeline().load(PlayerHandlerData.class,playerUUID, Pipeline.LoadingStrategy.LOAD_LOCAL_ELSE_LOAD);
        if(playerHandlerData == null)
            return null;
        if(identifier.equals("playTime"))
            return VCoreUtil.getTimeUtil().convertSecondsHM(playerHandlerData.getPlayTimeSeconds());
        return null;
    }
}
