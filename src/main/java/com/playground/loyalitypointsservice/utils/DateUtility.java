package com.playground.loyalitypointsservice.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public class DateUtility {
    private static LocalDate fixedDate = null;

    public static void fixDate(LocalDate localDate) {
        fixedDate = localDate;
    }

    public static void resetToDefaultDate() {
        fixedDate = null;
    }

    public static LocalDate getLastSundayDate() {
        if (fixedDate != null) {
            return fixedDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        }
        return LocalDate.now(ZoneId.systemDefault()).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
    }

    public static LocalDate now() {
        if (fixedDate != null) {
            return fixedDate;
        }
        return LocalDate.now();
    }
}
