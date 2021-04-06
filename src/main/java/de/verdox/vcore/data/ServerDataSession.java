package de.verdox.vcore.data;

import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.events.RedisDataCreationEvent;
import de.verdox.vcore.data.events.RedisDataRemoveEvent;
import de.verdox.vcore.data.manager.ServerDataManager;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class ServerDataSession {

  //  private final ServerDataManager<?> serverDataManager;
  //  private final VCoreSubsystem<?> subsystem;
  //  private final RMap<String,Object> sessionCache;
  //  private final Map<Class<? extends ServerData>, Map<UUID,ServerData>> serverDataObjects;
  //  private final RTopic objectHandlerTopic;
  //  private final MessageListener<ObjectHandlerMessage<? extends ServerData>> channelListener;
//
  //  public ServerDataSession (ServerDataManager<?> serverDataManager, VCoreSubsystem<?> vCoreSubsystem){
  //      this.serverDataManager = serverDataManager;
  //      this.subsystem = vCoreSubsystem;
  //      this.sessionCache = serverDataManager.getRedisManager().getRedissonClient().getMap("VCoreServerData:"+VCorePlugin.getMongoDBIdentifier(vCoreSubsystem.getClass()));
  //      serverDataObjects = new ConcurrentHashMap<>();
  //      //TODO: Hier den Channel für das Erstellen von Objekten reinlunzen
  //      objectHandlerTopic = serverDataManager.getRedisManager().getObjectHandlerTopic(vCoreSubsystem);
//
  //      channelListener = (channel, msg) -> {
  //          if(msg.getType() == msg.INSERT)
  //              serverDataManager.getPlugin().getEventBus().post(new RedisDataCreationEvent(msg.getDataType(),msg.getUuid()));
//
  //          else if(msg.getType() == msg.DELETE)
  //              serverDataManager.getPlugin().getEventBus().post(new RedisDataRemoveEvent(msg.getDataType(),msg.getUuid()));
  //      };
  //      objectHandlerTopic.addListener(ObjectHandlerMessage.class,channelListener);
  //  }
//
  //  public <S extends ServerData> void insertObject(S serverDataObject, Class<? extends S> type, boolean push){
  //      if(containsData(serverDataObject.getClass(),serverDataObject.getUUID()))
  //          return;
  //      if(!serverDataObjects.containsKey(serverDataObject.getClass()))
  //          serverDataObjects.put(serverDataObject.getClass(),new ConcurrentHashMap<>());
  //      //TODO: Hier das Erstellen von neuen Daten reinpushen
  //      serverDataObjects.get(serverDataObject.getClass()).put(serverDataObject.getUUID(),serverDataObject);
//
  //      if(push)
  //          pushCreation(type,serverDataObject);
  //  }
//
  //  public <S extends ServerData> void deleteObject(Class<? extends S> type, UUID uuid, boolean push){
  //      if(!containsData(type,uuid))
  //          return;
  //      serverDataObjects.get(type).remove(uuid);
  //      if(serverDataObjects.get(type).size() == 0)
  //          serverDataObjects.remove(type);
  //      //TODO: Hier das Löschen von Daten reinpushen
//
  //      if(push)
  //          pushRemoval(type,uuid);
  //  }
//
  //  public <S extends ServerData> S getServerData(Class<? extends S> type, UUID uuid){
  //      if(!containsData(type,uuid))
  //          return null;
  //      return (S) serverDataObjects.get(type).get(uuid);
  //  }
//
  //  //TODO: DATEN RICHTIG IN LOKALEN CACHE LADEN
//
  //  public ServerData loadServerData(Class<? extends ServerData> type, UUID uuid){
  //      if(containsData(type,uuid))
  //          return getServerData(type,uuid);
//
  //      Map<String, Object> dataFromRedis = new HashMap<>();
//
  //      //TODO: Load from Mongo to Redis
//
  //      serverDataManager.getRedisKeys(type,uuid).forEach(dataKey -> {
  //          dataFromRedis.put(dataKey,sessionCache.get(dataKey));
  //      });
//
  //      ServerData serverData = serverDataManager.instantiateVCoreData(type,uuid);
  //      serverData.restoreFromRedis(dataFromRedis);
  //      return serverData;
  //  }
//
  //  public <S extends ServerData> boolean containsData(Class<? extends S> type, UUID uuid){
  //      if(!serverDataObjects.containsKey(type))
  //          return false;
  //      return serverDataObjects.get(type).containsKey(uuid);
  //  }
//
  //  private <S extends ServerData> void pushCreation(Class<? extends S> type, S serverDataObject){
  //      //TODO: Hier wird das Object in Redis gespeichert.
  //      // Außerdem wird die Erstellung selbst gepushed
  //      ObjectHandlerMessage<S> message = new ObjectHandlerMessage<S>(type)
  //              .setInsert()
  //              .setUUID(serverDataObject.getUUID())
  //              .create();
//
  //      serverDataObject.dataForRedis().forEach((s, o) -> {
  //          sessionCache.put(redisObjectKey(serverDataObject.getClass(),serverDataObject.getUUID())+":"+s.split(":")[1],o);
  //      });
  //      //sessionCache.put(redisObjectKey(serverDataObject.getClass(),serverDataObject.getUUID()),serverDataObject.dataForRedis());
  //      objectHandlerTopic.publishAsync(message);
  //  }
//
  //  RMap<String, Object> getSessionCache() {
  //      return sessionCache;
  //  }
//
  //  private <S extends ServerData> void pushRemoval(Class<? extends S> type, UUID uuid){
  //      ObjectHandlerMessage<S> message = new ObjectHandlerMessage<S>(type)
  //              .setDelete()
  //              .setUUID(uuid)
  //              .create();
//
  //      getServerData(type,uuid).dataForRedis().forEach((s, o) -> {
  //          sessionCache.remove(redisObjectKey(type,uuid)+":"+s);
  //      });
//
  //      //TODO: Objekt aus Datenbank löschen
//
  //      //Außerdem wird die Löschung gepushed
  //      objectHandlerTopic.publishAsync(message);
  //  }
//
  //  private String redisObjectKey(Class<? extends ServerData> classType, UUID uuid){
  //      return VCorePlugin.getMongoDBIdentifier(classType)+":"+uuid.toString();
  //  }
//
  //  static class ObjectHandlerMessage <S extends ServerData> implements Serializable {
//
  //      private final int INSERT = 1;
  //      private final int DELETE = -1;
//
  //      private int type = -2;
  //      private Class<? extends S> dataType;
  //      private UUID uuid;
//
  //      public ObjectHandlerMessage(Class<? extends S> dataType){
  //          this.dataType = dataType;
  //      }
//
  //      public ObjectHandlerMessage<S> setInsert(){
  //          this.type = INSERT;
  //          return this;
  //      }
//
  //      public ObjectHandlerMessage<S> setDelete(){
  //          this.type = DELETE;
  //          return this;
  //      }
//
  //      public ObjectHandlerMessage<S> setUUID(UUID uuid){
  //          this.uuid = uuid;
  //          return this;
  //      }
//
  //      public ObjectHandlerMessage<S> create(){
  //          if(this.uuid == null)
  //              throw new NullPointerException("UUID can't be null!");
  //          if(type == -2)
  //              throw new IllegalArgumentException("Type must be set!");
  //          return this;
  //      }
//
  //      public UUID getUuid() {
  //          return uuid;
  //      }
//
  //      public int getType() {
  //          return type;
  //      }
//
  //      public Class<? extends S> getDataType() {
  //          return dataType;
  //      }
  //  }
}
