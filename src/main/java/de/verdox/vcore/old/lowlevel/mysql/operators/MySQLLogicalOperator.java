/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.old.lowlevel.mysql.operators;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.06.2021 13:33
 */
public enum MySQLLogicalOperator {
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    ;
    private final String operator;

    MySQLLogicalOperator(String operator){
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return getOperator();
    }
}
