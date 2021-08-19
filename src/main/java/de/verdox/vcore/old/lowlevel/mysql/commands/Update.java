/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.old.lowlevel.mysql.commands;

import de.verdox.vcore.old.lowlevel.mysql.operators.MySQLComparator;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.06.2021 14:08
 */
public class Update extends MysqlFormat {

    private String tableName;

    public Update(String tableName, Set set) {
        this.tableName = tableName;
        stringBuilder.append("UPDATE " + tableName);
        stringBuilder.append(" " + set.toString());
    }

    @Override
    public String toMySQLCommand() {
        return stringBuilder + ";";
    }

    public static class Set extends MysqlFormat {

        private String columnName;
        private Object value;

        public Set(String columnName, Object value) {
            this.columnName = columnName;
            this.value = value;
            stringBuilder.append("SET " + columnName + " " + MySQLComparator.EQUALITY + " " + value);
        }

        public Set addSet(String columnName, Object value) {
            stringBuilder.append(", " + columnName + " " + MySQLComparator.EQUALITY + " " + value);
            return this;
        }

        public Set setWhere(Where where) {
            stringBuilder.append(" " + where.toMySQLCommand());
            return this;
        }

        @Override
        public String toMySQLCommand() {
            return stringBuilder.toString();
        }
    }
}
