package de.verdox.vcore.dataconnection.mongodb;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.dataconnection.DataProvider;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;
import org.bson.Document;

public abstract class MongoDBDataConnection extends DataConnection<DBCollection,DataProvider.MongoDBCollection> {

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public MongoDBDataConnection(VCoreSubsystem<?> subsystem, String host, String database, int port, String user, String password) {
        super(subsystem);
        this.mongoClient = new MongoClient(host,port);
        this.mongoDatabase = mongoClient.getDatabase(database);
    }

    public MongoDBDataConnection(VCorePlugin<?,?> vCorePlugin, String host, String database, int port, String user, String password) {
        super(vCorePlugin);
        this.mongoClient = new MongoClient(host,port);
        this.mongoDatabase = mongoClient.getDatabase(database);
    }

    @Override
    public void connect() throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubsystem());

    }

    @Override
    public void disconnect() throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubsystem());
    }

    @Override
    public void saveAllPlayers() {

    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public MongoCollection<Document> getCollection(String name){
        try {
            return getMongoDatabase().getCollection(name);
        }
        // Collection does not exist
        catch (IllegalArgumentException e){
            getMongoDatabase().createCollection(name);
            return getCollection(name);
        }

    }
}
