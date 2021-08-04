/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.global;

import java.text.NumberFormat;

public class MathUtil {

    public double roundToTwoDigits(double number){
        return Math.round(number * 100) / 100d;
    }
}
