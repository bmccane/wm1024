package net.mccons.hardware.service;

import net.mccons.hardware.dto.RentalAgreement;
import net.mccons.hardware.dto.RentalRequest;
import org.springframework.stereotype.Service;

@Service
public class RentalService {
    public RentalAgreement rentEquipment(final RentalRequest request) {
        return RentalAgreement.from(request);
    }
}
