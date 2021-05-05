package de.verdox.vcore.dataconnection.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;
import org.bson.Document;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MongoDBDataConnection extends DataConnection<MongoCollection<Document>> {

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public MongoDBDataConnection(VCoreSubsystem<?> subsystem, String host, String database, int port, String user, String password) throws SubsystemDeactivatedException {
        super(subsystem);
        VCoreSubsystem.checkSubsystem(subsystem);

        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        subsystem.getVCorePlugin().consoleMessage("&6Starting MongoDB Manager",true);
        if(user.isEmpty() && password.isEmpty())
            this.mongoClient = new MongoClient(host,port);
        else
            this.mongoClient = new MongoClient(new ServerAddress(host,port), List.of(MongoCredential.createCredential(user,database,password.toCharArray())));
        this.mongoDatabase = mongoClient.getDatabase(database);
        onConnect();
    }

    public MongoDBDataConnection(VCorePlugin<?,?> vCorePlugin, String host, String database, int port, String user, String password) {
        super(vCorePlugin);

        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        vCorePlugin.consoleMessage("&6Starting MongoDB Manager",true);
        if(user.isEmpty() && password.isEmpty())
            this.mongoClient = new MongoClient(host,port);
        else
            this.mongoClient = new MongoClient(new ServerAddress(host,port), List.of(MongoCredential.createCredential(user,database,password.toCharArray())));
        this.mongoDatabase = mongoClient.getDatabase(database);
        try { onConnect(); } catch (SubsystemDeactivatedException e) { e.printStackTrace(); }
    }

    public MongoDBDataConnection(VCoreSubsystem<?> subsystem, String host, String database, int port) throws SubsystemDeactivatedException {
        super(subsystem);

        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        VCoreSubsystem.checkSubsystem(subsystem);
        subsystem.getVCorePlugin().consoleMessage("&6Starting MongoDB Manager",true);
        this.mongoClient = new MongoClient(host,port);
        this.mongoDatabase = mongoClient.getDatabase(database);
        onConnect();
    }

    public MongoDBDataConnection(VCorePlugin<?,?> vCorePlugin, String host, String database, int port) {
        super(vCorePlugin);

        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        vCorePlugin.consoleMessage("&6Starting MongoDB Manager",true);
        this.mongoClient = new MongoClient(host,port);
        this.mongoDatabase = mongoClient.getDatabase(database);
        try { onConnect(); } catch (SubsystemDeactivatedException e) { e.printStackTrace(); }
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

    @Override
    public MongoCollection<Document> getDataProvider(Class<? extends VCoreData> dataClass, String suffix) {
        Class<? extends VCoreSubsystem<?>> subsystemClass = VCorePlugin.findDependSubsystemClass(dataClass);
        if(subsystemClass == null)
            throw new NullPointerException("Dependent Subsystem Annotation not set. ["+dataClass.getCanonicalName()+"]");
        String mongoIdentifier = VCorePlugin.getMongoDBIdentifier(subsystemClass);
        if(mongoIdentifier == null)
            throw new NullPointerException("MongoDBIdentifier Annotation not set. ["+subsystemClass.getCanonicalName()+"]");
        return getCollection(VCorePlugin.getMongoDBIdentifier(subsystemClass)+suffix);
    }
}
