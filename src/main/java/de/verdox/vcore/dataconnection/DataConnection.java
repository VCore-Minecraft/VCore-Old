package de.verdox.vcore.dataconnection;

import de.verdox.vcore.dataconnection.mongodb.MongoDBDataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.DummySubSystem;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.dataconnection.mysql.MySQLDataConnection;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;

import java.util.HashMap;
import java.util.Map;

public abstract class DataConnection <D,R extends DataProvider<D>> {

    private final Map<Class<?>,R> providers = new HashMap<>();
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

    public void registerDataProvider(R dataProvider){
        this.providers.put(dataProvider.getClass(),dataProvider);
        loadData(dataProvider);
    }

    private void loadData(R dataProvider){
        subsystem.getVCorePlugin().consoleMessage("Initializing table&7: &b"+dataProvider);
    }

    public VCoreSubsystem<?> getSubsystem() {
        return subsystem;
    }

    public VCorePlugin<?, ?> getVCorePlugin() {
        return vCorePlugin;
    }

    public R getDataProvider(Class<? extends R> providerType){
        if(!this.providers.containsKey(providerType))
            return null;
        return this.providers.get(providerType);
    }

     public abstract static class MySQL extends MySQLDataConnection {
         public MySQL(VCoreSubsystem<?> subsystem, String host, String databaseName, String userName, String password, int port, int maxPoolSize) {
             super(subsystem, host, databaseName, userName, password, port, maxPoolSize);
         }

         public MySQL(VCorePlugin<?,?> vCorePlugin, String host, String databaseName, String userName, String password, int port, int maxPoolSize) {
             super(vCorePlugin, host, databaseName, userName, password, port, maxPoolSize);
         }
     }


    public abstract static class MongoDB extends MongoDBDataConnection {
        public MongoDB(VCoreSubsystem<?> subsystem, String host, String database, int port, String user, String password) {
            super(subsystem, host, database, port, user, password);
        }

        public MongoDB(VCorePlugin<?,?> vCorePlugin, String host, String database, int port, String user, String password) {
            super(vCorePlugin, host, database, port, user, password);
        }
    }

}
