package com.renting.rentingwebsite.DTO;

import java.time.LocalDate;

public record StripePaymentInfoRequestDTO(String receiptEmail, LocalDate startDate, LocalDate endDate, long rentable_id) {}
