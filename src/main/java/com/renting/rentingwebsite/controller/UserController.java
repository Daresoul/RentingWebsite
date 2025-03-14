package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.DTO.AuthRequest;
import com.renting.rentingwebsite.DTO.AuthResponse;
import com.renting.rentingwebsite.DTO.UserRegisterDTO;
import com.renting.rentingwebsite.Service.AuthService;
import com.renting.rentingwebsite.Service.StripeService.StripeService;
import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.enums.UserRole;
import com.renting.rentingwebsite.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    private final StripeService stripeService;

    private final AuthService authService;

    public UserController(UserRepository userRepository, StripeService stripeService, AuthService authService) {
        this.userRepository = userRepository;
        this.stripeService = stripeService;
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getClients() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getClient(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping("/create")
    @PermitAll
    public AuthResponse createClient(@RequestBody UserRegisterDTO client) throws StripeException {
        Customer customer = stripeService.createCustomer(client.name(), client.password());

        userRepository.save(
                new User(client.name(), client.email(), client.password(), new HashSet<>(Set.of(UserRole.CUSTOMER)), customer.getId())
        );
        return authService.authenticate(new AuthRequest(client.email(), client.password()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity updateClient(@PathVariable Long id, @RequestBody User user) {
        User currentUser = userRepository.findById(id).orElseThrow(RuntimeException::new);
        currentUser.setName(user.getName());
        currentUser.setEmail(user.getEmail());
        currentUser = userRepository.save(user);

        return ResponseEntity.ok(currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity deleteClient(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
