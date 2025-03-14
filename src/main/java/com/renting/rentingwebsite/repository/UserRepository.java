package com.renting.rentingwebsite.repository;

import com.renting.rentingwebsite.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByStripeCustomerId(String stripeCustomerId);
}
