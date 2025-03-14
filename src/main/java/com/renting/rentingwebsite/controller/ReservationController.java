package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.DTO.ErrorResponseDTO;
import com.renting.rentingwebsite.DTO.ReservationDTO;
import com.renting.rentingwebsite.Service.ReservationService;
import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.entities.PaymentIntentLog;
import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.entities.Reservation;
import com.renting.rentingwebsite.repository.UserRepository;
import com.renting.rentingwebsite.repository.PaymentIntentLogRepository;
import com.renting.rentingwebsite.repository.RentableItemRepository;
import com.renting.rentingwebsite.repository.ReservationRepository;
import com.renting.rentingwebsite.security.CurrentUser;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/*
EXAMPLES OF AUTH
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("isAuthenticated()")
@PostAuthorize("returnObject.owner == authentication.name")
 */

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    private final RentableItemRepository rentableItemRepository;
    private final ReservationService reservationService;

    public ReservationController(RentableItemRepository rentableItemRepository, ReservationService reservationService) {
        this.rentableItemRepository = rentableItemRepository;
        this.reservationService = reservationService;
    }

    @PostMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> validateReservationCreation(@CurrentUser User user, @RequestBody ReservationDTO reservationDTO) throws BadRequestException {
        boolean overlap = reservationService.CheckIfDateOverlap(reservationDTO.startat(), reservationDTO.endat());

        if (overlap) {
            throw new BadRequestException("The reservation period has other reservations inside.");
        }

        rentableItemRepository.findById(reservationDTO.rentable()).orElseThrow(
                () -> new BadRequestException("The rentable item could not be found.")
        );


        return ResponseEntity.ok().build();
    }

}
