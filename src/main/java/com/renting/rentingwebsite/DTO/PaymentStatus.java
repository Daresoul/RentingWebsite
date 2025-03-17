package com.renting.rentingwebsite.DTO;


import com.renting.rentingwebsite.enums.PaymentIntentStatus;

public record PaymentStatus(PaymentIntentStatus paid, boolean retry) {
}
