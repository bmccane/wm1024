package net.mccons.hardware.service;

import net.mccons.hardware.dto.RentalAgreement;
import net.mccons.hardware.dto.RentalRequest;
import net.mccons.hardware.exceptions.ToolCodeException;
import net.mccons.hardware.model.Tool;
import net.mccons.hardware.repository.ToolRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class RentalService {
    private final ToolRepository toolRepository;

    public RentalService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    private static boolean isChargeableWeekday(Tool tool, LocalDate date) {
        return (tool.getType().isWeekday() && TypeOfDayService.isWeekday(date)) ||
                !TypeOfDayService.isWeekday(date);
    }

    private static boolean isChargeableWeekend(Tool tool, LocalDate date) {
        return (tool.getType().isWeekend() && TypeOfDayService.isWeekend(date)) ||
                !TypeOfDayService.isWeekend(date);
    }

    private static boolean isChargeableHoliday(Tool tool, LocalDate date) {
        return (tool.getType().isHoliday() && TypeOfDayService.isHoliday(date)) ||
                !TypeOfDayService.isHoliday(date);
    }

    public RentalAgreement rentEquipment(final RentalRequest request) {
        final var tool = toolRepository.findById(request.getToolCode())
                .orElseThrow(() -> new ToolCodeException("No tool found matching: " + request.getToolCode()));

        final var firstChargeDate = request.getCheckOutDate().plusDays(1);
        final var endDate = firstChargeDate.plusDays(request.getRentalDayCount());
        final var chargeDays = firstChargeDate.datesUntil(endDate)
                .filter(date -> isChargeableWeekday(tool, date))
                .filter(date -> isChargeableWeekend(tool, date))
                .filter(date -> isChargeableHoliday(tool, date))
                .count();
        final var preDiscountCharge = tool.getType().getCharge()
                .multiply(BigDecimal.valueOf(chargeDays))
                .setScale(2, RoundingMode.HALF_UP);
        final var discountAmount = preDiscountCharge
                .multiply(BigDecimal.valueOf(request.getDiscountPercent().doubleValue() / 100.))
                .setScale(2, RoundingMode.HALF_UP);

        return RentalAgreement.from(request)
                .toBuilder()
                .toolType(tool.getType().getName())
                .toolBrand(tool.getBrand().getName())
                .dueDate(request.getCheckOutDate().plusDays(request.getRentalDayCount()))
                .dailyRentalCharge(tool.getType().getCharge())
                .chargeDays((int) chargeDays)
                .preDiscountCharge(preDiscountCharge)
                .discountAmount(discountAmount)
                .finalCharge(preDiscountCharge.subtract(discountAmount))
                .build();
    }
}
