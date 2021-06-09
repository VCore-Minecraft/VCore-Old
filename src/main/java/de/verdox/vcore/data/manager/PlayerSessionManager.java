package de.verdox.vcore.data.manager;

import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.session.PlayerSession;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlayerSessionManager<R extends VCorePlugin<?,?>> extends VCoreDataManager<PlayerData, R> {

    protected final Map<UUID, PlayerSession> playerSessionCache = new ConcurrentHashMap<>();

    public PlayerSessionManager(R plugin, boolean useRedisCluster, String[] addressArray, String redisPassword, DataConnection.MongoDB mongoDB){
        super(plugin,useRedisCluster,addressArray,redisPassword,mongoDB);
        getPlugin().consoleMessage("&eStarting Session Manager",true);
    }

    protected PlayerSession createSession(UUID uuid){
        //getPlugin().consoleMessage("&eCreating Local Player Session&7: &b"+uuid);
        if(exist(uuid))
            return getSession(uuid);
        PlayerSession playerSession = new PlayerSession(this,uuid);
        this.playerSessionCache.put(uuid, playerSession);
        return playerSession;
    }

    @Override
    public Set<PlayerData> getAllData(Class<? extends PlayerData> dataClass) {
        Set<PlayerData> dataSet = new HashSet<>();
        playerSessionCache
                .values()
                .stream()
                .map(playerSession -> playerSession.getAllData(dataClass))
                .forEach(dataSet::addAll);
        return dataSet;
    }

    @Override
    public PlayerData load(Class<? extends PlayerData> type, UUID uuid) {
        return getSession(uuid).loadFromPipeline(type,uuid);
    }

    protected PlayerSession deleteSession(UUID uuid){
        //getPlugin().consoleMessage("&eDeleting Local Player Session&7: &b"+uuid);
        PlayerSession playerSession = getSession(uuid);
        playerSession.getPlayerDataObjects().forEach((aClass, playerData) -> playerData.pushUpdate());
        playerSession.cleanUp();
        this.playerSessionCache.remove(uuid);
        return playerSession;
    }

    public boolean exist(UUID playerUUID){
        return playerSessionCache.containsKey(playerUUID);
    }

    public PlayerSession getSession(UUID playerUUID){
        return playerSessionCache.get(playerUUID);
    }

    protected Map<Class<? extends PlayerData>, Set<String>> getAllPlayerKeys(PlayerSession playerSession){
        Map<Class<? extends PlayerData>, Set<String>> keys = new HashMap<>();
        if(playerSession == null)
            throw new NullPointerException("PlayerSession can't be null!");
        plugin.getSubsystemManager().getRegisteredPlayerDataClasses()
                .forEach(aClass -> keys.put(aClass, playerSession.getRedisKeys(aClass,playerSession.getUuid())));
        return keys;
    }

    @Override
    protected void onCleanupInterval() {
        plugin.getSubsystemManager().getActivePlayerDataClasses().forEach(aClass -> {
            playerSessionCache.values().forEach(playerSession -> playerSession.getAllData(aClass).forEach(playerData -> {
                if(System.currentTimeMillis() - playerData.getLastUse() <= 1000L*1800)
                    return;
                // Wurde das Datum in den letzten 1800 Sekunden nicht genutzt wird es in Redis geladen
                playerData.getResponsibleDataSession().saveAndRemoveLocally(playerData.getClass(),playerData.getUUID());
            }));
        });
    }

    protected void loginPipeline(UUID playerUUID){
        long start = System.currentTimeMillis();
            Map<Class<? extends PlayerData>,Set<String>> keys = getAllPlayerKeys(createSession(playerUUID));
            getPlugin().consoleMessage("&eHandling Player Join&7: &a"+playerUUID.toString(),true);

            keys.forEach((playerDataClass, dataKeys) -> {
                getPlugin().consoleMessage("&a"+playerDataClass.getSimpleName(),true);
                RequiredSubsystemInfo requiredSubsystemInfo = playerDataClass.getAnnotation(RequiredSubsystemInfo.class);
                if(requiredSubsystemInfo == null)
                    throw new RuntimeException(getClass().getName()+" does not have PlayerDataClassInfo Annotation set");
                VCoreSubsystem<?> subsystem = plugin.getSubsystemManager().findSubsystemByClass(requiredSubsystemInfo.parentSubSystem());
                if(subsystem == null) {
                    throw new IllegalStateException("Subsystem could not be found!");
                }
                if(!subsystem.isActivated()) {
                    System.out.println(subsystem +" is deactivated!");
                    return;
                }
                getSession(playerUUID).loadFromPipeline(playerDataClass,playerUUID);
            });
            long end = System.currentTimeMillis() - start;
            getPlugin().consoleMessage("&eSuccessfully loaded data&7: &a"+playerUUID.toString() + " &7[&e"+end+" ms&7]",true);
    }

    protected void logoutPipeline(UUID playerUUID){
        getPlugin().consoleMessage("&eUnloading PlayerSession&7: &a"+playerUUID,true);
        deleteSession(playerUUID);
    }

    @Override
    public PlayerData instantiateVCoreData(Class<? extends PlayerData> dataClass, UUID objectUUID){
        try {
            return dataClass.getDeclaredConstructor(PlayerSessionManager.class,UUID.class).newInstance(this,objectUUID);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
            return null;
        }
    }

    public R getPlugin() {
        return plugin;
    }

    public static class BukkitPlayerSessionManager extends PlayerSessionManager<VCorePlugin.Minecraft> implements Listener {

        public BukkitPlayerSessionManager(VCorePlugin.Minecraft plugin, boolean useRedisCluster, String[] addressArray, String redisPassword, DataConnection.MongoDB mongoDB) {
            super(plugin, useRedisCluster, addressArray, redisPassword, mongoDB);
            plugin.getPlugin().getServer().getPluginManager().registerEvents(this,plugin);
        }

        public PlayerSession getPlayerSession(Player player){
            return getSession(player.getUniqueId());
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent e){
            Player player = e.getPlayer();
            plugin.async(() -> loginPipeline(player.getUniqueId()));
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent e){
            getPlugin().async(() -> logoutPipeline(e.getPlayer().getUniqueId()));
        }

        @EventHandler
        public void onPlayerKick(PlayerKickEvent e){
            getPlugin().async(() -> logoutPipeline(e.getPlayer().getUniqueId()));
        }
    }

    public static class BungeePlayerSessionManager extends PlayerSessionManager<VCorePlugin.BungeeCord> implements net.md_5.bungee.api.plugin.Listener {
        public BungeePlayerSessionManager(VCorePlugin.BungeeCord plugin, boolean useRedisCluster, String[] addressArray, String redisPassword, DataConnection.MongoDB mongoDB) {
            super(plugin, useRedisCluster, addressArray, redisPassword, mongoDB);
            ProxyServer.getInstance().getPluginManager().registerListener(plugin,this);
        }

        public PlayerSession getPlayerSession(ProxiedPlayer player){
            return getSession(player.getUniqueId());
        }

        @net.md_5.bungee.event.EventHandler
        public void onJoin(PostLoginEvent e){
            loginPipeline(e.getPlayer().getUniqueId());
        }

        @net.md_5.bungee.event.EventHandler
        public void onDisconnect(PlayerDisconnectEvent e){
            getPlugin().async(() -> logoutPipeline(e.getPlayer().getUniqueId()));
        }
    }
}
