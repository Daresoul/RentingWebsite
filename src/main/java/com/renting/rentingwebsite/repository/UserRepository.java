package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
