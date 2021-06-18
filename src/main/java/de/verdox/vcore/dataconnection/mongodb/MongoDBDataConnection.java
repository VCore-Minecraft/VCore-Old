package de.verdox.vcore.dataconnection.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.dataconnection.DataProvider;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class MongoDBDataConnection extends DataConnection<MongoCollection<Document>> {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MongoDBDataConnection(VCorePlugin<?,?> vCorePlugin, String host, String database, int port, String user, String password) {
        super(vCorePlugin,host,database,port,user,password);
        vCorePlugin.consoleMessage("&6Starting MongoDB Manager",true);
        connect();
    }

    public MongoDBDataConnection(VCorePlugin<?,?> vCorePlugin, String host, String database, int port) {
        this(vCorePlugin,host,database,port,"","");
    }

    @Override
    protected void connect() {
        if(user.isEmpty() && password.isEmpty())
            this.mongoClient = new MongoClient(host,port);
        else
            this.mongoClient = new MongoClient(new ServerAddress(host,port), List.of(MongoCredential.createCredential(user,database,password.toCharArray())));
        this.mongoDatabase = mongoClient.getDatabase(database);
        onConnect();
    }

    @Override
    public MongoCollection<Document> getDataStorage(Class<? extends VCoreData> dataClass, String suffix) {
        Class<? extends VCoreSubsystem<?>> subsystemClass = VCorePlugin.findDependSubsystemClass(dataClass);
        if(subsystemClass == null)
            throw new NullPointerException("Dependent Subsystem Annotation not set. ["+dataClass.getCanonicalName()+"]");
        String mongoIdentifier = VCorePlugin.getMongoDBIdentifier(subsystemClass);
        if(mongoIdentifier == null)
            throw new NullPointerException("MongoDBIdentifier Annotation not set. ["+subsystemClass.getCanonicalName()+"]");
        return getCollection(VCorePlugin.getMongoDBIdentifier(subsystemClass)+suffix);
    }

    protected final MongoCollection<Document> getCollection(String name){
        try {
            return mongoDatabase.getCollection(name);
        }
        // Collection does not exist
        catch (IllegalArgumentException e){
            mongoDatabase.createCollection(name);
            return getCollection(name);
        }
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
