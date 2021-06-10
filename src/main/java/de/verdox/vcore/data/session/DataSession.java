package de.verdox.vcore.data.session;

import com.mongodb.client.MongoCollection;
import de.verdox.vcore.data.annotations.DataContext;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.manager.VCoreDataManager;
import de.verdox.vcore.data.session.datahandler.database.DatabaseHandler;
import de.verdox.vcore.data.session.datahandler.database.DatabaseHandlerImpl;
import de.verdox.vcore.data.session.datahandler.local.LocalDataHandler;
import de.verdox.vcore.data.session.datahandler.redis.RedisHandler;
import de.verdox.vcore.data.session.datahandler.redis.RedisHandlerImpl;
import org.bson.Document;

import java.util.*;

public abstract class DataSession <S extends VCoreData> {

    protected final VCoreDataManager<S,?> dataManager;
    private UUID uuid;
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

    public S loadFromPipeline(Class<? extends S> dataClass, UUID objectUUID){
        if(dataClass == null || objectUUID == null)
            return null;

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
                getDatabaseHandler().dataBaseToRedis(dataClass, objectUUID);
                getRedisHandler().redisToLocal(dataClass,objectUUID);
            }
            else {
                getDatabaseHandler().databaseToLocal(dataClass,objectUUID);
            }
        }
        else {
            dataManager.getPlugin().consoleMessage("&eNo Data was found. Creating new data! &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            S vCoreData = dataManager.instantiateVCoreData(dataClass,objectUUID);
            getLocalDataHandler().addDataLocally(vCoreData,dataClass,true);
            getLocalDataHandler().localToRedis(vCoreData,dataClass,vCoreData.getUUID());
        }
        dataManager.getPlugin().consoleMessage("&eLoaded &a"+dataClass.getSimpleName()+" &ewith uuid&7: "+objectUUID, 1,true);
        return getLocalDataHandler().getDataLocal(dataClass,objectUUID);
    }

    public void saveToPipeline(Class<? extends S> dataClass, UUID objectUUID){
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

    public void saveAndRemoveLocally(Class<? extends S> dataClass, UUID objectUUID){
        saveToPipeline(dataClass,objectUUID);
        getLocalDataHandler().removeDataLocally(dataClass,objectUUID,true);
    }


    //TODO: First search in Redis then search in MongoDB
    protected void loadAllDataFromDatabaseToPipeline(Class<? extends S> dataClass){
        dataManager.getPlugin().consoleMessage("&ePreloading Data for &a"+dataClass.getSimpleName()+" &efrom database&7!",true);
        getMongoCollection(dataClass)
                .find()
                .iterator()
                .forEachRemaining(document -> {
                    UUID uuid = UUID.fromString(document.getString("_id"));
                    loadFromPipeline(dataClass,uuid);
                });
    }

    public final MongoCollection<Document> getMongoCollection(Class<? extends S> dataClass){
        return dataManager.getRedisManager().getMongoDB().getDataStorage(dataClass,getMongoDBSuffix());
    }
    public abstract String getMongoDBSuffix();

    public abstract void onLoad();
    public abstract void onCleanUp();

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
