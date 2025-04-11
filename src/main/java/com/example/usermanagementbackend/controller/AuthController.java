package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.payload.LoginRequest;
import com.example.usermanagementbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Recherche de l'utilisateur par email
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("error", "Utilisateur non trouvé"));
        }

        User user = optionalUser.get();
        // Vérification du mot de passe
        if (!passwordEncoder.matches(loginRequest.getMotDePasse(), user.getMotDePasse())) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("error", "Mot de passe incorrect"));
        }




        user.setMotDePasse(null);
        return ResponseEntity.ok(user);
    }
}
