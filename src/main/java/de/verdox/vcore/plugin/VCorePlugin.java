/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin;

import de.verdox.vcore.performance.concurrent.TaskBatch;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.plugin.wrapper.PlatformWrapper;
import de.verdox.vcore.synchronization.pipeline.annotations.RequiredSubsystemInfo;

import java.io.File;
import java.util.List;

public interface VCorePlugin<T, R extends VCoreSubsystem<?>> extends SystemLoadable {

    //TODO: Wenn nicht Bungee Modus muss der Spigot Server selbst die VCorePlayer Data rausnehmen

    String vCorePaperName = "VCorePaper";
    String vCoreWaterfallName = "VCoreWaterfall";

    void onPluginEnable();

    void onPluginDisable();

    List<R> provideSubsystems();

    T getPlugin();

    File getPluginDataFolder();

    String getPluginName();

    void consoleMessage(String message, boolean debug);

    void consoleMessage(String message, int tabSize, boolean debug);

    PlatformWrapper getPlatformWrapper();

    boolean debug();

    void setDebugMode(boolean value);

    TaskBatch<VCorePlugin<T, R>> createTaskBatch();

    void async(Runnable runnable);

    void sync(Runnable runnable);

    <V extends VCoreCoreInstance<T, R>> V getCoreInstance();

    PluginServiceParts<?, R> getServices();

    default VCoreSubsystem<?> findDependSubsystem(Class<?> classType) {
        RequiredSubsystemInfo requiredSubsystemInfo = classType.getAnnotation(RequiredSubsystemInfo.class);
        if (requiredSubsystemInfo == null)
            throw new RuntimeException(classType.getName() + " does not have RequiredSubsystemInfo Annotation set");
        return getServices().getSubsystemManager().findSubsystemByClass(requiredSubsystemInfo.parentSubSystem());
    }
}
