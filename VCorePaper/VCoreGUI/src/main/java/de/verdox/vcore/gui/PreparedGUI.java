/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.gui;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.08.2021 22:58
 */
public interface PreparedGUI<T> extends CustomGUI {
    VCoreGUI.Builder<T> getBuilder();
}
