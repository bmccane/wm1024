package net.mccons.hardware.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

public interface TypeOfDayService {
    /**
     * Checks if the given date is a weekend (Saturday or Sunday).
     *
     * @param date the date to check
     * @return true if the date is a weekend, false otherwise
     */
    static boolean isWeekend(final LocalDate date) {
        return DayOfWeek.SATURDAY.equals(date.getDayOfWeek()) ||
                DayOfWeek.SUNDAY.equals(date.getDayOfWeek());
    }

    /**
     * Checks if the given date is a weekday (Monday to Friday).
     *
     * @param date the date to check
     * @return true if the date is a weekday, false otherwise
     */
    static boolean isWeekday(final LocalDate date) {
        return !isWeekend(date);
    }

    /**
     * Checks if the given date is a holiday.
     *
     * @param date the date to check
     * @return true if the date is a holiday, false otherwise
     */
    boolean isHoliday(final LocalDate date);
}
