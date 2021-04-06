package de.verdox.vcore.dataconnection;

import de.verdox.vcore.dataconnection.mongodb.MongoDBDataProvider;
import de.verdox.vcore.dataconnection.mysql.MySQLDataProvider;
import de.verdox.vcore.data.datatypes.PlayerData;

public interface DataProvider<T> {

    void onConnect();
    void onDisconnect();
    void loadGlobalData(T dataReceiver);
    PlayerData createPlayerData(T dataReceiver);
    void hasPlayerData(T dataReceiver, PlayerData playerData);
    void loadPlayerData(T dataReceiver, PlayerData playerData);
    void updatePlayerData(T dataReceiver, PlayerData playerData);
    void deletePlayerData(T dataReceiver, PlayerData playerData);

    String identifier();

    abstract class MySQLTable extends MySQLDataProvider {
        public MySQLTable(DataConnection.MySQL mySQLDataConnection) {
            super(mySQLDataConnection);
        }
    }

    abstract class MongoDBCollection extends MongoDBDataProvider {
        public MongoDBCollection(DataConnection.MongoDB mongoDBDataConnection){
            super(mongoDBDataConnection);
        }
    }

}
