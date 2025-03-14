package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.RentableItemSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentableItemSpecificationRepository extends JpaRepository<RentableItemSpecification, Long> {
}
