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

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RentalServiceTest {

    public static final String CHAINSAW = "Chainsaw";
    public static final double CHAINSAW_CHARGE = 1.99;
    public static final String LADDER = "Ladder";
    public static final double LADDER_CHARGE = 1.49;
    public static final String JACKHAMMER = "Jackhammer";
    public static final double JACKHAMMER_CHARGE = 2.99;

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
                .chargeDays(2)
                .discountPercent(discountPercent)
                .build();
        assertThat(rentalService.rentEquipment(request))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
