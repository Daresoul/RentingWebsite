package com.renting.rentingwebsite.Service;

import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.entities.Reservation;
import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.repository.PaymentIntentLogRepository;
import com.renting.rentingwebsite.repository.RentableItemRepository;
import com.renting.rentingwebsite.repository.ReservationRepository;
import com.stripe.model.PaymentIntent;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentIntentLogRepository paymentIntentLogRepository;
    private final RentableItemRepository rentableItemRepository;

    public ReservationService(ReservationRepository reservationRepository, PaymentIntentLogRepository paymentIntentLogRepository, RentableItemRepository rentableItemRepository) {
        this.reservationRepository = reservationRepository;
        this.paymentIntentLogRepository = paymentIntentLogRepository;
        this.rentableItemRepository = rentableItemRepository;
    }

    public boolean CheckIfDateOverlap(LocalDate startDate, LocalDate endDate) {
        return !reservationRepository.findOverlappingReservations(startDate, endDate).isEmpty();
    }

    public Reservation createReservation(
            LocalDate startAt,
            LocalDate endAt,
            RentableItem rentableItem,
            boolean paidOnline,
            PaymentIntent paymentIntent,
            User user
    ) throws BadRequestException {

        // TODO IF ANYTHING FAILS, PLEASE REFUND THE ORDER!
        if(CheckIfDateOverlap(startAt, endAt)) {
            throw new BadRequestException("Date overlaps with previous reservation");
        }


        Reservation reservation = new Reservation(
                startAt,
                endAt,
                true,
                paidOnline,
                user,
                rentableItem,
                paymentIntent.getId()
        );

        reservationRepository.save(reservation);
        return reservation;
    }
}
