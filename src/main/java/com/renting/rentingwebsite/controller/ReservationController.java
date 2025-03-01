package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.DTO.ReservationDTO;
import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.entities.PaymentIntentLog;
import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.entities.Reservation;
import com.renting.rentingwebsite.repository.UserRepository;
import com.renting.rentingwebsite.repository.PaymentIntentLogRepository;
import com.renting.rentingwebsite.repository.RentableItemRepository;
import com.renting.rentingwebsite.repository.ReservationRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final PaymentIntentLogRepository paymentIntentLogRepository;
    private final UserRepository userRepository;
    private final RentableItemRepository rentableItemRepository;

    public ReservationController(ReservationRepository reservationRepository, PaymentIntentLogRepository paymentIntentLogRepository, UserRepository userRepository, RentableItemRepository rentableItemRepository) {
        this.reservationRepository = reservationRepository;
        this.paymentIntentLogRepository = paymentIntentLogRepository;
        this.userRepository = userRepository;
        this.rentableItemRepository = rentableItemRepository;
    }

    @PostMapping("/create")
    public Reservation CreateRental(@RequestBody ReservationDTO rental) throws BadRequestException {

        List<PaymentIntentLog> paymentIntents = paymentIntentLogRepository.findByPaymentIntentId(rental.paymentIntentId());
        if (paymentIntents.isEmpty()) {
            throw new BadRequestException("Payment Intent not found");
        }

        if (paymentIntents.size() > 1) {
            throw new BadRequestException("Multiple payment intents found, please restart the process");
        }

        PaymentIntentLog paymentIntent = paymentIntents.getFirst();

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        if (paymentIntent.getCreatedAt().isBefore(oneHourAgo)) {
            throw new BadRequestException("Payment Intent has expired");
        }

        User user = userRepository.findById(rental.client()).orElseThrow(
                () -> new BadRequestException("The sent userID is incorrect please reload.")
        );

        RentableItem rentableItem = rentableItemRepository.findById(rental.rentable()).orElseThrow(
                () -> new BadRequestException("The rentableID is incorrect please reload.")
        );

        Reservation rentals = new Reservation(
                rental.startat(),
                rental.endat(),
                false,
                rental.paidOnline(),
                user,
                rentableItem,
                rental.paymentIntentId()
        );

        reservationRepository.save(rentals);
        return rentals;
    }

}
