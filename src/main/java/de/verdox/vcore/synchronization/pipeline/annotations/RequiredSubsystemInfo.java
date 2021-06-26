/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.annotations;

import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredSubsystemInfo {
    Class<? extends VCoreSubsystem<?>> parentSubSystem();
}
