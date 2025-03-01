package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.DTO.UserDTO;
import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getClients() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getClient(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping("/create")
    public ResponseEntity createClient(@RequestBody UserDTO client) throws URISyntaxException {
        User savedUser = userRepository.save(
                new User(client.name(), client.email())
        );
        return ResponseEntity.created(new URI("/clients/" + savedUser.getId())).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateClient(@PathVariable Long id, @RequestBody User user) {
        User currentUser = userRepository.findById(id).orElseThrow(RuntimeException::new);
        currentUser.setName(user.getName());
        currentUser.setEmail(user.getEmail());
        currentUser = userRepository.save(user);

        return ResponseEntity.ok(currentUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteClient(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
