/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface VCorePersistentData {
}
