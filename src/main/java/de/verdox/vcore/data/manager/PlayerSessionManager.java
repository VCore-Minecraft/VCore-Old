package de.verdox.vcore.data.manager;

import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.session.PlayerSession;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.redisson.api.RMap;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlayerSessionManager<R extends VCorePlugin<?,?>> extends VCoreDataManager<PlayerData, R> {

    protected final Map<UUID, PlayerSession> playerSessionCache = new ConcurrentHashMap<>();

    public PlayerSessionManager(R plugin, boolean useRedisCluster, String[] addressArray, DataConnection.MongoDB mongoDB){
        super(plugin,useRedisCluster,addressArray,mongoDB);
        getPlugin().consoleMessage("&eStarting Session Manager");
    }

    protected PlayerSession createSession(UUID uuid){
        getPlugin().consoleMessage("&eCreating Local Player Session&7: &b"+uuid);
        if(exist(uuid))
            return getSession(uuid);
        return this.playerSessionCache.put(uuid, new PlayerSession(this,uuid));
    }

    protected PlayerSession deleteSession(UUID uuid){
        getPlugin().consoleMessage("&eDeleting Local Player Session&7: &b"+uuid);
        PlayerSession playerSession = this.playerSessionCache.remove(uuid);
        playerSession.cleanUp();
        return playerSession;
    }

    public boolean exist(UUID playerUUID){
        return playerSessionCache.containsKey(playerUUID);
    }

    public PlayerSession getSession(UUID playerUUID){
        return playerSessionCache.get(playerUUID);
    }

    protected Map<Class<? extends PlayerData>, Set<String>> getAllPlayerKeys(UUID playerUUID){
        Map<Class<? extends PlayerData>, Set<String>> keys = new HashMap<>();
        PlayerSession playerSession = getSession(playerUUID);

        plugin.getSubsystemManager().getRegisteredPlayerDataClasses()
                .forEach(aClass -> keys.put(aClass, playerSession.getRedisKeys(aClass,playerUUID)));
        return keys;
    }

    protected void loginPipeline(UUID playerUUID){
        long start = System.currentTimeMillis();
            createSession(playerUUID);

            Map<Class<? extends PlayerData>,Set<String>> keys = getAllPlayerKeys(playerUUID);
            getPlugin().consoleMessage("&eHandling Player Join&7: &a"+playerUUID.toString());

            keys.forEach((playerDataClass, dataKeys) -> {

                RequiredSubsystemInfo requiredSubsystemInfo = playerDataClass.getAnnotation(RequiredSubsystemInfo.class);
                if(requiredSubsystemInfo == null)
                    throw new RuntimeException(getClass().getName()+" does not have PlayerDataClassInfo Annotation set");

                getSession(playerUUID).load(playerDataClass,playerUUID);


              // VCoreSubsystem<?> subsystem = plugin.getSubsystemManager().findSubsystemByClass(requiredSubsystemInfo.parentSubSystem());
              // RMap<String,Object> playerSessionDataCache = playerSessionCache.get(playerUUID).getRedisCache(playerDataClass);

              // if(subsystem.isActivated()){
              //     dataKeys.parallelStream()
              //             .filter(dataKey -> !playerSessionDataCache.containsKey(dataKey))
              //             .forEach(dataKey -> playerSession.dataBaseToRedis(playerDataClass,playerUUID));

              //     getPlugin().consoleMessage("&eLoading Data &b"+VCorePlugin.getMongoDBIdentifier(playerDataClass));
              //     playerSession.loadFromRedis(playerDataClass, playerSession.getPlayerUUID());
              // }
              // else {
              //     getPlugin().consoleMessage("&eSaving to database &b"+VCorePlugin.getMongoDBIdentifier(playerDataClass));
              //     playerSession.redisToDatabase(playerDataClass,playerUUID,dataKeys);
              // }
            });
            long end = System.currentTimeMillis() - start;
            getPlugin().consoleMessage("&eSuccessfully loaded data&7: &a"+playerUUID.toString() + " &7[&e"+end+" ms&7]");
    }

    protected void logoutPipeline(UUID playerUUID){
        getPlugin().consoleMessage("&eUnloading PlayerSession&7: &a"+playerUUID);
        deleteSession(playerUUID);
    }

    //protected void databaseToRedisPipeline(VCoreSubsystem<?> subsystem, Class<? extends PlayerData> playerDataClass, UUID playerUUID, Map<String, Object> playerSessionDataCache){
    //    MongoCollection<Document> mongoCollection = redisManager.getMongoDB().getCollection(VCorePlugin.getMongoDBIdentifier(subsystem.getClass()));
//
    //    PlayerData playerData = instantiatePlayerData(playerDataClass,playerUUID);
//
    //    Document mongoDBData = mongoCollection.find(new Document("playerUUID",playerUUID.toString())).first();
    //    if(mongoDBData == null)
    //        mongoDBData = new Document("playerUUID", playerUUID.toString());
//
    //    Map<String, Object> dataFromDatabase = new HashMap<>();
//
    //    mongoDBData.forEach((key, data) -> {
    //        if(!key.contains(":"))
    //            return;
    //        String[]split = key.split(":");
    //        if(!split[0].equals(VCorePlugin.getMongoDBIdentifier(playerDataClass)))
    //            return;
    //        dataFromDatabase.put(key.split(":")[1],data);
    //    });
//
    //    playerData.restoreFromDataBase(dataFromDatabase);
//
    //    playerData.dataForRedis().forEach(playerSessionDataCache::put);
    //}

   //protected void redisToDatabasePipeline(Map<String, Object> playerSessionDataCache, VCoreSubsystem<?> subsystem, UUID playerUUID, Set<String> keys){
   //    if(playerSessionDataCache == null)
   //        throw new NullPointerException("playerSessionDataCache can't be null!");

   //    MongoCollection<Document> mongoCollection = redisManager.getMongoDB().getCollection(VCorePlugin.getMongoDBIdentifier(subsystem.getClass()));

   //    keys.parallelStream().filter(playerSessionDataCache::containsKey).forEach(dataKey -> {

   //        Document playerData = new Document("playerUUID",playerUUID.toString());
   //        playerData.putAll(playerSessionDataCache);

   //        mongoCollection.insertOne(playerData);

   //        // Removing from Redis
   //        playerSessionDataCache.remove(dataKey);
   //    });
   //}

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

        public BukkitPlayerSessionManager(VCorePlugin.Minecraft plugin, boolean useRedisCluster, String[] addressArray, DataConnection.MongoDB mongoDB) {
            super(plugin, useRedisCluster, addressArray,mongoDB);
            plugin.getPlugin().getServer().getPluginManager().registerEvents(this,plugin);
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
        public BungeePlayerSessionManager(VCorePlugin.BungeeCord plugin, boolean useRedisCluster, String[] addressArray, DataConnection.MongoDB mongoDB) {
            super(plugin, useRedisCluster, addressArray, mongoDB);
            ProxyServer.getInstance().getPluginManager().registerListener(plugin,this);
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
