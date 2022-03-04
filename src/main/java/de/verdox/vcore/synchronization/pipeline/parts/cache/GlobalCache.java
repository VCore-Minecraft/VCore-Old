/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.cache;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.DataProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:25
 */
public interface GlobalCache extends DataProvider {
    Set<String> getKeys(Class<? extends VCoreData> dataClass);

    //TODO: Queries für GlobalStorage nach bestimmten Attributen / sortiert etc. -> Für TOP Listen etc interessant
}
