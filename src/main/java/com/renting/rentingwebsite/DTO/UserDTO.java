package com.renting.rentingwebsite.DTO;

import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.enums.UserRole;

import java.util.Optional;
import java.util.Set;

public record UserDTO(String name, String email, Optional<Set<UserRole>> roles) {

    public UserDTO(User user) {
        this(user.getName(), user.getEmail(), Optional.of(user.getRoles()));
    }
}
