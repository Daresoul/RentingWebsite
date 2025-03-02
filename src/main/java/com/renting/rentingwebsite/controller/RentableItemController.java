package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.entities.Reservation;
import com.renting.rentingwebsite.DTO.RentableItemDTO;
import com.renting.rentingwebsite.repository.RentableItemRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rentable-item")
public class RentableItemController {

    private final RentableItemRepository rentableItemRepository;

    public RentableItemController(RentableItemRepository rentableItemRepository) {
        this.rentableItemRepository = rentableItemRepository;
    }

    @GetMapping
    public List<RentableItem> getRentables() {
        return rentableItemRepository.findAll();
    }

/*    @PostMapping
    public ResponseEntity createRentable(@RequestBody Rentable rentable) throws URISyntaxException {
        Rentable savedClient = rentableRepository.save(rentable);
        return ResponseEntity.created(new URI("/clients/" + savedClient.getId())).body(savedClient);
    }*/

    @GetMapping("/byId/{id}")
    public RentableItem getRentable(@PathVariable Long id) {
        return rentableItemRepository.findById(id).orElseThrow();
    }

    @GetMapping("/{name}")
    public RentableItemDTO getRentable(@PathVariable String name) throws BadRequestException {
        RentableItem rentableItem = rentableItemRepository.findRentableByUrlName(name).orElseThrow(
                () -> new BadRequestException("Rentable with name " + name + " not found")
        );

        ArrayList<LocalDate> l = new ArrayList<>();
        for (Reservation reservation : rentableItem.getReservations()) {
            LocalDate currentDate = reservation.getStartAt();
            while(!currentDate.isAfter(reservation.getEndAt()))
            {
                l.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
        }
        return new RentableItemDTO(rentableItem.getId(), rentableItem.getName(), rentableItem.getType(), rentableItem.getDescription(), rentableItem.getUrlName(), rentableItem.getPrice(), l);
    }
}
