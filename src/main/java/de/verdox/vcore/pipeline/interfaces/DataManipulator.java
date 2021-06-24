/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.interfaces;

import de.verdox.vcore.pipeline.datatypes.VCoreData;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 01:22
 */
public interface DataManipulator {

    void cleanUp();
    void pushUpdate(VCoreData vCoreData, boolean async);

}
