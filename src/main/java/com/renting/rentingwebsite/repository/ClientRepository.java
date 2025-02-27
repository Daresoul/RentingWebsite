package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
