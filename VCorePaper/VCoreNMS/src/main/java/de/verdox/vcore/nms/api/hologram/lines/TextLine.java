/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.hologram.lines;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.06.2021 02:41
 */
public interface TextLine extends HologramLine {
    String getText();

    void setText(String text);
}
