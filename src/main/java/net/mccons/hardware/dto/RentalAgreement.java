package net.mccons.hardware.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder(toBuilder = true)
@Getter
@Setter
public class RentalAgreement {
    private String toolCode;
    private String toolType;
    private String toolBrand;
    private Integer rentalDays;
    private LocalDate checkOutDate;
    private LocalDate dueDate;
    private BigDecimal dailyRentalCharge;
    private Integer chargeDays;
    private BigDecimal preDiscountCharge;
    private Integer discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal finalCharge;

    public static RentalAgreement from(final RentalRequest request) {
        return RentalAgreement.builder()
                .toolCode(request.getToolCode())
                .rentalDays(request.getRentalDayCount())
                .checkOutDate(request.getCheckOutDate())
                .discountPercent(request.getDiscountPercent())
                .build();
    }

    public String print() {
        // TODO add unit test and implement
        return null;
    }
}
