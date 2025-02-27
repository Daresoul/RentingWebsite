package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.entities.Rentable;
import com.renting.rentingwebsite.entities.Rentals;
import com.renting.rentingwebsite.DTO.RentableStandardDTO;
import com.renting.rentingwebsite.repository.RentableRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rentable")
public class RentableController {

    private final RentableRepository rentableRepository;

    public RentableController(RentableRepository rentableRepository) {
        this.rentableRepository = rentableRepository;
    }

    @GetMapping
    public List<Rentable> getRentables() {
        return rentableRepository.findAll();
    }

    @PostMapping
    public ResponseEntity createRentable(@RequestBody Rentable rentable) throws URISyntaxException {
        Rentable savedClient = rentableRepository.save(rentable);
        return ResponseEntity.created(new URI("/clients/" + savedClient.getId())).body(savedClient);
    }

    @GetMapping("/byId/{id}")
    public Rentable getRentable(@PathVariable Long id) {
        return rentableRepository.findById(id).orElseThrow();
    }

    @GetMapping("/{name}")
    public RentableStandardDTO getRentable(@PathVariable String name) {
        Rentable rentable = rentableRepository.findRentableByUrlName(name).orElseThrow();
        ArrayList<LocalDate> l = new ArrayList<>();
        for (Rentals rental: rentable.getRentals()) {
            LocalDate currentDate = rental.getStart_at();
            while(!currentDate.isAfter(rental.getEnd_at()))
            {
                l.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
        }
        return new RentableStandardDTO(rentable.getName(), rentable.getType(), rentable.getPrice(), l);
    }
}
