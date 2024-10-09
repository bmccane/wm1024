package net.mccons.hardware.service;

import net.mccons.hardware.dto.RentalAgreement;
import net.mccons.hardware.dto.RentalRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RentalServiceTest {

    @Autowired
    private RentalService rentalService;

    @Test
    void rentEquipmentTest2() {
        final var toolCode = "LADW";
        final var checkoutDate = LocalDate.of(2024, Month.JULY, 20);
        final var rentalDays = 3;
        final var discountPercent = 10;

        final var request = RentalRequest.builder()
                .toolCode(toolCode)
                .checkoutDate(checkoutDate)
                .days(rentalDays)
                .discountPercent(discountPercent)
                .build();
        final var expected = RentalAgreement.builder()
                .toolCode(toolCode)
                .checkoutDate(checkoutDate)
                .rentalDays(rentalDays)
                .discountPercent(discountPercent)
                .build();
        assertThat(rentalService.rentEquipment(request))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}