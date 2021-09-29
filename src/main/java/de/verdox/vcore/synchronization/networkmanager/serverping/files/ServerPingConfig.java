/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.serverping.files;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.files.config.VCoreYAMLConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 01:27
 */
public class ServerPingConfig extends VCoreYAMLConfig {
    public ServerPingConfig(@NotNull VCorePlugin<?, ?> plugin, @NotNull String fileName, @NotNull String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    @Override
    public void onInit() {

    }

    @Override
    public void setupConfig() {
        config.addDefault("ServerInfos.serverName", "serverName");
        config.addDefault("ServerInfos.serverAddress", "serverName");
        config.addDefault("ServerInfos.serverPort", 25565);
        config.addDefault("bungee.enable", true);
        config.options().copyDefaults(true);
        save();
    }

    public String getServerName() {
        return config.getString("ServerInfos.serverName").toLowerCase(Locale.ROOT);
    }

    public String getServerAddress() {
        return config.getString("ServerInfos.serverAddress");
    }

    public int getServerPort() {
        return config.getInt("ServerInfos.serverPort");
    }

    public boolean isBungeeMode() {
        return config.getBoolean("bungee.enable");
    }
}
