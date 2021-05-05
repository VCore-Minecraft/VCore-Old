package de.verdox.vcore.dataconnection.mysql;

import com.zaxxer.hikari.HikariDataSource;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.dataconnection.DataProvider;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public abstract class MySQLDataConnection extends DataConnection<DataProvider.MySQLTable> {

    private final String host;
    private String databaseName;
    private final String userName;
    private final String password;
    private final int port;
    private final int maxPoolSize;
    private final HikariDataSource hikari;

    public MySQLDataConnection(VCoreSubsystem<?> subSystem, String host, String databaseName, String userName, String password, int port, int maxPoolSize){
        super(subSystem);
        this.host = host;
        this.databaseName = databaseName;
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.maxPoolSize = maxPoolSize;
        this.hikari = new HikariDataSource();
    }

    public MySQLDataConnection(VCorePlugin<?,?> vCorePlugin, String host, String databaseName, String userName, String password, int port, int maxPoolSize){
        super(vCorePlugin);
        this.host = host;
        this.databaseName = databaseName;
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.maxPoolSize = maxPoolSize;
        this.hikari = new HikariDataSource();
    }

    @Override
    public void connect() throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubsystem());

        try{
            hikari.setMaximumPoolSize(maxPoolSize);
            hikari.setDataSourceClassName("");
            hikari.setJdbcUrl("jdbc:mysql://"+host+":"+port+"/"+databaseName);
            hikari.setUsername(userName);
            hikari.setPassword(password);
            hikari.setIdleTimeout(600000);
            hikari.setMaxLifetime(1800000);
            hikari.setLeakDetectionThreshold(60 * 1000);
            Connection connection = getConnection();
            if(connection != null){
                connection.close();
                getVCorePlugin().consoleMessage("&eConnected to database&7!",true);
            }
            else {
                getVCorePlugin().consoleMessage("&4Could not connect to database&7!",true);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        onConnect();
    }

    public Connection getConnection() throws SQLException {
        return hikari.getConnection();
    }

    @Override
    public void disconnect() throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubsystem());

        onDisconnect();
        getSubsystem().getVCorePlugin().consoleMessage("&eClosing Database Connection",true);
        saveAllPlayers();
    }

    @Override
    public DataProvider.MySQLTable getDataProvider(Class<? extends VCoreData> dataClass, String suffix) {
        return null;
    }

    @Override
    public void saveAllPlayers() {

    }
}