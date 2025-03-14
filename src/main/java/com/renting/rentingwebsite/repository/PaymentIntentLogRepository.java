package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.PaymentIntentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentIntentLogRepository extends JpaRepository<PaymentIntentLog, Long> {
    Optional<PaymentIntentLog> findByPaymentIntentId(String paymentIntentId);
}
