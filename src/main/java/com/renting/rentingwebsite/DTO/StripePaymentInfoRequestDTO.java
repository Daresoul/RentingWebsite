package com.renting.rentingwebsite.DTO;

public record StripePaymentInfoRequestDTO(int amount, String receiptEmail){}
