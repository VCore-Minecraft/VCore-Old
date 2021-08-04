/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.query.registry;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 04.08.2021 21:23
 */
public interface QueryData {
    Object[] write();
    void read(Object[] data);
}
