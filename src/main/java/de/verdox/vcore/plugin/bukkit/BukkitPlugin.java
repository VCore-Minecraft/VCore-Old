package de.verdox.vcore.plugin.bukkit;

import de.verdox.vcore.plugin.PluginServiceParts;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import org.bukkit.ChatColor;
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
    public void consoleMessage(String message, boolean debug) {
        if(debug && !debug())
            return;
        consoleMessage(message,0,debug);
    }

    @Override
    public void consoleMessage(String message, int tabSize, boolean debug) {
        if(debug && !debug())
            return;
        StringBuilder newMessageBuilder = new StringBuilder();
        for(int i = 0; i < tabSize; i++)
            newMessageBuilder.append("\t");
        if(tabSize >= 1)
            newMessageBuilder.append("&7>> &f");
        newMessageBuilder.append(message);
        org.bukkit.Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&8[&c"+getPluginName()+"&8] "+newMessageBuilder.toString()));
    }
}
