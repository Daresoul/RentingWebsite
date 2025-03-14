package com.renting.rentingwebsite.entities;


import com.renting.rentingwebsite.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "stripe_customer_id", nullable = false, unique = true)
    private String stripeCustomerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    public User(String name, String email, String password, String stripeCustomerId) {
        this(name, email, password, new HashSet<>(Set.of(UserRole.CUSTOMER)), stripeCustomerId);
    }

    public User(String name, String email, String password, Set<UserRole> roles, String stripeCustomerId) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.name = name;
        this.email = email;
        this.password = passwordEncoder.encode(password);
        this.roles = roles;
        this.stripeCustomerId = stripeCustomerId;
    }

    public User() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.name = "Default";
        this.email = "Default";
        this.password = passwordEncoder.encode("Default");
        this.roles = new HashSet<>(Set.of(UserRole.CUSTOMER));
        this.stripeCustomerId = "cus_RtEi7G8Hopajil";
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
