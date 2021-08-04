/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.query;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 17:40
 */
public interface QueryHandler {
    void onQuerySend(UUID queryUUID, String[] parameters, Object[] queryData);
    Object[] respondToQuery(UUID queryUUID, String[] parameters, Object[] queryData);
    void onResponse(UUID queryUUID, String[] parameters, Object[] queryData, Object[] responseData);
}
