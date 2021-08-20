/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.custompipelinedata.inventories;

import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 19.08.2021 23:15
 */
public interface CustomPipelineData {
    Map<String, Object> getUnderlyingMap();
}
