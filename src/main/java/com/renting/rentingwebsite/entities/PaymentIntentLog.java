package com.renting.rentingwebsite.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "payment_intent_log")
public class PaymentIntentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreationTimestamp // ✅ Automatically sets creation time
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name="user_email")
    private String userEmail;

    @Column(name = "units")
    private long units;

    @Column(name = "amount")
    private long amount;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public PaymentIntentLog() {
        this.paymentIntentId = UUID.randomUUID().toString();
        this.userEmail = UUID.randomUUID().toString();
        this.units = 1;
        this.amount = 1;
    }

    public PaymentIntentLog(String paymentIntentId, String userEmail, long units, long amount) {
        this.paymentIntentId = paymentIntentId;
        this.userEmail = userEmail;
        this.units = units;
        this.amount = amount;
    }
 }
