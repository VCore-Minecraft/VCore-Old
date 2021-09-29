/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.bungeecord;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public abstract class BungeeCordPlugin extends Plugin implements VCorePlugin<Plugin, VCoreSubsystem.BungeeCord> {

    @Override
    public File getPluginDataFolder() {
        return getPlugin().getDataFolder();
    }

    @Override
    public String getPluginName() {
        return getPlugin().getDescription().getName();
    }

    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    public void consoleMessage(String message, boolean debug) {
        Objects.requireNonNull(message, "message can't be null!");
        if (debug && !debug())
            return;
        consoleMessage(message, 0, debug);
    }

    @Override
    public void consoleMessage(@NotNull String message, int tabSize, boolean debug) {
        Objects.requireNonNull(message, "message can't be null!");
        if (debug && !debug())
            return;
        StringBuilder newMessageBuilder = new StringBuilder();
        for (int i = 0; i < tabSize; i++)
            newMessageBuilder.append("\t");
        if (tabSize >= 1)
            newMessageBuilder.append("&7>> &f");
        newMessageBuilder.append(message);
        getPlugin().getProxy().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&c" + getPluginName() + "&8] " + newMessageBuilder));

    }
}
