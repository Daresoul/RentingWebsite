package com.renting.rentingwebsite.DTO;

import com.renting.rentingwebsite.enums.PaymentIntentStatus;

import java.time.LocalDate;

public record StripePaymentInfoRequestDTO(
        LocalDate startDate,
        LocalDate endDate,
        long rentable_id
) {
}
