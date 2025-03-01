package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
