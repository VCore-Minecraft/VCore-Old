package de.verdox.vcore.data.manager;

import com.fasterxml.jackson.databind.util.JSONPObject;
import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.events.PlayerPreSessionLoadEvent;
import de.verdox.vcore.data.events.PlayerPreSessionUnloadEvent;
import de.verdox.vcore.data.events.PlayerSessionLoadedEvent;
import de.verdox.vcore.data.session.PlayerSession;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.redisson.messages.RedisSimpleMessage;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class PlayerSessionManager<R extends VCorePlugin<?,?>> extends VCoreDataManager<PlayerData, R> {

    protected final Map<UUID, PlayerSession> playerSessionCache = new ConcurrentHashMap<>();

    public PlayerSessionManager(R plugin, boolean useRedisCluster, String[] addressArray, String redisPassword, DataConnection.MongoDB mongoDB){
        super(plugin,useRedisCluster,addressArray,redisPassword,mongoDB);
        getPlugin().consoleMessage("&eStarting PlayerSessionManager",false);
        loaded = true;
    }

    protected PlayerSession createSession(@Nonnull UUID uuid){
        //getPlugin().consoleMessage("&eCreating Local Player Session&7: &b"+uuid);
        if(exist(uuid))
            return getSession(uuid);
        PlayerSession playerSession = new PlayerSession(this,uuid);
        this.playerSessionCache.put(uuid, playerSession);
        return playerSession;
    }

    @Override
    public void saveAllData(){
        playerSessionCache.forEach((uuid, playerSession) -> playerSession.saveAllData());
    }

    @Override
    public <U extends PlayerData> Set<U> getAllData(@Nonnull Class<? extends U> dataClass) {
        Set<U> dataSet = new HashSet<>();
        playerSessionCache
                .values()
                .stream()
                .map(playerSession -> playerSession.getLocalDataHandler().getAllLocalData(dataClass))
                .forEach(dataSet::addAll);
        return dataSet;
    }

    @Override
    public <T extends PlayerData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist, @Nullable Consumer<T> callback) {
        PlayerSession playerSession = getSession(uuid);
        if (playerSession == null) {
            if(loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL))
                throw new NullPointerException("There is no local Session for Player with uuid: "+uuid+". Check first!");
            else
                playerSession = createSession(uuid);
        }
        if(!loadingStrategy.equals(LoadingStrategy.LOAD_PIPELINE)) {
            if(!playerSession.getLocalDataHandler().dataExistLocally(type, uuid)){
                if(loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL))
                    throw new NullPointerException("Data with type "+type+" and uuid "+uuid+" is not cached locally. Check first!");
                else{
                    PlayerSession finalPlayerSession = playerSession;
                    plugin.async(() -> {
                        T data = finalPlayerSession.loadFromPipeline(type, uuid, createIfNotExist);
                        if(callback != null)
                            callback.accept(data);
                    });
                }
            }
            if(!playerSession.getLocalDataHandler().dataExistLocally(type, uuid))
                return null;
            return playerSession.getLocalDataHandler().getDataLocal(type, uuid);
        }
        return playerSession.loadFromPipeline(type, uuid, createIfNotExist);
    }

    protected PlayerSession deleteSession(@Nonnull UUID uuid){
        //getPlugin().consoleMessage("&eDeleting Local Player Session&7: &b"+uuid);
        PlayerSession playerSession = getSession(uuid);
        playerSession.getPlayerDataObjects().forEach((aClass, playerData) -> playerData.pushUpdate(true));
        playerSession.cleanUp();
        this.playerSessionCache.remove(uuid);
        return playerSession;
    }

    public boolean exist(@Nonnull UUID playerUUID){
        return playerSessionCache.containsKey(playerUUID);
    }

    public PlayerSession getSession(@Nonnull UUID playerUUID){
        return playerSessionCache.get(playerUUID);
    }

    protected Map<Class<? extends PlayerData>, Set<String>> getAllPlayerKeys(PlayerSession playerSession){
        Map<Class<? extends PlayerData>, Set<String>> keys = new HashMap<>();
        if(playerSession == null)
            throw new NullPointerException("PlayerSession can't be null!");
        plugin.getSubsystemManager().getRegisteredPlayerDataClasses()
                .forEach(aClass -> keys.put(aClass, playerSession.getRedisHandler().getRedisKeys(aClass,playerSession.getUuid())));
        return keys;
    }

    @Override
    protected void onCleanupInterval() {
        plugin.getSubsystemManager().getActivePlayerDataClasses().forEach(aClass -> {
            playerSessionCache.values().forEach(playerSession -> playerSession.getLocalDataHandler().getAllLocalData(aClass).forEach(playerData -> {
                if(System.currentTimeMillis() - playerData.getLastUse() <= 1000L*1800)
                    return;
                // Wurde das Datum in den letzten 1800 Sekunden nicht genutzt wird es in Redis geladen
                playerData.getResponsibleDataSession().saveAndRemoveLocally(playerData.getClass(),playerData.getUUID());
            }));
        });
    }

    protected void loginPipeline(UUID playerUUID){
        long start = System.currentTimeMillis();
        PlayerSession playerSession = createSession(playerUUID);
            Map<Class<? extends PlayerData>,Set<String>> keys = getAllPlayerKeys(playerSession);
            getPlugin().consoleMessage("&eHandling Player Join&7: &a"+playerUUID,true);

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
            long success = System.currentTimeMillis();
            long end = success - start;
        getPlugin().getEventBus().post(new PlayerSessionLoadedEvent(playerSession, success));
            getPlugin().consoleMessage("&eSuccessfully loaded data&7: &a"+playerUUID + " &7[&e"+end+" ms&7]",true);
    }

    protected void logoutPipeline(UUID playerUUID){
        getPlugin().consoleMessage("&eUnloading PlayerSession&7: &a"+playerUUID,true);
        deleteSession(playerUUID);
    }

    @Override
    public PlayerData instantiateVCoreData(@Nonnull Class<? extends PlayerData> dataClass, UUID objectUUID){

        if(VCorePlugin.findDependSubsystemClass(dataClass) == null)
            throw new NullPointerException(dataClass+" does not have RequiredSubsystem Annotation set.");
        if(objectUUID == null)
            throw new NullPointerException("objectUUID can't be null!");
        VCoreSubsystem<?> subsystem = plugin.findDependSubsystem(dataClass);
        if(subsystem == null)
            throw new NullPointerException("Provided Subsystem can't be null");
        if(!subsystem.isActivated())
            throw new NullPointerException("Provided Subsystem is not activated");

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
        public void preConnect(AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent){
            if(plugin.isLoaded())
                return;
            asyncPlayerPreLoginEvent.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            asyncPlayerPreLoginEvent.setKickMessage(ChatColor.translateAlternateColorCodes('&',"&eDer Server fährt gerade hoch&7!"));
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent e){
            Player player = e.getPlayer();
            getPlugin().getEventBus().post(new PlayerPreSessionLoadEvent(e.getPlayer().getUniqueId()));
            plugin.async(() -> {
                getPlugin().getServerDataManager().broadcastRedisMessage(new RedisSimpleMessage.Builder("connection", "minecraft", "join").setDataToSend(player.getUniqueId(),player.getName()));
                loginPipeline(player.getUniqueId());
            });

        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent e){
            Player player = e.getPlayer();
            getPlugin().getEventBus().post(new PlayerPreSessionUnloadEvent(e.getPlayer().getUniqueId()));
            getPlugin().async(() -> {
                getPlugin().getServerDataManager().broadcastRedisMessage(new RedisSimpleMessage.Builder("connection", "minecraft", "leave").setDataToSend(player.getUniqueId(),player.getName()));
                logoutPipeline(e.getPlayer().getUniqueId());
            });
        }

        @EventHandler
        public void onPlayerKick(PlayerKickEvent e){
            Player player = e.getPlayer();
            getPlugin().getEventBus().post(new PlayerPreSessionUnloadEvent(e.getPlayer().getUniqueId()));
            getPlugin().async(() -> {
                getPlugin().getServerDataManager().broadcastRedisMessage(new RedisSimpleMessage.Builder("connection", "minecraft", "kick").setDataToSend(player.getUniqueId(),player.getName()));
                logoutPipeline(e.getPlayer().getUniqueId());
            });
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
        public void onPreLogin(PreLoginEvent e){
            if(plugin.isLoaded())
                return;
            e.setCancelReason(new TextComponent(ChatColor.translateAlternateColorCodes('&',"&eDer server fährt gerade hoch&7!")));
            e.setCancelled(true);
        }

        @net.md_5.bungee.event.EventHandler
        public void onJoin(PostLoginEvent e){
            ProxiedPlayer player = e.getPlayer();
            plugin.getEventBus().post(new PlayerPreSessionLoadEvent(e.getPlayer().getUniqueId()));
            getPlugin().async(() -> {
                getPlugin().getServerDataManager().broadcastRedisMessage(new RedisSimpleMessage.Builder("connection", "bungee", "join").setDataToSend(player.getUniqueId(),player.getName()));
                loginPipeline(e.getPlayer().getUniqueId());
            });
        }

        @net.md_5.bungee.event.EventHandler
        public void onDisconnect(PlayerDisconnectEvent e){
            ProxiedPlayer player = e.getPlayer();
            plugin.getEventBus().post(new PlayerPreSessionUnloadEvent(e.getPlayer().getUniqueId()));
            getPlugin().async(() -> {
                getPlugin().getServerDataManager().broadcastRedisMessage(new RedisSimpleMessage.Builder("connection", "bungee", "leave").setDataToSend(player.getUniqueId(),player.getName()));
                logoutPipeline(e.getPlayer().getUniqueId());
            });
        }
    }
}
