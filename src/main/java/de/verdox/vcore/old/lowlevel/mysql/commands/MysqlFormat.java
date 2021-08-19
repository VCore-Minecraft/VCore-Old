/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.old.lowlevel.mysql.commands;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.06.2021 13:35
 */
public abstract class MysqlFormat {
    protected final StringBuilder stringBuilder = new StringBuilder();

    public abstract String toMySQLCommand();

    @Override
    public String toString() {
        return toMySQLCommand();
    }
}
