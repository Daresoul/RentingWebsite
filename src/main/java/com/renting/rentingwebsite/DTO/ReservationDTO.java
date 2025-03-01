package com.renting.rentingwebsite.DTO;

import java.time.LocalDate;

public record ReservationDTO(
        LocalDate startat,
        LocalDate endat,
        Long client,
        Long rentable,
        boolean paidOnline,
        String paymentIntentId
) {

}
