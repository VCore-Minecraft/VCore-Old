/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.language;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 28.06.2021 00:46
 */
public interface InGameMessage {
    String getTranslatedMessage(Language language);
}
