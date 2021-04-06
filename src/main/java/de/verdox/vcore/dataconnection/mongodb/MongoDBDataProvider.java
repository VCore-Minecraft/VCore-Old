package de.verdox.vcore.dataconnection.mongodb;

import com.mongodb.DBCollection;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.dataconnection.DataProvider;

public abstract class MongoDBDataProvider implements DataProvider<DBCollection> {

    private DataConnection.MongoDB mongoDBDataConnection;

    public MongoDBDataProvider(DataConnection.MongoDB mongoDBDataConnection){
        this.mongoDBDataConnection = mongoDBDataConnection;
    }

    public DataConnection.MongoDB getMongoDBDataConnection() {
        return mongoDBDataConnection;
    }
}
