/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.dataconnection.storage;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 16:00
 */
public class MongoDBStorage implements GlobalStorage, RemoteStorage{


    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private String host;
    private String database;
    private int port;
    private String user;
    private String password;
    //private final CodecRegistry codecRegistry;

    public MongoDBStorage(VCorePlugin<?,?> vCorePlugin, String host, String database, int port, String user, String password) {
        this.host = host;
        this.database = database;
        this.port = port;
        this.user = user;
        this.password = password;
        vCorePlugin.consoleMessage("&6Starting MongoDB Manager",true);
        //this.codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),CodecRegistries.fromProviders(new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY)));
        connect();
    }

    public MongoDBStorage(VCorePlugin<?,?> vCorePlugin, String host, String database, int port) {
        this(vCorePlugin,host,database,port,"","");
    }


    @Override
    public Map<String, Object> loadData(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        Document filter = new Document("objectUUID",objectUUID.toString());

        Document mongoDBData = getMongoStorage(dataClass,getSuffix(dataClass)).find(filter).first();

        if(mongoDBData == null)
            mongoDBData = filter;
        Map<String, Object> dataFromDatabase = new HashMap<>();
        mongoDBData.forEach(dataFromDatabase::put);
        return dataFromDatabase;
    }

    @Override
    public boolean dataExist(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        Document document = getMongoStorage(dataClass, getSuffix(dataClass)).find(new Document("objectUUID",objectUUID.toString())).first();
        return document != null;
    }

    @Override
    public void save(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID, @Nonnull Map<String, Object> dataToSave) {
        Document filter = new Document("objectUUID",objectUUID.toString());

        MongoCollection<Document> collection = getMongoStorage(dataClass,getSuffix(dataClass));

        if(collection.find(filter).first() == null){
            Document newData = new Document("objectUUID",objectUUID.toString());
            newData.putAll(dataToSave);
            collection.insertOne(newData);
        }
        else {
            Document newData = new Document(dataToSave);
            Document updateFunc = new Document("$set",newData);
            collection.updateOne(filter,updateFunc);
        }
    }

    @Override
    public boolean remove(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        Document filter = new Document("objectUUID",objectUUID.toString());

        MongoCollection<Document> collection = getMongoStorage(dataClass,getSuffix(dataClass));
        return collection.deleteOne(filter).getDeletedCount() >= 1;
    }

    @Override
    public Set<UUID> getSavedUUIDs(@Nonnull Class<? extends VCoreData> dataClass) {
        return null;
    }

    private MongoCollection<Document> getMongoStorage(Class<? extends VCoreData> dataClass, String suffix){
        Class<? extends VCoreSubsystem<?>> subsystemClass = VCorePlugin.findDependSubsystemClass(dataClass);
        if(subsystemClass == null)
            throw new NullPointerException("Dependent Subsystem Annotation not set. ["+dataClass.getCanonicalName()+"]");
        String mongoIdentifier = VCorePlugin.getMongoDBIdentifier(subsystemClass);
        if(mongoIdentifier == null)
            throw new NullPointerException("MongoDBIdentifier Annotation not set. ["+subsystemClass.getCanonicalName()+"]");
        return getCollection(VCorePlugin.getMongoDBIdentifier(subsystemClass)+suffix);
    }

    private final com.mongodb.client.MongoCollection<Document> getCollection(String name){
        try {
            return mongoDatabase.getCollection(name);
        }
        // Collection does not exist
        catch (IllegalArgumentException e){
            mongoDatabase.createCollection(name);
            return getCollection(name);
        }
    }

    @Override
    public void connect() {
        if(user.isEmpty() && password.isEmpty())
            this.mongoClient = new MongoClient(host,port);
        else
            this.mongoClient = new MongoClient(new ServerAddress(host,port), List.of(MongoCredential.createCredential(user,database,password.toCharArray())));
        this.mongoDatabase = mongoClient.getDatabase(database);
    }

    @Override
    public void disconnect() {

    }

    private String getSuffix(Class<? extends VCoreData> dataClass){
        if(dataClass.isAssignableFrom(PlayerData.class))
            return "PlayerData";
        else if(dataClass.isAssignableFrom(ServerData.class))
            return "ServerData";
        else
            return "Unknown";
    }
}
