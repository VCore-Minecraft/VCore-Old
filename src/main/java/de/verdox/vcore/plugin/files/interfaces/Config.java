/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.files.interfaces;

import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:07
 */
public interface Config {

    void writeString(String path, String value);
    void writeInt(String path, int value);
    void writeFloat(String path, float value);
    void writeUUID(String path, UUID value);
    void writeStringList(String path, List<String> value);

}
