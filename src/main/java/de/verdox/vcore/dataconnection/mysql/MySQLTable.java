package de.verdox.vcore.dataconnection.mysql;

import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;

import java.sql.*;
import java.util.UUID;

public class MySQLTable {

    private final DataConnection.MySQL mySQL;
    private Class<? extends VCoreData> dataClass;
    private String suffix;

    public MySQLTable(DataConnection.MySQL mySQL, Class<? extends VCoreData> dataClass, String suffix) throws SQLException {
        this.mySQL = mySQL;
        this.dataClass = dataClass;
        this.suffix = suffix;
        initTable(mySQL.getConnection());
    }

    private void initTable(Connection connection) throws SQLException {
        if(!createTable(connection))
            alterTable(connection);
    }

    private String getMySQLTableName(){
        return mySQL.getVCorePlugin().getPluginName()+"_"+VCorePlugin.getMongoDBIdentifier(dataClass)+":"+suffix;
    }

    private boolean tableExist(Connection connection) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet tables = databaseMetaData.getTables(null,null,getMySQLTableName(),null);
        return tables.next();
    }

    private boolean createTable(Connection connection) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS "+getMySQLTableName()+" (" +
                        "uuid varchar(64) NOT NULL DEFAULT ''," +
                        "PRIMARY KEY (uuid)\n)");
        preparedStatement.executeUpdate();
        return false;
    }

    private void alterTable(Connection connection){

    }

    private <T> String toMySQLType(T object){
        if(object instanceof UUID)
            return "varchar(64)";
        if(object instanceof Integer)
            return null;
        return null;
    }



}
