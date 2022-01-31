/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.block;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.nbt.CustomDataManager;
import de.verdox.vcorepaper.custom.nbt.block.data.VBlockCustomData;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 22:12
 */
public class CustomLocationDataManager extends CustomDataManager<Location, VBlockCustomData<?>, VBlock.LocationBased> implements SystemLoadable {
    //private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("VBlock - Ticking Thread"));

    public CustomLocationDataManager(VCorePaper vCorePaper) {
        super(vCorePaper);
    }

    public VBlock.LocationBased getVBlock(Location location) {
        return wrap(VBlock.LocationBased.class, location);
    }

    @Override
    public <U extends VBlock.LocationBased> U wrap(Class<? extends U> type, Location inputObject) {
        try {
            return type.getDeclaredConstructor(Location.class, CustomLocationDataManager.class).newInstance(inputObject, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <U extends VBlock.LocationBased> U convertTo(Class<? extends U> type, VBlock.LocationBased customData) {
        try {
            return type.getDeclaredConstructor(Location.class, CustomLocationDataManager.class).newInstance(customData.getDataHolder(), this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected VBlockCustomData<?> instantiateCustomData(Class<? extends VBlockCustomData<?>> dataClass) {
        try {
            return dataClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        //executor.shutdown();
    }
}
