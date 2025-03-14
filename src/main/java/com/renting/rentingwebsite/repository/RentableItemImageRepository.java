package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.entities.RentableItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentableItemImageRepository extends JpaRepository<RentableItemImage, Long> {

    Optional<RentableItemImage> findByShowIndexAndRentableItem(float showIndex, RentableItem rentableItem);
}
