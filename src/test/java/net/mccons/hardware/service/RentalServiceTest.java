package net.mccons.hardware.service;

import net.mccons.hardware.dto.RentalRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RentalServiceTest {
    @Autowired
    private RentalService rentalService;

    @Test
    void rentEquipment() {
        final var request = RentalRequest.builder()
                .toolCode("CHNS")
                .checkoutDate(LocalDate.of(2024, 7, 5))
                .days(5)
                .discountPercent(0)
                .build();
        assertThat(rentalService.rentEquipment(request)).isNotNull();
    }
}