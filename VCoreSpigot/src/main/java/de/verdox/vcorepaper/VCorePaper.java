/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import de.verdox.vcore.synchronization.networkmanager.player.api.bukkit.VCorePlayerAPIBukkitImpl;
import de.verdox.vcore.synchronization.networkmanager.player.listener.PlayerBukkitListener;
import de.verdox.vcore.synchronization.networkmanager.server.ServerType;
import de.verdox.vcore.synchronization.networkmanager.server.api.VCoreServerAPI;
import de.verdox.vcore.synchronization.networkmanager.server.api.VCoreServerAPIImpl;
import de.verdox.vcorepaper.commands.AdminCommands;
import de.verdox.vcorepaper.commands.NMSCommand;
import de.verdox.vcorepaper.commands.PlayerAPICommands;
import de.verdox.vcorepaper.commands.TalkingNPCCommand;
import de.verdox.vcorepaper.custom.CustomDataListener;
import de.verdox.vcorepaper.custom.block.CustomBlockDataManager;
import de.verdox.vcorepaper.custom.block.CustomBlockProvider;
import de.verdox.vcorepaper.custom.block.CustomLocationDataManager;
import de.verdox.vcorepaper.custom.block.data.debug.BlockDebugData;
import de.verdox.vcorepaper.custom.block.internal.VBlockListener;
import de.verdox.vcorepaper.custom.entities.CustomEntityListener;
import de.verdox.vcorepaper.custom.entities.CustomEntityManager;
import de.verdox.vcorepaper.custom.events.paper.CustomPaperEventListener;
import de.verdox.vcorepaper.custom.items.CustomItemManager;
import de.verdox.vcorepaper.custom.nbtholders.location.LocationNBTFileStorage;
import de.verdox.vcorepaper.custom.talkingnpc.TalkingNPCListener;
import de.verdox.vcorepaper.nms.NMSManager;
import net.roxeez.advancement.AdvancementManager;
import org.bukkit.Bukkit;

import java.util.List;

public class VCorePaper extends VCoreCoreInstance.Minecraft {

    //TODO: Integrieren possibly: https://www.spigotmc.org/resources/mapapi.93343
    //TODO: Standard Commands mit ner Config /discord /teamspeak /forum /wiki

    public static VCorePaper instance;

    private final AdvancementManager manager = new AdvancementManager(this);

    private NMSManager nmsManager;

    private NetworkManager<VCorePaper> networkManager;

    private CustomEntityManager customEntityManager;
    private CustomItemManager customItemManager;

    private CustomBlockProvider customBlockProvider;
    private CustomBlockDataManager customBlockDataManager;
    private CustomLocationDataManager customLocationDataManager;

    private ProtocolManager protocolManager;

    private VCorePlayerAPI vCorePlayerAPI;
    private VCoreServerAPI vCoreServerAPI;

    private LocationNBTFileStorage locationNBTFileStorage;

    public static VCorePaper getInstance() {
        return instance;
    }

    @Override
    public void onPluginEnable() {
        instance = this;

        Bukkit.advancementIterator().forEachRemaining(advancement -> {
            Bukkit.getUnsafe().removeAdvancement(advancement.getKey());
        });

        Bukkit.reloadData();

        this.vCorePlayerAPI = new VCorePlayerAPIBukkitImpl(this);
        this.vCoreServerAPI = new VCoreServerAPIImpl(this);
        this.nmsManager = new NMSManager(this);

        this.customEntityManager = new CustomEntityManager(this);
        this.customItemManager = new CustomItemManager(this);

        this.customBlockProvider = new CustomBlockProvider(this);
        this.customBlockDataManager = customBlockProvider.getBlockDataManager();
        this.customLocationDataManager = customBlockProvider.getLocationDataManager();
        getServices().eventBus.register(new VCorePaperMainListener(this));

        this.locationNBTFileStorage = new LocationNBTFileStorage(this);

        new CustomDataListener(this);
        new CustomEntityListener(this);
        new VBlockListener(this);
        new CustomPaperEventListener(this);
        new TalkingNPCListener(this);


        new AdminCommands(this, "debug");
        new NMSCommand(this, "nms");
        new PlayerAPICommands(this, "playerapi");
        new TalkingNPCCommand(this, "talkingNPC");

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
            protocolManager = ProtocolLibrary.getProtocolManager();

        networkManager = new NetworkManager<>(ServerType.GAME_SERVER, this);
        new PlayerBukkitListener(networkManager);
        if (networkManager.getServerCache().isServerNameTaken(getServerName())) {
            consoleMessage("&4<> ============================================= <>", false);
            consoleMessage("", false);
            consoleMessage("&cThe Server &e" + getServerName() + " &cis already online in this VCore Network &7(&bExists in Global Cache&7)", false);
            consoleMessage("&cShutting down other server&7!", false);
            consoleMessage("", false);
            consoleMessage("&4<> ============================================= <>", false);
            vCoreServerAPI.remoteShutdown(getServerName(), true);
        }
        networkManager.getServerPingManager().sendOnlinePing();
        getCustomLocationDataManager().registerData(BlockDebugData.class);
    }

    public ProtocolManager getProtocolManager() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null)
            throw new IllegalStateException("ProtocolLib could not be found on this server.");
        return protocolManager;
    }

    @Override
    public void onPluginDisable() {
        locationNBTFileStorage.shutdown();
        networkManager.shutdown();
    }

    @Override
    public List<VCoreSubsystem.Bukkit> provideSubsystems() {
        return null;
    }

    public CustomEntityManager getCustomEntityManager() {
        return customEntityManager;
    }

    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    public CustomBlockProvider getCustomBlockManager() {
        return customBlockProvider;
    }

    public CustomLocationDataManager getCustomLocationDataManager() {
        return customLocationDataManager;
    }

    public CustomBlockDataManager getCustomBlockDataManager() {
        return customBlockDataManager;
    }

    public void asyncThenSync(Runnable asyncTask, Runnable syncTask) {
        async(() -> {
            asyncTask.run();
            sync(syncTask);
        });
    }

    public void syncThenAsync(Runnable syncTask, Runnable asyncTask) {
        sync(() -> {
            syncTask.run();
            async(asyncTask);
        });
    }

    public NMSManager getNmsManager() {
        return nmsManager;
    }

    @Override
    public void shutdown() {

    }

    public NetworkManager<VCorePaper> getNetworkManager() {
        return networkManager;
    }

    @Override
    public VCorePlayerAPI getPlayerAPI() {
        return vCorePlayerAPI;
    }

    @Override
    public VCoreServerAPI getServerAPI() {
        return this.vCoreServerAPI;
    }

    @Override
    public String getServerName() {

        return getNetworkManager().getServerPingManager().getServerName();
    }

    public AdvancementManager getAdvancementManager() {
        return manager;
    }

    public LocationNBTFileStorage getBlockFileStorage() {
        return locationNBTFileStorage;
    }
}