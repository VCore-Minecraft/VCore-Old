/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.annotations;

/**
 * Specifies if a data object will be held in global cache or only local cache
 */
public enum DataContext {
    GLOBAL,
    LOCAL
}
