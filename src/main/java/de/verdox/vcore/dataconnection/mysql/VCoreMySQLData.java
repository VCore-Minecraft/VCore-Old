/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.dataconnection.mysql;

import com.j256.ormlite.field.DatabaseFieldConfig;

import java.util.Set;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 11.06.2021 23:40
 */
public interface VCoreMySQLData {
    Set<DatabaseFieldConfig> getDatabaseFieldConfigs();
}
