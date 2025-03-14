package com.renting.rentingwebsite.DTO;

import com.renting.rentingwebsite.enums.UserRole;

import java.util.Optional;
import java.util.Set;

public record UserRegisterDTO(String name, String email, String password, Optional<Set<UserRole>> roles) {
}
