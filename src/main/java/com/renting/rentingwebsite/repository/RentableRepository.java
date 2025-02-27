package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.Rentable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentableRepository extends JpaRepository<Rentable, Long> {
    Optional<Rentable> findRentableByUrlName(String url);
}
