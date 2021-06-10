/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.old.lowlevel.mysql.operators;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.06.2021 13:02
 */
public enum MySQLTypes {
    BIT(Boolean.class,"BIT"),
    SMALLINT(Integer.class,"SMALLINT"),
    MEDIUMINT(Integer.class,"MEDIUMINT"),
    INTEGER(Integer.class,"INTEGER"),
    BIGINT(Integer.class,"BIGINT"),
    FLOAT(Float.class,"FLOAT"),
    DOUBLE(Double.class,"DOUBLE"),
    DECIMAL(BigDecimal.class,"DECIMAL"),
    DATE(Date.class,"DATE"),
    DATETIME(Timestamp.class,"DATETIME"),
    TIMESTAMP(Timestamp.class,"TIMESTAMP"),
    TIME(Time.class,"TIME"),
    CHAR(String.class,"CHAR"),
    VARCHAR(String.class,"VARCHAR"),
    BINARY(byte[].class,"BINARY"),
    VARBINARY(byte[].class,"VARBINARY"),
    TINYBLOB(byte[].class,"TINYBLOB"),
    TINYTEXT(String.class,"VARCHAR"),
    TEXT(String.class,"TEXT"),
    MEDIUMTEXT(String.class,"TEXT"),
    LONGTEXT(String.class,"TEXT"),
    ;
    private Class<?> classType;
    private String queryFormat;

    MySQLTypes(Class<?> classType, String queryFormat){
        this.classType = classType;
        this.queryFormat = queryFormat;
    }

    @Override
    public String toString() {
        return queryFormat;
    }

    public Class<?> getClassType() {
        return classType;
    }
}
