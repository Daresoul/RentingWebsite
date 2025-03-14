package com.renting.rentingwebsite.entities;

import com.renting.rentingwebsite.enums.PaymentIntentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "payment_intent_log")
public class PaymentIntentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_intent_id", nullable = false, unique = true, updatable = false)
    private String paymentIntentId;

    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private PaymentIntentStatus status;

    @Column(name = "amount", updatable = false)
    private long amount;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    private User user_id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", referencedColumnName = "id")
    private Reservation reservation;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public PaymentIntentLog() {
        this.paymentIntentId = UUID.randomUUID().toString();
        this.amount = 1;
        this.user_id = null;
        this.reservation = null;
    }

    public PaymentIntentLog(String paymentIntentId, User user, long amount) {
        this.paymentIntentId = paymentIntentId;
        this.amount = amount;
        this.user_id = user;
        this.reservation = null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(PaymentIntentStatus status) {
        this.status = status;
    }
}
