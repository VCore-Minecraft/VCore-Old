package de.verdox.vcore.data.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VCoreDataContext {
    DataContext dataContext();
    PreloadStrategy preloadStrategy();
}
