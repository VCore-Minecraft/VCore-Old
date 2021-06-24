/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.dataconnection;

import de.verdox.vcore.data.datatypes.VCoreData;

import java.util.Map;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.06.2021 15:41
 */
public interface DataProvider {
    void saveToDatabase(String suffix, Class<? extends VCoreData> dataClass, UUID objectUUID, Map<String, Object> dataToSave);
    Map<String, Object> restoreFromDatabase(String suffix, Class<? extends VCoreData> dataClass, UUID objectUUID);
    boolean dataExistInDatabase(String suffix, Class<? extends VCoreData> dataClass, UUID objectUUID);
}
