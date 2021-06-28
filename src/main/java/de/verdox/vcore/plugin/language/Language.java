/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.language;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 27.06.2021 23:59
 */
public enum Language {
    GERMAN("DE"),
    ENGLISH("EN")
    ;
    private String abbreviation;

    Language(String abbreviation){
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static Language findLanguage(String abbreviation){
        for (Language value : values()) {
            if(value.abbreviation.equals(abbreviation))
                return value;
        }
        return ENGLISH;
    }
}
