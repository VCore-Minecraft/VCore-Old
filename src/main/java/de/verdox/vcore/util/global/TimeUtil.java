/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.global;

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

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            if (years != 0) {
                if (years == 1)
                    stringBuilder.append(years + " Jahr ");
                else
                    stringBuilder.append(years + " Jahre ");
            }
            if (months != 0) {
                if (months == 1)
                    stringBuilder.append(months + " Monat ");
                else
                    stringBuilder.append(months + " Monate ");
            }
            if (weeks != 0) {
                if (weeks == 1)
                    stringBuilder.append(weeks + " Woche ");
                else
                    stringBuilder.append(weeks + " Wochen ");
            }
            if (days != 0) {
                if (days == 1)
                    stringBuilder.append(days + " Tag ");
                else
                    stringBuilder.append(days + " Tage ");
            }
            if (hours != 0) {
                if (hours == 1)
                    stringBuilder.append(hours + " Stunde ");
                else
                    stringBuilder.append(hours + " Stunden ");
            }
            if (minutes != 0) {
                if (minutes == 1)
                    stringBuilder.append(minutes + " Minute ");
                else
                    stringBuilder.append(minutes + " Minuten ");
            }
            if (seconds != 0) {
                if (seconds == 1)
                    stringBuilder.append(seconds + " Sekunde");
                else
                    stringBuilder.append(seconds + " Sekunden");
            }
            return stringBuilder.toString();
        }
    }
}
