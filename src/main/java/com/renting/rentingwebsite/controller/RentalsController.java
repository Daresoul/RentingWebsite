package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.repository.RentalsRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentable")
public class RentalsController {
    private final RentalsRepository rentalsRepository;

    public RentalsController(RentalsRepository rentalsRepository) {
        this.rentalsRepository = rentalsRepository;
    }

}
