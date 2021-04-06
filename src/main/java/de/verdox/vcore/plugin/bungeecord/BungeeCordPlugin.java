package de.verdox.vcore.plugin.bungeecord;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

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
    public void consoleMessage(String message) {
        getPlugin().getProxy().getConsole().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',"&8[&c"+getPluginName()+"&8] "+message)));
    }

}
