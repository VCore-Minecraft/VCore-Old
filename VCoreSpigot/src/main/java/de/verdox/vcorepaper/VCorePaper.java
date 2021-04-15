package de.verdox.vcorepaper;

import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcorepaper.subsystems.VCoreTestSubsystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.List;

public class VCorePaper extends VCorePlugin.Minecraft implements Listener {

    @Override
    public void onPluginEnable() {
        getSessionManager();
        Bukkit.getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onPluginDisable() {

    }

    @Override
    public List<VCoreSubsystem.Bukkit> provideSubsystems() {
        return Arrays.asList(new VCoreTestSubsystem(this));
    }

    @Override
    public boolean useRedisCluster() {
        return false;
    }

    @Override
    public String[] redisAddresses() {
        return new String[]{"redis://localhost:6379"};
    }

    @Override
    public DataConnection.MongoDB mongoDB() {
        return new DataConnection.MongoDB(this,"localhost","vcore",27017,"","") {
            @Override
            public void onConnect() {
                consoleMessage("&eMongoDB successfully connected!");
            }

            @Override
            public void onDisconnect() { }
        };
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){

    }
}
