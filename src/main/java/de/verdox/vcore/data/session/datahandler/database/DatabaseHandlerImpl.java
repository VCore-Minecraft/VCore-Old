/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.session.datahandler.database;

import com.mongodb.client.MongoCollection;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.session.DataSession;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.06.2021 13:50
 */
public class DatabaseHandlerImpl <S extends VCoreData> extends DatabaseHandler<S> {

    public DatabaseHandlerImpl(DataSession<S> dataSession) {
        super(dataSession);
    }

    public final Map<String, Object> loadDataFromDatabase(Class<? extends S> dataClass, UUID objectUUID){
        Document filter = new Document("objectUUID",objectUUID.toString());

        Document mongoDBData = dataSession.getMongoCollection(dataClass).find(filter).first();

        if(mongoDBData == null)
            mongoDBData = filter;
        Map<String, Object> dataFromDatabase = new HashMap<>();
        mongoDBData.forEach(dataFromDatabase::put);
        return dataFromDatabase;
    }

    public final boolean dataExistInDatabase(Class<? extends S> dataClass, UUID objectUUID){
        Document document = dataSession.getMongoCollection(dataClass).find(new Document("objectUUID",objectUUID.toString())).first();
        return document != null;
    }

    public final void saveToDatabase(Class<? extends S> dataClass, UUID objectUUID, Map<String, Object> dataToSave){
        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
        if(dataToSave == null)
            return;
        if(dataToSave.isEmpty())
            return;
        Set<String> dataKeysToSave = dataSession.getRedisHandler().getRedisKeys(dataClass,objectUUID);
        if(dataKeysToSave == null)
            return;
        //dataSession.getLocalDataHandler().getDataLocal(dataClass,objectUUID).cleanUp();

        dataKeysToSave
                .stream()
                .filter(s -> !dataToSave.containsKey(s))
                .forEach(dataToSave::remove);

        MongoCollection<Document> mongoCollection = dataSession.getMongoCollection(dataClass);

        Document filter = new Document("objectUUID",objectUUID.toString());

        if(mongoCollection.find(filter).first() == null){
            Document newData = new Document("objectUUID",objectUUID.toString());
            newData.putAll(dataToSave);
            mongoCollection.insertOne(newData);
        }
        else {
            Document newData = new Document(dataToSave);
            Document updateFunc = new Document("$set",newData);
            mongoCollection.updateOne(filter,updateFunc);
        }
    }
}
