package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.DTO.AuthRequest;
import com.renting.rentingwebsite.DTO.AuthResponse;
import com.renting.rentingwebsite.DTO.UserDTO;
import com.renting.rentingwebsite.Service.AuthService;
import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.security.CurrentUser;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @PermitAll
    @ResponseBody
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }

    @GetMapping("/check")
    @PreAuthorize("isAuthenticated()")
    public UserDTO checkUserLoggedIn(@CurrentUser User user) {
        return new UserDTO(user);
    }
}
