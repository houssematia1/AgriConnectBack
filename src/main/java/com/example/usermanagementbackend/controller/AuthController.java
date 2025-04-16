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
        Optional<User> optionalUser;
        try {
            optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Collections.singletonMap("error", "Conflit : plusieurs comptes existent avec cet email."));
        }

        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("error", "Utilisateur non trouvé"));
        }

        User user = optionalUser.get();

        if (user.isBlocked()) {
            return ResponseEntity.status(403)
                    .body(Collections.singletonMap("error", "Votre compte est bloqué. Veuillez contacter l’administrateur."));
        }

        if (!passwordEncoder.matches(loginRequest.getMotDePasse(), user.getMotDePasse())) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("error", "Mot de passe incorrect"));
        }

        user.setMotDePasse(null);
        return ResponseEntity.ok(user);
    }


}
