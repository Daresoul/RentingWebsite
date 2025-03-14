package com.renting.rentingwebsite.enums;

public enum PaymentIntentStatus {
    COULDNOTPROCESS,
    REQUIRES_PAYMENT_METHOD,
    REQUIRES_CONFIRMATION,
    REQUIRES_ACTION,
    PROCESSING,
    REQUIRES_CAPTURE,
    SUCCEEDED,
    CANCELED,
    REFUNDED;

    public static PaymentIntentStatus getPaymentIntentStatus(String status) {
        return switch (status) {
            case "requires_payment_method" -> REQUIRES_PAYMENT_METHOD;
            case "requires_confirmation" -> REQUIRES_CONFIRMATION;
            case "requires_action" -> REQUIRES_ACTION;
            case "processing" -> PROCESSING;
            case "requires_capture" -> REQUIRES_CAPTURE;
            case "succeeded" -> SUCCEEDED;
            case "canceled" -> CANCELED;
            default -> COULDNOTPROCESS;
        };
    }

    @Override
    public String toString() {
        return this.name();
    }
}
