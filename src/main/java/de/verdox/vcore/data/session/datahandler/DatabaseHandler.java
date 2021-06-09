/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.session.datahandler;

import de.verdox.vcore.data.datatypes.VCoreData;

import java.util.Map;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.06.2021 13:50
 */
public interface DatabaseHandler <S extends VCoreData> {
    Map<String, Object> loadDataFromDatabase(Class<? extends S> dataClass, UUID objectUUID);
    boolean dataExistInDatabase(Class<? extends S> dataClass, UUID objectUUID);
    void saveToDatabase(Class<? extends S> dataClass, UUID objectUUID, Map<String, Object> dataToSave);
    void dataBaseToRedis(Class<? extends S> dataClass, UUID objectUUID);
    void databaseToLocal(Class<? extends S> dataClass, UUID objectUUID);
}
