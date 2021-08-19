/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.old.lowlevel.mysql.commands;

import java.util.Arrays;
import java.util.List;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.06.2021 13:35
 */
public class Select extends MysqlFormat {

    private final String tableName;
    private List<String> columnNames;
    private Where where;
    private boolean selectEverything = false;

    public Select(String tableName, String... columnNames) {
        if (columnNames.length == 0)
            throw new NullPointerException("Please provide at least one columnName");
        if (columnNames[0].equalsIgnoreCase("*"))
            selectEverything = true;
        else
            this.columnNames = Arrays.asList(columnNames);
        this.tableName = tableName;
    }

    public Select setWhere(Where where) {
        this.where = where;
        return this;
    }

    @Override
    public String toMySQLCommand() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        if (selectEverything)
            builder.append(" * ");
        else {
            for (int i = 0; i < columnNames.size(); i++) {
                builder.append(columnNames.get(i));
                if (i < columnNames.size() - 1)
                    builder.append(", ");
            }
        }
        builder.append(" FROM " + tableName);
        if (where != null) {
            builder.append(" ");
            builder.append(where.toMySQLCommand());
        }
        return builder.toString() + ";";
    }
}
