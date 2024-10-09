package net.mccons.hardware.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

public class HolidayService {
    public static boolean isHoliday(final LocalDate date) {
        return isIndependenceDay(date) || isLaborDay(date);
    }

    private static boolean isIndependenceDay(final LocalDate date) {
        LocalDate independenceDay = LocalDate.of(date.getYear(), Month.JULY, 4);
        if (DayOfWeek.SATURDAY.equals(independenceDay.getDayOfWeek()))
            independenceDay = independenceDay.minusDays(1);
        else if (DayOfWeek.SUNDAY.equals(independenceDay.getDayOfWeek()))
            independenceDay = independenceDay.plusDays(1);

        return date.equals(independenceDay);
    }

    private static boolean isLaborDay(final LocalDate date) {
        final LocalDate laborDay = LocalDate.of(date.getYear(), Month.SEPTEMBER, 1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        return date.equals(laborDay);
    }
}
