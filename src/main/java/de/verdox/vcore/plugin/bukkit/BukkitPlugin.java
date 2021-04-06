package de.verdox.vcore.plugin.bukkit;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public abstract class BukkitPlugin extends JavaPlugin implements VCorePlugin<JavaPlugin, VCoreSubsystem.Bukkit> {

    @Override
    public File getPluginDataFolder() {
        return getPlugin().getDataFolder();
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public String getPluginName() {
        return getPlugin().getName();
    }

    @Override
    public void consoleMessage(String message) {
        org.bukkit.Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&8[&c"+getPluginName()+"&8] "+message));
    }
}
