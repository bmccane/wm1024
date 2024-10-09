package net.mccons.hardware.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class RentalAgreementTest {
    @Test
    void print() {
        final var checkOutDate = LocalDate.of(2020, Month.JULY, 2);

        final var expected = RentalAgreement.builder()
                .toolCode("code")
                .toolType("type")
                .toolBrand("brand")
                .rentalDays(3)
                .checkOutDate(checkOutDate)
                .dueDate(checkOutDate.plusDays(3))
                .dailyRentalCharge(BigDecimal.valueOf(1.49))
                .discountPercent(10)
                .chargeDays(2)
                .preDiscountCharge(BigDecimal.valueOf(2.98).setScale(2, RoundingMode.HALF_UP))
                .discountAmount(BigDecimal.valueOf(0.30).setScale(2, RoundingMode.HALF_UP))
                .finalCharge(BigDecimal.valueOf(2.68).setScale(2, RoundingMode.HALF_UP))
                .build();
        assertThat(expected.formatted()).isEqualTo(
                """
                        Tool code: code
                        Tool type: type
                        Tool brand: brand
                        Rental days: 3
                        Check out date: 07/02/20
                        Due date: 07/05/20
                        Daily rental charge: $1.49
                        Charge days: 2
                        Pre-discount charge: $2.98
                        Discount percent: 10%
                        Discount amount: $0.30
                        Final charge: $2.68
                        """
        );
    }
}
