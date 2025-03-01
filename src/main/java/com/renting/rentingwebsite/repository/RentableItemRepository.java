package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.RentableItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentableItemRepository extends JpaRepository<RentableItem, Long> {
    Optional<RentableItem> findRentableByUrlName(String url);
}
