package net.mccons.hardware.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Getter
@Setter
public class RentalAgreement {
    private String toolCode;
    private String toolType;
    private String brandName;
    private Integer rentalDays;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private Integer chargeDays;
    private Double preDiscountCharge;
    private Integer discountPercent;
    private Double discountAmount;
    private Double finalCharge;

    public static RentalAgreement from(final RentalRequest request) {
        return RentalAgreement.builder()
                .toolCode(request.getToolCode())
                .rentalDays(request.getDays())
                .checkoutDate(request.getCheckoutDate())
                .discountPercent(request.getDiscountPercent())
                .build();
    }

    public String print() {
        // TODO add unit test and implement
        return null;
    }
}
