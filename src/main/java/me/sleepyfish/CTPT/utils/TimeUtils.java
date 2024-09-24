package me.sleepyfish.CTPT.utils;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used to get current time or date
 */
public final class TimeUtils {

    public static String getCurrentTime() {
        final LocalTime time = LocalTime.now();
        return time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
    }

    public static String getCurrentDate() {
        final LocalDate date = LocalDate.now();
        return date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear();
    }

}