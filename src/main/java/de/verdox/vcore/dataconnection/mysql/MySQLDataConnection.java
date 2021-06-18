/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.dataconnection.mysql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.table.DatabaseTableConfig;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.dataconnection.DataProvider;
import de.verdox.vcore.plugin.VCorePlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 11.06.2021 23:27
 */
public class MySQLDataConnection extends DataConnection<Dao<VCoreData, UUID>> {
    public MySQLDataConnection(VCorePlugin<?, ?> vCorePlugin, String host, String database, int port, String user, String password) {
        super(vCorePlugin, host, database, port, user, password);
    }

    @Override
    protected void connect() {

    }

    @Override
    protected void onConnect() {

    }

    @Override
    public Dao<VCoreData, UUID> getDataStorage(Class<? extends VCoreData> dataClass, String suffix) {
        //DatabaseTableConfig<? extends VCoreData> dataDatabaseTableConfig = new DatabaseTableConfig<>(dataClass,"tableName", List.of(DatabaseFieldConfig.));
        return null;
    }

    public List<DatabaseFieldConfig> getDatabaseFieldConfigs(Class<? extends VCoreData> dataClass){
        //VCoreData.getPersistentDataFields(dataClass).forEach(field -> {
        //    DatabaseFieldConfig databaseFieldConfig = new DatabaseFieldConfig(field.getName());
        //    databaseFieldConfig.setDataType();
        //    field.getType();
        //});
        return null;
    }
}
