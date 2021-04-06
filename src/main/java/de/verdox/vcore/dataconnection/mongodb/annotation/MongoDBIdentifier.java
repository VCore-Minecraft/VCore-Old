package de.verdox.vcore.dataconnection.mongodb.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MongoDBIdentifier {
    String identifier();
}
