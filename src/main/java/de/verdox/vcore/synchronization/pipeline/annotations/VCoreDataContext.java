/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
public @interface VCoreDataContext {
    DataContext dataContext();
    PreloadStrategy preloadStrategy();
    boolean cleanOnNoUse() default true;
    long time() default 20L;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
