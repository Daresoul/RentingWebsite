package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.PaymentIntentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentIntentLogRepository extends JpaRepository<PaymentIntentLog, Long> {
    List<PaymentIntentLog> findByPaymentIntentId(String paymentIntentId);
}
