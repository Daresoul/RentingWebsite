package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.DTO.RentableItemImagesDTO;
import com.renting.rentingwebsite.DTO.SpecificationDTO;
import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.entities.RentableItemImage;
import com.renting.rentingwebsite.entities.Reservation;
import com.renting.rentingwebsite.DTO.RentableItemDTO;
import com.renting.rentingwebsite.repository.RentableItemRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rentable-item")
public class RentableItemController {

    private final RentableItemRepository rentableItemRepository;

    private String getServerBaseUrl() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("Request attributes not found");
        }
        HttpServletRequest request = attrs.getRequest();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }


    public RentableItemController(RentableItemRepository rentableItemRepository) {
        this.rentableItemRepository = rentableItemRepository;
    }

    @GetMapping
    @PermitAll
    public List<RentableItemDTO> getRentables() {
        var rentables = rentableItemRepository.findAll();

        List<RentableItemDTO> rentableItems = new ArrayList<>();

        for (var rentable : rentables) {
            List<RentableItemImagesDTO> images = new ArrayList<>();

            var image = rentable.getImages().getFirst();

            String url = getServerBaseUrl() + "/api/image/" + rentable.getId() + "/" + image.getShowIndex();
            images.add(new RentableItemImagesDTO(image.getImageName(), url, image.getShowIndex()));

            List<SpecificationDTO> specifications = new ArrayList<>();

            for (var specification : rentable.getSpecifications()) {
                specifications.add(
                        new SpecificationDTO(
                                specification.getSpecificationKey().getKeyName(), specification.getValue()
                        )
                );
            }

            rentableItems.add(
                new RentableItemDTO(
                    rentable.getId(),
                    rentable.getName(),
                    rentable.getType(),
                    rentable.getDescription(),
                    rentable.getUrlName(),
                    rentable.getDisplayPrice(),
                    new ArrayList<>(),
                    images,
                    specifications
                )
            );
        }

        return rentableItems;
    }

/*    @PostMapping
    public ResponseEntity createRentable(@RequestBody Rentable rentable) throws URISyntaxException {
        Rentable savedClient = rentableRepository.save(rentable);
        return ResponseEntity.created(new URI("/clients/" + savedClient.getId())).body(savedClient);
    }*/

    @GetMapping("/byId/{id}")
    @PermitAll
    public RentableItem getRentable(@PathVariable Long id) {
        return rentableItemRepository.findById(id).orElseThrow();
    }

    @GetMapping("/{name}")
    @PermitAll
    public RentableItemDTO getRentable(@PathVariable String name) throws BadRequestException {
        RentableItem rentableItem = rentableItemRepository.findRentableByUrlName(name).orElseThrow(
                () -> new BadRequestException("Rentable with name " + name + " not found")
        );

        ArrayList<LocalDate> reservations = new ArrayList<>();
        for (Reservation reservation : rentableItem.getReservations()) {
            LocalDate currentDate = reservation.getStartAt();
            while(!currentDate.isAfter(reservation.getEndAt()))
            {
                reservations.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
        }

        List<RentableItemImagesDTO> images = new ArrayList<>();

        for (RentableItemImage image : rentableItem.getImages()) {
            String url = getServerBaseUrl() + "/api/image/" + rentableItem.getId() + "/" + image.getShowIndex();
            images.add(new RentableItemImagesDTO(image.getImageName(), url, image.getShowIndex()));
        }

        List<SpecificationDTO> specifications = new ArrayList<>();

        for (var specification : rentableItem.getSpecifications()) {
            specifications.add(
                    new SpecificationDTO(
                            specification.getSpecificationKey().getKeyName(), specification.getValue()
                    )
            );
        }

        return new RentableItemDTO(
                rentableItem.getId(),
                rentableItem.getName(),
                rentableItem.getType(),
                rentableItem.getDescription(),
                rentableItem.getUrlName(),
                rentableItem.getDisplayPrice(),
                reservations,
                images,
                specifications
        );
    }
}
