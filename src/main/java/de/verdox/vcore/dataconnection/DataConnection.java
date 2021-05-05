package de.verdox.vcore.dataconnection;

import com.mongodb.client.MongoCollection;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.dataconnection.mongodb.MongoDBDataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.DummySubSystem;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.dataconnection.mysql.MySQLDataConnection;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public abstract class DataConnection <T> {

    private final VCoreSubsystem<?> subsystem;
    private final VCorePlugin<?, ?> vCorePlugin;

    public DataConnection(VCoreSubsystem<?> subsystem){
        this.subsystem = subsystem;
        this.vCorePlugin = subsystem.getVCorePlugin();
    }
    public DataConnection(VCorePlugin<?,?> vCorePlugin){
        this.vCorePlugin = vCorePlugin;

        this.subsystem = new DummySubSystem(vCorePlugin);
    }

    public abstract void connect() throws SubsystemDeactivatedException;
    public abstract void onConnect() throws SubsystemDeactivatedException;
    public abstract void disconnect() throws SubsystemDeactivatedException;
    public abstract void onDisconnect() throws SubsystemDeactivatedException;
    public abstract void saveAllPlayers();

    public VCoreSubsystem<?> getSubsystem() {
        return subsystem;
    }

    public VCorePlugin<?, ?> getVCorePlugin() {
        return vCorePlugin;
    }

    public abstract T getDataProvider(Class<? extends VCoreData> dataClass, String suffix);

     public abstract static class MySQL extends MySQLDataConnection {
         public MySQL(VCoreSubsystem<?> subsystem, String host, String databaseName, String userName, String password, int port, int maxPoolSize) {
             super(subsystem, host, databaseName, userName, password, port, maxPoolSize);
         }

         public MySQL(VCorePlugin<?,?> vCorePlugin, String host, String databaseName, String userName, String password, int port, int maxPoolSize) {
             super(vCorePlugin, host, databaseName, userName, password, port, maxPoolSize);
         }
     }


    public abstract static class MongoDB extends MongoDBDataConnection {
        public MongoDB(VCoreSubsystem<?> subsystem, String host, String database, int port, String user, String password) throws SubsystemDeactivatedException {
            super(subsystem, host, database, port, user, password);
        }

        public MongoDB(VCorePlugin<?,?> vCorePlugin, String host, String database, int port, String user, String password) {
            super(vCorePlugin, host, database, port, user, password);
        }
    }

}
