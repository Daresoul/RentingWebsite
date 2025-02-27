package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
