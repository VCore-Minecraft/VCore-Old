package de.verdox.vcore.synchronization.pipeline.parts.manipulator;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 07.02.2022 23:45
 */
public interface SynchronizingService {

    //TODO: DataManipulator unabhängig von GlobalCache machen. -> Unabhängige Implementierung möglich machen
    DataSynchronizer getDataSynchronizer(VCoreData vCoreData);

}
