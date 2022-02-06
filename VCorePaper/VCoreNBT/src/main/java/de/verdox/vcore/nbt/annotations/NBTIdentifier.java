package de.verdox.vcore.nbt.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NBTIdentifier {
    String nbtKey();
}
