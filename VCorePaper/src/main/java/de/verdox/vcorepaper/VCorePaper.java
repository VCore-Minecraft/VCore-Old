/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcorepaper;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.verdox.vcore.modules.VCoreModuleLoader;
import de.verdox.vcore.modules.VCoreModuleLoaderImpl;
import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import de.verdox.vcore.synchronization.networkmanager.server.ServerType;
import de.verdox.vcore.synchronization.networkmanager.server.api.VCoreServerAPI;
import de.verdox.vcore.synchronization.networkmanager.server.api.VCoreServerAPIImpl;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcorepaper.commands.AdminCommands;
import de.verdox.vcorepaper.commands.PlayerAPICommands;
import de.verdox.vcorepaper.custom.economy.EconomyContainer;
import de.verdox.vcorepaper.impl.network.VCorePaperPlayerAPIImpl;
import de.verdox.vcorepaper.impl.listener.VCorePaperPlayerPingListener;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import de.verdox.vcorepaper.impl.plugin.VCorePaperSubsystem;
import de.verdox.vcorepaper.module.VCorePaperModule;
import net.roxeez.advancement.AdvancementManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VCorePaper extends VCorePaperPlugin implements VCoreCoreInstance<JavaPlugin, VCorePaperSubsystem> {

    //TODO: Integrieren possibly: https://www.spigotmc.org/resources/mapapi.93343
    //TODO: Standard Commands mit ner Config /discord /teamspeak /forum /wiki
    // Wenn Server während Laufzeit gestartet ist muss der Spielercache irgendwie aktualisiert werden

    public static VCorePaper instance;

    private final AdvancementManager manager = new AdvancementManager(this);

    private NetworkManager<VCorePaper> networkManager;

    private ProtocolManager protocolManager;

    private VCorePlayerAPI vCorePlayerAPI;
    private VCoreServerAPI vCoreServerAPI;
    private EconomyContainer economyContainer = new EconomyContainer();

    private VCoreModuleLoader<JavaPlugin, VCorePaperSubsystem, VCorePaper, VCorePaperModule> moduleLoader;

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

        moduleLoader = new VCoreModuleLoaderImpl<>(this);

        this.vCorePlayerAPI = new VCorePaperPlayerAPIImpl(this);
        this.vCoreServerAPI = new VCoreServerAPIImpl(this);

        getServices().eventBus.register(new VCorePaperMainListener(this));

        new AdminCommands(this, "debug");
        new PlayerAPICommands(this, "playerapi");

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
            protocolManager = ProtocolLibrary.getProtocolManager();

        networkManager = new NetworkManager<>(this, ServerType.GAME_SERVER);
        new VCorePaperPlayerPingListener(networkManager);
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
        getInstance().getServices().getPipeline().loadAllData(VCorePlayer.class, Pipeline.LoadingStrategy.LOAD_PIPELINE);
    }

    public void setupEconomy() {
        if (org.bukkit.Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        economyContainer = new EconomyContainer();
    }

    @Nullable
    public EconomyContainer getEconomyContainer() {
        return economyContainer;
    }

    public ProtocolManager getProtocolManager() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null)
            throw new IllegalStateException("ProtocolLib could not be found on this server.");
        return protocolManager;
    }

    @Override
    public void onPluginDisable() {

        networkManager.shutdown();
    }

    @Override
    public List<VCorePaperSubsystem> provideSubsystems() {
        return null;
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

    @Override
    public VCoreModuleLoader<JavaPlugin, VCorePaperSubsystem, VCorePaper, VCorePaperModule> getModuleLoader() {
        return moduleLoader;
    }
}