package de.verdox.vcorepaper.custom.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NBTIdentifier {
    String nbtKey();
}
