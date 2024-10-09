package net.mccons.hardware.service;

import net.mccons.hardware.dto.RentalAgreement;
import net.mccons.hardware.dto.RentalRequest;
import net.mccons.hardware.exceptions.ToolCodeException;
import net.mccons.hardware.model.Tool;
import net.mccons.hardware.repository.ToolRepository;
import org.springframework.stereotype.Service;

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

        return RentalAgreement.from(request)
                .toBuilder()
                .toolType(tool.getType().getName())
                .toolBrand(tool.getBrand().getName())
                .dueDate(request.getCheckOutDate().plusDays(request.getRentalDayCount()))
                .dailyRentalCharge(tool.getType().getCharge())
                .chargeDays((int) chargeDays)
                .build();
    }
}
