package de.verdox.vcore.dataconnection.mysql;

import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.dataconnection.DataProvider;

import java.sql.Connection;

public abstract class MySQLDataProvider implements DataProvider<Connection> {
    private final DataConnection.MySQL mySQLDataConnection;

    public MySQLDataProvider(DataConnection.MySQL mySQLDataConnection){
        this.mySQLDataConnection = mySQLDataConnection;
    }

    public DataConnection.MySQL getMySQLDataConnection() {
        return mySQLDataConnection;
    }
}