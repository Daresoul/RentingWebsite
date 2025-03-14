package com.renting.rentingwebsite.Service;

import com.renting.rentingwebsite.DTO.AuthRequest;
import com.renting.rentingwebsite.DTO.AuthResponse;
import com.renting.rentingwebsite.DTO.UserDTO;
import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.enums.UserRole;
import com.renting.rentingwebsite.repository.UserRepository;
import com.renting.rentingwebsite.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse authenticate(AuthRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.email());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(request.password(), user.getPassword())) {

                Set<String> roleNames = user.getRoles().stream()
                        .map(UserRole::name)
                        .collect(Collectors.toSet());
                String token = jwtUtil.generateToken(user.getEmail(), roleNames);

                return new AuthResponse(token, new UserDTO(user));
            } else {
                log.warn("Password does not match for user: " + user.getEmail());
            }
        } else {
            log.warn("User not found for email: " + request.email());
        }

        throw new AuthenticationCredentialsNotFoundException("Invalid email or password");
    }

}
