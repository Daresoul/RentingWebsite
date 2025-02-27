package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.Rentals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalsRepository extends JpaRepository<Rentals, Long> {
    List<Rentals> findByMachineId(Long rentableId);
}
