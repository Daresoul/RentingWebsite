package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.Reservation;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r WHERE " +
            "(r.startAt <= :endDate AND r.endAt >= :startDate)")
    List<Reservation> findOverlappingReservations(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

