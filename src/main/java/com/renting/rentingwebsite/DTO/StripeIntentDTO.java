package com.renting.rentingwebsite.DTO;

import java.util.List;

public record StripeIntentDTO(
        String paymentIntentId,
        String clientSecret,
        List<String> paymentMethods,
        long amount,
        long cost
) {
}
