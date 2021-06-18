package de.verdox.vcore.dataconnection;

import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.dataconnection.mongodb.MongoDBDataConnection;
import de.verdox.vcore.plugin.VCorePlugin;

import java.util.Objects;

/**
 * Type of DataStorage (e.g. MongoCollection)
 * @param <T>
 */
public abstract class DataConnection <T> {
    private final VCorePlugin<?, ?> vCorePlugin;

    protected final String host;
    protected final String database;
    protected final int port;
    protected final String user;
    protected final String password;

    public DataConnection(VCorePlugin<?,?> vCorePlugin, String host, String database, int port, String user, String password){
        this.vCorePlugin = vCorePlugin;
        this.host = host;
        this.database = database;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    protected abstract void connect();
    protected abstract void onConnect();

    public VCorePlugin<?, ?> getVCorePlugin() {
        return vCorePlugin;
    }

    public abstract T getDataStorage(Class<? extends VCoreData> dataClass, String suffix);


    public abstract static class MongoDB extends MongoDBDataConnection {
        public MongoDB(VCorePlugin<?,?> vCorePlugin, String host, String database, int port, String user, String password) {
            super(vCorePlugin, host, database, port, user, password);
        }
    }
}
