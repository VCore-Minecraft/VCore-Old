/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.session.datahandler;

import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.datatypes.VCoreData;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.06.2021 13:48
 */
public interface LocalDataHandler <S extends VCoreData> {
    boolean dataExistLocally(Class<? extends S> dataClass, UUID uuid);
    void addDataLocally(PlayerData data, Class<? extends PlayerData> dataClass, boolean push);
    void removeDataLocally(Class<? extends PlayerData> dataClass, UUID uuid, boolean push);
    <T extends PlayerData> T getDataLocal(Class<? extends T> type, UUID uuid);

    void localToRedis(S dataObject, Class<? extends S> dataClass, UUID objectUUID);
    void localToDatabase(Class<? extends S> dataClass,UUID objectUUID);
}
