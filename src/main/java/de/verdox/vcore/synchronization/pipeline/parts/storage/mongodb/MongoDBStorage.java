/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.storage.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.synchronization.pipeline.parts.storage.RemoteStorage;
import de.verdox.vcore.util.global.AnnotationResolver;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 16:00
 */
public class MongoDBStorage implements GlobalStorage, RemoteStorage {


    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private final VCorePlugin<?, ?> vCorePlugin;
    private final String host;
    private final String database;
    private final int port;
    private final String user;
    private final String password;
    //private final CodecRegistry codecRegistry;

    public MongoDBStorage(VCorePlugin<?, ?> vCorePlugin, String host, String database, int port, String user, String password) {
        Objects.requireNonNull(vCorePlugin, "vCorePlugin can't be null!");
        Objects.requireNonNull(host, "host can't be null!");
        Objects.requireNonNull(database, "database can't be null!");
        Objects.requireNonNull(user, "user can't be null!");
        Objects.requireNonNull(password, "password can't be null!");
        this.vCorePlugin = vCorePlugin;
        this.host = host;
        this.database = database;
        this.port = port;
        this.user = user;
        this.password = password;
        //this.codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),CodecRegistries.fromProviders(new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY)));

        connect();
        vCorePlugin.consoleMessage("&eMongoDB Global Storage started", true);
    }

    public MongoDBStorage(VCorePlugin<?, ?> vCorePlugin, String host, String database, int port) {
        this(vCorePlugin, host, database, port, "", "");
    }


    @Override
    public synchronized Map<String, Object> loadData(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        Document filter = new Document("objectUUID", objectUUID.toString());

        Document mongoDBData = getMongoStorage(dataClass, getSuffix(dataClass)).find(filter).first();

        if (mongoDBData == null)
            mongoDBData = filter;
        return convertDocumentToHashMap(mongoDBData);
    }

    private Map<String, Object> convertDocumentToHashMap(@NotNull Object object) {
        Objects.requireNonNull(object, "object can't be null!");
        if (!(object instanceof Document))
            return null;
        Document document = (Document) object;
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(document);
        map.remove("_id");
        document.forEach((s, o) -> {
            Map<String, Object> possibleFoundDocument = convertDocumentToHashMap(o);
            if (possibleFoundDocument == null)
                return;
            map.put(s, possibleFoundDocument);
        });
        return map;
    }

    @Override
    public synchronized boolean dataExist(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        Document document = getMongoStorage(dataClass, getSuffix(dataClass)).find(new Document("objectUUID", objectUUID.toString())).first();
        return document != null;
    }

    @Override
    public synchronized void save(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID, @NotNull Map<String, Object> dataToSave) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        Objects.requireNonNull(dataToSave, "dataToSave can't be null!");
        Document filter = new Document("objectUUID", objectUUID.toString());

        MongoCollection<Document> collection = getMongoStorage(dataClass, getSuffix(dataClass));

        if (collection.find(filter).first() == null) {
            Document newData = new Document("objectUUID", objectUUID.toString());
            newData.putAll(dataToSave);
            collection.insertOne(newData);
        } else {
            Document newData = new Document(dataToSave);
            Document updateFunc = new Document("$set", newData);
            collection.updateOne(filter, updateFunc);
        }
    }

    @Override
    public synchronized boolean remove(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        Document filter = new Document("objectUUID", objectUUID.toString());

        MongoCollection<Document> collection = getMongoStorage(dataClass, getSuffix(dataClass));
        return collection.deleteOne(filter).getDeletedCount() >= 1;
    }

    @Override
    public synchronized Set<UUID> getSavedUUIDs(@NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        MongoCollection<Document> collection = getMongoStorage(dataClass, getSuffix(dataClass));
        Set<UUID> uuids = new HashSet<>();
        for (Document document : collection.find()) {
            if (!document.containsKey("objectUUID"))
                continue;
            uuids.add(UUID.fromString((String) document.get("objectUUID")));
        }
        return uuids;
    }

    private synchronized MongoCollection<Document> getMongoStorage(@NotNull Class<? extends VCoreData> dataClass, @NotNull String suffix) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(suffix, "suffix can't be null!");
        if (NetworkData.class.isAssignableFrom(dataClass)) {
            return getCollection("VCore_NetworkData_" + dataClass.getCanonicalName() + "_" + suffix);
        } else {
            Class<? extends VCoreSubsystem<?>> subsystemClass = AnnotationResolver.findDependSubsystemClass(dataClass);
            if (subsystemClass == null)
                throw new NullPointerException("Dependent Subsystem Annotation not set. [" + dataClass.getCanonicalName() + "]");
            String mongoIdentifier = AnnotationResolver.getDataStorageIdentifier(subsystemClass);
            if (mongoIdentifier == null)
                throw new NullPointerException("MongoDBIdentifier Annotation not set. [" + subsystemClass.getCanonicalName() + "]");
            return getCollection(AnnotationResolver.getDataStorageIdentifier(subsystemClass) + "_" + suffix);
        }
    }

    private synchronized com.mongodb.client.MongoCollection<Document> getCollection(@NotNull String name) {
        Objects.requireNonNull(name, "name can't be null!");
        try {
            return mongoDatabase.getCollection(name);
        }
        // Collection does not exist
        catch (IllegalArgumentException e) {
            mongoDatabase.createCollection(name);
            return mongoDatabase.getCollection(name);
        }
    }

    @Override
    public void connect() {
        vCorePlugin.consoleMessage("&6Trying to connect to MongoDB&7: &b" + host + "&7:&b" + port, false);
        if (user.isEmpty() && password.isEmpty())
            this.mongoClient = new MongoClient(host, port);
        else
            this.mongoClient = new MongoClient(new ServerAddress(host, port), List.of(MongoCredential.createCredential(user, database, password.toCharArray())));
        this.mongoDatabase = mongoClient.getDatabase(database);
    }

    @Override
    public void disconnect() {
        this.mongoClient.close();
    }

    private String getSuffix(@NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        return AnnotationResolver.getDataStorageIdentifier(dataClass);
    }
}
