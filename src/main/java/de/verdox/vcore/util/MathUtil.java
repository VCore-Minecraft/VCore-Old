package de.verdox.vcore.util;

import java.text.NumberFormat;

public class MathUtil {

    public double roundToTwoDigits(double number){
        return Math.round(number * 100) / 100d;
    }
}
