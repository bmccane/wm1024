package net.mccons.hardware.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TypeOfDayServiceTest {
    @ParameterizedTest
    @CsvSource({
            "2024, 7, 3, false",
            "2024, 7, 4, true",
            "2024, 7, 5, false",
            "2021, 7, 3, false",
            "2021, 7, 4, false",
            "2021, 7, 5, true",
            "2020, 7, 3, true",
            "2020, 7, 4, false",
            "2020, 7, 5, false",
            "2023, 9, 2, false",
            "2024, 9, 2, true",
            "2025, 9, 2, false"
    })
    void isHolidayTest(final int year, final int month, final int day, final boolean expected) {
        assertThat(TypeOfDayService.isHoliday(LocalDate.of(year, month, day))).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "2020, 7, 3, false",
            "2020, 7, 4, true",
            "2020, 7, 5, true"
    })
    void isWeekend(final int year, final int month, final int day, final boolean expected) {
        assertThat(TypeOfDayService.isWeekend(LocalDate.of(year, month, day))).isEqualTo(expected);
    }
}
