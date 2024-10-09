package net.mccons.hardware.service;

import net.mccons.hardware.dto.RentalAgreement;
import net.mccons.hardware.dto.RentalRequest;
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

@SpringBootTest
class RentalServiceTest {

    public static final double CHAINSAW_CHARGE = 1.99;
    public static final double LADDER_CHARGE = 1.49;
    public static final double JACKHAMMER_CHARGE = 2.99;

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
        var chainsaw = toolTypeRepository.save(ToolType.builder().name("Chainsaw").charge(CHAINSAW_CHARGE).weekday(true).weekend(true).build());
        var ladder = toolTypeRepository.save(ToolType.builder().name("Ladder").charge(LADDER_CHARGE).weekday(true).holiday(true).build());
        var jackhammer = toolTypeRepository.save(ToolType.builder().name("Jackhammer").charge(JACKHAMMER_CHARGE).weekday(true).build());

        var dewalt = toolBrandRepository.save(ToolBrand.builder().name("DeWalt").build());
        var ridgid = toolBrandRepository.save(ToolBrand.builder().name("Ridgid").build());
        var stihl = toolBrandRepository.save(ToolBrand.builder().name("Stihl").build());
        var werner = toolBrandRepository.save(ToolBrand.builder().name("Werner").build());

        toolRepository.save(Tool.builder().code("CHNS").type(chainsaw).brand(stihl).build());
        toolRepository.save(Tool.builder().code("LADW").type(ladder).brand(werner).build());
        toolRepository.save(Tool.builder().code("JAKD").type(jackhammer).brand(dewalt).build());
        toolRepository.save(Tool.builder().code("JAKR").type(jackhammer).brand(ridgid).build());
    }

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
