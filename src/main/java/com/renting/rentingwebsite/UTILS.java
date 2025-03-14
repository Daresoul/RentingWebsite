package com.renting.rentingwebsite;

import java.time.*;

public class UTILS {
    public static LocalDate convertTimezoneToLocalDate(LocalDateTime localDateTime) {
        ZoneId copenhagenZone = ZoneId.of("Europe/Copenhagen");
        return ZonedDateTime.of(localDateTime, copenhagenZone).toLocalDate();
    }
}
