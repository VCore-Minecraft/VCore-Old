package de.verdox.vcore.data.annotations;

import de.verdox.vcore.subsystem.VCoreSubsystem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredSubsystemInfo {
    Class<? extends VCoreSubsystem<?>> parentSubSystem();
}
