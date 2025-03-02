package com.renting.rentingwebsite.DTO;

import java.time.LocalDate;
import java.util.List;

public record RentableItemDTO(long id, String name, String type, String description, String urlName, double price, List<LocalDate> dates) {
}
