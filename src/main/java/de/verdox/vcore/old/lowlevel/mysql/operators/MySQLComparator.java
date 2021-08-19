/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.old.lowlevel.mysql.operators;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.06.2021 13:27
 */
public enum MySQLComparator {
    EQUALITY("="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_OR_EQUAL("<="),
    GREATER_THAN_OR_EQUAL(">="),
    NOT_EQUAL("<>"),
    BETWEEN("<>"),
    LIKE("LIKE"),
    ;
    private String comparatorChar;

    MySQLComparator(String comparatorChar) {
        this.comparatorChar = comparatorChar;
    }

    public String getComparatorChar() {
        return comparatorChar;
    }

    @Override
    public String toString() {
        return getComparatorChar();
    }
}
