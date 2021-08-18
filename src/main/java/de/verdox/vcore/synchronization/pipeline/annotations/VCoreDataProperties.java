/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VCoreDataProperties {
    DataContext dataContext();
    PreloadStrategy preloadStrategy();
    boolean cleanOnNoUse() default true;
    long time() default 20L;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
