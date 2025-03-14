package com.renting.rentingwebsite.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StripePaymentInfoRequestDTO(
        LocalDate startDate,
        LocalDate endDate,
        long rentable_id
) {}
