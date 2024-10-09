package net.mccons.hardware.service;

import jakarta.transaction.Transactional;
import net.mccons.hardware.dto.RentalAgreement;
import net.mccons.hardware.dto.RentalRequest;
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
    public static final BigDecimal CHAINSAW_CHARGE = BigDecimal.valueOf(1.99).setScale(2, RoundingMode.HALF_UP);
    public static final String LADDER = "Ladder";
    public static final BigDecimal LADDER_CHARGE = BigDecimal.valueOf(1.49).setScale(2, RoundingMode.HALF_UP);
    public static final String JACKHAMMER = "Jackhammer";
    public static final BigDecimal JACKHAMMER_CHARGE = BigDecimal.valueOf(2.99).setScale(2, RoundingMode.HALF_UP);

    public static final String DEWALT = "DeWalt";
    public static final String RIDGID = "Ridgid";
    public static final String STIHL = "Stihl";
    public static final String WERNER = "Werner";

    public static final String TOOL_1_NAME = "CHNS";
    public static final String TOOL_2_NAME = "LADW";
    public static final String TOOL_3_NAME = "JAKD";
    public static final String TOOL_4_NAME = "JAKR";

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

        var dewalt = toolBrandRepository.save(ToolBrand.builder().name(DEWALT).build());
        var ridgid = toolBrandRepository.save(ToolBrand.builder().name(RIDGID).build());
        var stihl = toolBrandRepository.save(ToolBrand.builder().name(STIHL).build());
        var werner = toolBrandRepository.save(ToolBrand.builder().name(WERNER).build());

        toolRepository.save(Tool.builder().code(TOOL_1_NAME).type(chainsaw).brand(stihl).build());
        toolRepository.save(Tool.builder().code(TOOL_2_NAME).type(ladder).brand(werner).build());
        toolRepository.save(Tool.builder().code(TOOL_3_NAME).type(jackhammer).brand(dewalt).build());
        toolRepository.save(Tool.builder().code(TOOL_4_NAME).type(jackhammer).brand(ridgid).build());
    }

    @Test
    void rentEquipmentNoToolFound() {
        final var request = RentalRequest.builder()
                .toolCode("ABCD")
                .build();

        var exception = assertThrows(ToolCodeException.class, () -> rentalService.rentEquipment(request));

        assertThat(exception.getMessage()).isEqualTo("No tool found matching: ABCD");
    }

    @Test
    void rentEquipmentTest2() {
        final var toolCode = TOOL_2_NAME;
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
                .preDiscountCharge(BigDecimal.valueOf(2.98).setScale(2, RoundingMode.HALF_UP))
                .discountAmount(BigDecimal.valueOf(0.30).setScale(2, RoundingMode.HALF_UP))
                .finalCharge(BigDecimal.valueOf(2.68).setScale(2, RoundingMode.HALF_UP))
                .build();
        assertThat(rentalService.rentEquipment(request))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
