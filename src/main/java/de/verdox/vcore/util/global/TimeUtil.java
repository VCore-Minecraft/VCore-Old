/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.global;

import org.checkerframework.checker.index.qual.NonNegative;

import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 06.07.2021 18:34
 */
public class TimeUtil {
    public String convertSecondsHMS(long seconds) {
        if (seconds >= 3600)
            return String.format("%02dh %02dmin %02ds", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
        else if (seconds >= 60)
            return String.format("%02dmin %02ds", (seconds % 3600) / 60, seconds % 60);
        else
            return String.format("%2ds", seconds % 60);
    }

    public String convertSecondsHM(long seconds) {
        if (seconds >= 3600)
            return String.format("%2dh %2dmin", seconds / 3600, (seconds % 3600) / 60);
        else
            return String.format("%2dmin", (seconds % 3600) / 60);
    }

    public static class TimeConverter {

        private final long years;
        private final long months;
        private final long weeks;
        private final long hours;
        private final long minutes;
        private final long seconds;
        private long days;

        public TimeConverter(int inputSeconds) {
            this.days = TimeUnit.SECONDS.toDays(inputSeconds);
            this.years = days / 365;
            this.days %= 365;
            this.months = days / 30;
            this.days %= 30;
            this.weeks = days / 7;
            this.days %= 7;
            this.hours = TimeUnit.SECONDS.toHours(inputSeconds) - TimeUnit.DAYS.toHours(TimeUnit.SECONDS.toDays(inputSeconds));
            this.minutes = TimeUnit.SECONDS.toMinutes(inputSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(inputSeconds));
            this.seconds = TimeUnit.SECONDS.toSeconds(inputSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(inputSeconds));
        }

        public TimeConverter(long inputMillis) {
            this((int) TimeUnit.MILLISECONDS.toSeconds(inputMillis));
        }

        public String toString(@NonNegative int visibleFormats) {
            StringBuilder stringBuilder = new StringBuilder();

            long[] valueArray = new long[]{years, months, weeks, days, hours, minutes, seconds};
            String[] translationArray = new String[]{
                    "Jahr", "Jahre",
                    "Monat", "Monate",
                    "Woche", "Wochen",
                    "Tag", "Tage",
                    "Stunde", "Stunden",
                    "Minute", "Minuten",
                    "Sekunde", "Sekunden",
            };

            int counter = 0;
            for (int i = 0; i < valueArray.length && counter < visibleFormats; i++) {
                long value = valueArray[i];
                String singular = translationArray[i];
                String plural = translationArray[i + 1];

                if (value != 0) {
                    if (value == 1)
                        stringBuilder.append(value + " " + singular);
                    else
                        stringBuilder.append(value + " " + plural);
                    counter++;
                }
            }
            return stringBuilder.toString();
        }

        @Override
        public String toString() {
            return toString(7);
        }
    }
}
