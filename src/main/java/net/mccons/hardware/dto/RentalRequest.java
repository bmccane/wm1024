package net.mccons.hardware.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class RentalRequest {
    private String toolCode;
    private Integer rentalDayCount;
    private Integer discountPercent;
    private LocalDate checkOutDate;
}
