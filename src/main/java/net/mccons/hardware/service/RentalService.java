package net.mccons.hardware.service;

import net.mccons.hardware.dto.RentalAgreement;
import net.mccons.hardware.dto.RentalRequest;
import net.mccons.hardware.exceptions.ToolCodeException;
import net.mccons.hardware.repository.ToolRepository;
import org.springframework.stereotype.Service;

@Service
public class RentalService {
    private final ToolRepository toolRepository;

    public RentalService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    public RentalAgreement rentEquipment(final RentalRequest request) {
        final var tool = toolRepository.findById(request.getToolCode())
                .orElseThrow(() -> new ToolCodeException("No tool found matching: " + request.getToolCode()));

        return RentalAgreement.from(request)
                .toBuilder()
                .toolType(tool.getType().getName())
                .brandName(tool.getBrand().getName())
                .dailyRentalCharge(tool.getType().getCharge())
                .build();
    }
}
