/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.old.lowlevel.mysql.commands;

import de.verdox.vcore.old.lowlevel.mysql.operators.MySQLComparator;
import de.verdox.vcore.old.lowlevel.mysql.operators.MySQLLogicalOperator;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.06.2021 13:36
 */
public class Where extends MysqlFormat {
    public Where(String columnName, MySQLComparator mysqlComparator, Object value) {
        stringBuilder.append("WHERE ");
        stringBuilder.append(new WherePart(columnName, mysqlComparator, value).toMySQLCommand());
    }

    public Where addWhereStatement(MySQLLogicalOperator mySQLLogicalOperator, String columnName, MySQLComparator mysqlComparator, Object value) {
        stringBuilder.append(" " + mySQLLogicalOperator.getOperator() + " " + new WherePart(columnName, mysqlComparator, value).toMySQLCommand());
        return this;
    }

    @Override
    public String toMySQLCommand() {
        return stringBuilder.toString();
    }

    public static class WherePart extends MysqlFormat {

        private final String columnName;
        private final MySQLComparator mysqlComparator;
        private final Object value;

        public WherePart(String columnName, MySQLComparator mysqlComparator, Object value) {
            this.columnName = columnName;
            this.mysqlComparator = mysqlComparator;
            this.value = value;
        }

        @Override
        public String toMySQLCommand() {
            return columnName + " " + mysqlComparator.getComparatorChar() + " " + value;
        }
    }
}
