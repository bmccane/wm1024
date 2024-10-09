package net.mccons.hardware.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Builder(toBuilder = true)
@Getter
@Setter
public class RentalAgreement {
    private final static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");

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
        return String.format("""
                        Tool code: %s
                        Tool type: %s
                        Tool brand: %s
                        Rental days: %d
                        Check out date: %s
                        Due date: %s
                        Daily rental charge: %s
                        Charge days: %s
                        Pre-discount charge: %s
                        Discount percent: %d%%
                        Discount amount: %s
                        Final charge: %s
                        """,
                getToolCode(),
                getToolType(),
                getToolBrand(),
                getRentalDays(),
                formatDate(getCheckOutDate()),
                formatDate(getDueDate()),
                formatCurrency(getDailyRentalCharge()),
                getChargeDays(),
                formatCurrency(getPreDiscountCharge()),
                getDiscountPercent(),
                formatCurrency(getDiscountAmount()),
                formatCurrency(getFinalCharge()));
    }

    public String formatCurrency(final BigDecimal value) {
        return currencyFormatter.format(value);
    }

    public String formatDate(final LocalDate date) {
        return date.format(dateTimeFormatter);
    }
}
