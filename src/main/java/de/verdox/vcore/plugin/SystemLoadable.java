/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 14:16
 */
public interface SystemLoadable {

    boolean isLoaded();

    void shutdown();

}
