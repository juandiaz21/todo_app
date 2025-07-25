package com.prueba.todoapp.controller;

import com.prueba.todoapp.model.User;
import com.prueba.todoapp.config.jwt.JwtUtils;
import com.prueba.todoapp.repository.UserRepo;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserRESTController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = jwtUtils.generateAccesToken(username);

            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username y password son requeridos");
        }

        if (userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);

        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepo.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<?> getUserByID(@PathVariable Long id) {
        Optional<User> user = userRepo.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
