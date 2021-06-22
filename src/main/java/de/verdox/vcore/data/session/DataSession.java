package de.verdox.vcore.data.session;

import com.mongodb.client.MongoCollection;
import de.verdox.vcore.data.annotations.DataContext;
import de.verdox.vcore.data.annotations.PreloadStrategy;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.manager.VCoreDataManager;
import de.verdox.vcore.data.session.datahandler.database.DatabaseHandler;
import de.verdox.vcore.data.session.datahandler.database.DatabaseHandlerImpl;
import de.verdox.vcore.data.session.datahandler.local.LocalDataHandler;
import de.verdox.vcore.data.session.datahandler.redis.RedisHandler;
import de.verdox.vcore.data.session.datahandler.redis.RedisHandlerImpl;
import de.verdox.vcore.plugin.VCorePlugin;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class DataSession <S extends VCoreData> {

    protected final VCoreDataManager<S,?> dataManager;
    private final UUID uuid;
    private final LocalDataHandler<S> localDataHandler;
    private final RedisHandler<S> redisHandler;
    private final DatabaseHandler<S> databaseHandler;

    public DataSession(VCoreDataManager<S,?> dataManager, UUID uuid){
        this.dataManager = dataManager;
        this.uuid = uuid;
        this.localDataHandler = setupLocalHandler();
        this.redisHandler = new RedisHandlerImpl<>(this);
        this.databaseHandler = new DatabaseHandlerImpl<>(this);
        onLoad();
    }

    public final UUID getUuid() {
        return uuid;
    }

    public final void cleanUp(){
        onCleanUp();
    }

    public final synchronized <T extends S> T loadFromPipeline(@Nonnull Class<? extends T> dataClass, @Nonnull UUID objectUUID, boolean createIfNotExist){
        if(getLocalDataHandler().dataExistLocally(dataClass,objectUUID)) {
            dataManager.getPlugin().consoleMessage("&eFound Data in Local Cache &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
        }
        else if(getRedisHandler().dataExistRedis(dataClass,objectUUID)) {
            dataManager.getPlugin().consoleMessage("&eFound Data in Redis Cache &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            getRedisHandler().redisToLocal(dataClass, objectUUID);
        }
        else if(getDatabaseHandler().dataExistInDatabase(dataClass,objectUUID)) {
            dataManager.getPlugin().consoleMessage("&eFound Data in Database &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            if(dataManager.getRedisManager().getContext(dataClass).equals(DataContext.GLOBAL)){
                getDatabaseHandler().databaseToLocal(dataClass, objectUUID);
                getDatabaseHandler().dataBaseToRedis(dataClass, objectUUID);
            }
            else {
                getDatabaseHandler().databaseToLocal(dataClass,objectUUID);
            }
        }
        else {
            if(!createIfNotExist)
                return null;
            dataManager.getPlugin().consoleMessage("&eNo Data was found. Creating new data! &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            S vCoreData = dataManager.instantiateVCoreData(dataClass,objectUUID);
            getLocalDataHandler().addDataLocally(vCoreData,dataClass,true);
            getLocalDataHandler().localToRedis(vCoreData,dataClass,vCoreData.getUUID());
        }
        dataManager.getPlugin().consoleMessage("&eCaller&7: &b"+getClass().getSimpleName()+" &7| &b"+getUuid()+"&7 >> &eLoaded &a"+dataClass.getSimpleName()+" &ewith uuid&7: "+objectUUID, 1,true);
        if(!getLocalDataHandler().dataExistLocally(dataClass, objectUUID))
            throw new NullPointerException("Error in dataPipeline while loading "+dataClass+" with uuid "+uuid);
        return getLocalDataHandler().getDataLocal(dataClass,objectUUID);
    }

    public final synchronized <T extends S> T loadFromPipeline(@Nonnull Class<? extends T> dataClass, @Nonnull UUID objectUUID){
        return loadFromPipeline(dataClass, objectUUID, false);
    }

    public synchronized void saveToPipeline(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID){
        if(dataManager.getRedisManager().getContext(dataClass).equals(DataContext.GLOBAL)){
            if(!getRedisHandler().dataExistRedis(dataClass,objectUUID))
                //TODO: Es wird hier nur in die Datenbank gespeichert, wenn remote die Daten noch existieren,
                // um zu verhindern, dass lokal noch Reste bestehen die nicht existieren sollten
                return;
            getRedisHandler().redisToDatabase(dataClass,objectUUID);
        }
        else {
            getLocalDataHandler().localToDatabase(dataClass,objectUUID);
        }
    }

    public void saveAndRemoveLocally(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID){
        saveToPipeline(dataClass,objectUUID);
        getLocalDataHandler().removeDataLocally(dataClass,objectUUID,true);
    }


    protected void loadAllFromDatabase(@Nonnull Class<? extends S> dataClass){
        dataManager.getPlugin().consoleMessage("&ePreloading Data for &a"+dataClass.getSimpleName()+" &efrom database&7!",true);
        getMongoCollection(dataClass)
                .find()
                .iterator()
                .forEachRemaining(document -> {
                    if(document == null)
                        return;
                    UUID uuid = UUID.fromString(document.getString("objectUUID"));
                    if(getLocalDataHandler().dataExistLocally(dataClass, uuid))
                        return;
                    getDatabaseHandler().databaseToLocal(dataClass, uuid);
                    dataManager.getPlugin().consoleMessage("&eLoaded &a"+dataClass.getSimpleName()+" &efrom database with uuid &b"+uuid+"&7!",1, true);
                });

    }

    protected void loadAllFromRedis(@Nonnull Class<? extends S> dataClass){
        dataManager.getPlugin().consoleMessage("&ePreloading Data for &a"+dataClass.getSimpleName()+" &efrom redis&7!",true);
        getRedisHandler().getSavedRedisData(dataClass)
                .stream()
                .filter(redisCachedUUID -> !getLocalDataHandler().dataExistLocally(dataClass, redisCachedUUID))
                .forEach(redisCachedUUID -> {
                    getRedisHandler().redisToLocal(dataClass, redisCachedUUID);
                    dataManager.getPlugin().consoleMessage("&eLoaded &a"+dataClass.getSimpleName()+" &efrom redis with uuid &b"+redisCachedUUID+"&7!",1, true);
                });
    }

    public final MongoCollection<Document> getMongoCollection(@Nonnull Class<? extends S> dataClass){
        return dataManager.getRedisManager().getMongoDB().getDataStorage(dataClass,getMongoDBSuffix());
    }
    public abstract String getMongoDBSuffix();

    public abstract void preloadData();
    public abstract void onLoad();
    public abstract void onCleanUp();
    public abstract void saveAllData();

    public abstract void debugToConsole();

    public final LocalDataHandler<S> getLocalDataHandler() {
        return localDataHandler;
    }

    public RedisHandler<S> getRedisHandler() {
        return redisHandler;
    }

    public DatabaseHandler<S> getDatabaseHandler() {
        return databaseHandler;
    }

    protected abstract LocalDataHandler<S> setupLocalHandler();

    public VCoreDataManager<S, ?> getDataManager() {
        return dataManager;
    }
}
