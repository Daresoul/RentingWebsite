package com.renting.rentingwebsite.DTO;

import java.time.LocalDate;

public record ReservationDTO(
        LocalDate startat,
        LocalDate endat,
        Long rentable
) {

}
