package com.renting.rentingwebsite.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "start_at")
    private LocalDate startAt;

    @Column(name = "end_at")
    private LocalDate endAt;

    @Column(name = "rental_paid_online")
    private boolean rentalPaidOnline;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "payment_intent_id")
    private String PaymentIntentId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "rentable_id", referencedColumnName = "id")
    private RentableItem rentableItem;

    public Reservation(LocalDate start_at, LocalDate endAt, boolean paid, boolean rentalPaidOnline, User user, RentableItem rentableItem, String paymentIntentId) {
        this.startAt = start_at;
        this.endAt = endAt;
        this.paid = paid;
        this.rentableItem = rentableItem;
        this.user = user;
        this.rentalPaidOnline = rentalPaidOnline;
        this.PaymentIntentId = paymentIntentId;
    }

    public Reservation() {
        this.startAt = LocalDate.now();
        this.endAt = LocalDate.now();
        this.paid = false;
        this.rentableItem = null;
        this.user = null;
        this.rentalPaidOnline = false;
    }

    public LocalDate getStartAt() {
        return startAt;
    }

    public LocalDate getEndAt() {
        return endAt;
    }
}
