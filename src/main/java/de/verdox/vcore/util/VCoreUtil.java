/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util;

import de.verdox.vcore.util.global.MathUtil;
import de.verdox.vcore.util.global.RandomUtil;
import de.verdox.vcore.util.global.TimeUtil;
import de.verdox.vcore.util.global.TypeUtil;

public class VCoreUtil {
    public static TypeUtil getTypeUtil() {
        return new TypeUtil();
    }

    public static RandomUtil getRandomUtil() {
        return new RandomUtil();
    }

    public static MathUtil getMathUtil() {
        return new MathUtil();
    }

    public static TimeUtil getTimeUtil() {
        return new TimeUtil();
    }
}