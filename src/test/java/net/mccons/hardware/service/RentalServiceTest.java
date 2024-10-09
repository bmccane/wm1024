package net.mccons.hardware.service;

import jakarta.transaction.Transactional;
import net.mccons.hardware.dto.RentalAgreement;
import net.mccons.hardware.dto.RentalRequest;
import net.mccons.hardware.exceptions.DiscountPercentException;
import net.mccons.hardware.exceptions.RentalDayCountException;
import net.mccons.hardware.exceptions.ToolCodeException;
import net.mccons.hardware.model.Tool;
import net.mccons.hardware.model.ToolBrand;
import net.mccons.hardware.model.ToolType;
import net.mccons.hardware.repository.ToolBrandRepository;
import net.mccons.hardware.repository.ToolRepository;
import net.mccons.hardware.repository.ToolTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RentalServiceTest {

    public static final String CHAINSAW = "Chainsaw";
    public static final BigDecimal CHAINSAW_CHARGE = BigDecimal.valueOf(1.49).setScale(2, RoundingMode.HALF_UP);
    public static final String LADDER = "Ladder";
    public static final BigDecimal LADDER_CHARGE = BigDecimal.valueOf(1.99).setScale(2, RoundingMode.HALF_UP);
    public static final String JACKHAMMER = "Jackhammer";
    public static final BigDecimal JACKHAMMER_CHARGE = BigDecimal.valueOf(2.99).setScale(2, RoundingMode.HALF_UP);

    public static final String DEWALT = "DeWalt";
    public static final String RIDGID = "Ridgid";
    public static final String STIHL = "Stihl";
    public static final String WERNER = "Werner";

    public static final String CHNS = "CHNS";
    public static final String LADW = "LADW";
    public static final String JAKD = "JAKD";
    public static final String JAKR = "JAKR";

    @Autowired
    private RentalService rentalService;
    @Autowired
    private ToolTypeRepository toolTypeRepository;
    @Autowired
    private ToolBrandRepository toolBrandRepository;
    @Autowired
    private ToolRepository toolRepository;

    @BeforeEach
    public void setup() {
        // populate database for test
        var ladder = toolTypeRepository.save(ToolType.builder()
                .name(LADDER)
                .charge(LADDER_CHARGE)
                .weekday(true)
                .weekend(true)
                .build());
        var chainsaw = toolTypeRepository.save(ToolType.builder()
                .name(CHAINSAW)
                .charge(CHAINSAW_CHARGE)
                .weekday(true)
                .holiday(true)
                .build());
        var jackhammer = toolTypeRepository.save(ToolType.builder()
                .name(JACKHAMMER)
                .charge(JACKHAMMER_CHARGE)
                .weekday(true)
                .build());

        var deWalt = toolBrandRepository.save(ToolBrand.builder().name(DEWALT).build());
        var ridgid = toolBrandRepository.save(ToolBrand.builder().name(RIDGID).build());
        var stihl = toolBrandRepository.save(ToolBrand.builder().name(STIHL).build());
        var werner = toolBrandRepository.save(ToolBrand.builder().name(WERNER).build());

        toolRepository.save(Tool.builder().code(CHNS).type(chainsaw).brand(stihl).build());
        toolRepository.save(Tool.builder().code(LADW).type(ladder).brand(werner).build());
        toolRepository.save(Tool.builder().code(JAKD).type(jackhammer).brand(deWalt).build());
        toolRepository.save(Tool.builder().code(JAKR).type(jackhammer).brand(ridgid).build());
    }

    @Test
    void checkoutNoToolFound() {
        final var request = RentalRequest.builder()
                .toolCode("ABCD")
                .build();

        var exception = assertThrows(ToolCodeException.class, () -> rentalService.checkout(request));

        assertThat(exception.getMessage()).isEqualTo("No tool found matching: ABCD");
    }

    @Test
    void checkoutInvalidRentalDayCount() {
        final var checkOutDate = LocalDate.of(2015, Month.SEPTEMBER, 3);
        final var rentalDays = 0;
        final var discountPercent = 10;

        final var request = RentalRequest.builder()
                .toolCode(JAKR)
                .checkOutDate(checkOutDate)
                .rentalDayCount(rentalDays)
                .discountPercent(discountPercent)
                .build();
        var exception = assertThrows(RentalDayCountException.class, () -> rentalService.checkout(request));

        assertThat(exception.getMessage())
                .isEqualTo("Rental day count must be 1 or greater: " + request.getRentalDayCount());
    }

    @Test
    void checkoutTest1() {
        final var checkOutDate = LocalDate.of(2015, Month.SEPTEMBER, 3);
        final var rentalDays = 5;
        final var discountPercent = 101;

        final var request = RentalRequest.builder()
                .toolCode(JAKR)
                .checkOutDate(checkOutDate)
                .rentalDayCount(rentalDays)
                .discountPercent(discountPercent)
                .build();
        var exception = assertThrows(DiscountPercentException.class, () -> rentalService.checkout(request));

        assertThat(exception.getMessage())
                .isEqualTo("Discount percent must be between 0 and 100: " + request.getDiscountPercent());
    }

    @Test
    void checkoutTest2() {
        final var toolCode = LADW;
        final var checkOutDate = LocalDate.of(2020, Month.JULY, 2);
        final var rentalDays = 3;
        final var discountPercent = 10;

        final var request = RentalRequest.builder()
                .toolCode(toolCode)
                .checkOutDate(checkOutDate)
                .rentalDayCount(rentalDays)
                .discountPercent(discountPercent)
                .build();
        final var expected = RentalAgreement.builder()
                .toolCode(toolCode)
                .toolType(LADDER)
                .toolBrand(WERNER)
                .rentalDays(rentalDays)
                .checkOutDate(checkOutDate)
                .dueDate(checkOutDate.plusDays(rentalDays))
                .dailyRentalCharge(LADDER_CHARGE)
                .discountPercent(discountPercent)
                // all remaining values I calculated manually from spec
                .chargeDays(2)
                .preDiscountCharge(BigDecimal.valueOf(3.98).setScale(2, RoundingMode.HALF_UP))
                .discountAmount(BigDecimal.valueOf(0.40).setScale(2, RoundingMode.HALF_UP))
                .finalCharge(BigDecimal.valueOf(3.58).setScale(2, RoundingMode.HALF_UP))
                .build();
        assertThat(rentalService.checkout(request))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void checkoutTest3() {
        final var toolCode = CHNS;
        final var checkOutDate = LocalDate.of(2015, Month.JULY, 2);
        final var rentalDays = 5;
        final var discountPercent = 25;

        final var request = RentalRequest.builder()
                .toolCode(toolCode)
                .checkOutDate(checkOutDate)
                .rentalDayCount(rentalDays)
                .discountPercent(discountPercent)
                .build();
        final var expected = RentalAgreement.builder()
                .toolCode(toolCode)
                .toolType(CHAINSAW)
                .toolBrand(STIHL)
                .rentalDays(rentalDays)
                .checkOutDate(checkOutDate)
                .dueDate(checkOutDate.plusDays(rentalDays))
                .dailyRentalCharge(CHAINSAW_CHARGE)
                .discountPercent(discountPercent)
                // all remaining values I calculated manually from spec
                .chargeDays(3)
                .preDiscountCharge(BigDecimal.valueOf(4.47).setScale(2, RoundingMode.HALF_UP))
                .discountAmount(BigDecimal.valueOf(1.12).setScale(2, RoundingMode.HALF_UP))
                .finalCharge(BigDecimal.valueOf(3.35).setScale(2, RoundingMode.HALF_UP))
                .build();
        assertThat(rentalService.checkout(request))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void checkoutTest4() {
        final var toolCode = JAKD;
        final var checkOutDate = LocalDate.of(2015, Month.SEPTEMBER, 3);
        final var rentalDays = 6;
        final var discountPercent = 0;

        final var request = RentalRequest.builder()
                .toolCode(toolCode)
                .checkOutDate(checkOutDate)
                .rentalDayCount(rentalDays)
                .discountPercent(discountPercent)
                .build();
        final var expected = RentalAgreement.builder()
                .toolCode(toolCode)
                .toolType(JACKHAMMER)
                .toolBrand(DEWALT)
                .rentalDays(rentalDays)
                .checkOutDate(checkOutDate)
                .dueDate(checkOutDate.plusDays(rentalDays))
                .dailyRentalCharge(JACKHAMMER_CHARGE)
                .discountPercent(discountPercent)
                // all remaining values I calculated manually from spec
                .chargeDays(3)
                .preDiscountCharge(BigDecimal.valueOf(8.97).setScale(2, RoundingMode.HALF_UP))
                .discountAmount(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP))
                .finalCharge(BigDecimal.valueOf(8.97).setScale(2, RoundingMode.HALF_UP))
                .build();
        assertThat(rentalService.checkout(request))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void checkoutTest5() {
        final var toolCode = JAKR;
        final var checkOutDate = LocalDate.of(2015, Month.JULY, 2);
        final var rentalDays = 9;
        final var discountPercent = 0;

        final var request = RentalRequest.builder()
                .toolCode(toolCode)
                .checkOutDate(checkOutDate)
                .rentalDayCount(rentalDays)
                .discountPercent(discountPercent)
                .build();
        final var expected = RentalAgreement.builder()
                .toolCode(toolCode)
                .toolType(JACKHAMMER)
                .toolBrand(RIDGID)
                .rentalDays(rentalDays)
                .checkOutDate(checkOutDate)
                .dueDate(checkOutDate.plusDays(rentalDays))
                .dailyRentalCharge(JACKHAMMER_CHARGE)
                .discountPercent(discountPercent)
                // all remaining values I calculated manually from spec
                .chargeDays(5)
                .preDiscountCharge(BigDecimal.valueOf(14.95).setScale(2, RoundingMode.HALF_UP))
                .discountAmount(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP))
                .finalCharge(BigDecimal.valueOf(14.95).setScale(2, RoundingMode.HALF_UP))
                .build();
        assertThat(rentalService.checkout(request))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void checkoutTest6() {
        final var toolCode = JAKR;
        final var checkOutDate = LocalDate.of(2020, Month.JULY, 2);
        final var rentalDays = 4;
        final var discountPercent = 50;

        final var request = RentalRequest.builder()
                .toolCode(toolCode)
                .checkOutDate(checkOutDate)
                .rentalDayCount(rentalDays)
                .discountPercent(discountPercent)
                .build();
        final var expected = RentalAgreement.builder()
                .toolCode(toolCode)
                .toolType(JACKHAMMER)
                .toolBrand(RIDGID)
                .rentalDays(rentalDays)
                .checkOutDate(checkOutDate)
                .dueDate(checkOutDate.plusDays(rentalDays))
                .dailyRentalCharge(JACKHAMMER_CHARGE)
                .discountPercent(discountPercent)
                // all remaining values I calculated manually from spec
                .chargeDays(1)
                .preDiscountCharge(BigDecimal.valueOf(2.99).setScale(2, RoundingMode.HALF_UP))
                .discountAmount(BigDecimal.valueOf(1.50).setScale(2, RoundingMode.HALF_UP))
                .finalCharge(BigDecimal.valueOf(1.49).setScale(2, RoundingMode.HALF_UP))
                .build();
        assertThat(rentalService.checkout(request))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
