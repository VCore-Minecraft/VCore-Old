package de.verdox.vcore.dataconnection.mysql;

import com.zaxxer.hikari.HikariDataSource;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.dataconnection.DataProvider;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class MySQLDataConnection extends DataConnection<Connection,DataProvider.MySQLTable> {

    private final String host;
    private String databaseName;
    private final String userName;
    private final String password;
    private final int port;
    private final int maxPoolSize;
    private final HikariDataSource hikari;

    private final Map<Class<?>,DataProvider.MySQLTable> providers;

    public MySQLDataConnection(VCoreSubsystem<?> subSystem, String host, String databaseName, String userName, String password, int port, int maxPoolSize){
        super(subSystem);
        this.host = host;
        this.databaseName = databaseName;
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.maxPoolSize = maxPoolSize;
        this.hikari = new HikariDataSource();
        this.providers = new HashMap<>();
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
        this.providers = new HashMap<>();
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
                getVCorePlugin().consoleMessage("&eConnected to database&7!");
            }
            else {
                getVCorePlugin().consoleMessage("&4Could not connect to database&7!");
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
        getSubsystem().getVCorePlugin().consoleMessage("&eClosing Database Connection");
        saveAllPlayers();
    }

    @Override
    public DataProvider.MySQLTable getDataProvider(Class<? extends DataProvider.MySQLTable> providerType) {
        if(!this.providers.containsKey(providerType))
            return null;
        return this.providers.get(providerType);
    }

    @Override
    public void saveAllPlayers() {

    }
}