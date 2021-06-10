/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.old.lowlevel.mysql.commands;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.06.2021 14:01
 */
public class Delete extends MysqlFormat {
    private final String tableName;
    private Where where;

    public Delete(String tableName){
        this.tableName = tableName;
        stringBuilder.append("DELETE FROM "+tableName);
    }

    public Delete setWhere(Where where){
        this.where = where;
        return this;
    }
    
    @Override
    public String toMySQLCommand() {
        if(where != null)
            stringBuilder.append(" "+where.toMySQLCommand());
        stringBuilder.append(";");
        return stringBuilder.toString();
    }
}
