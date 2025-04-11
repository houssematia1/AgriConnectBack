package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.payload.LoginRequest;
import com.example.usermanagementbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
// Si vous avez une configuration CORS globale, vous pouvez retirer cette annotation
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 1. Recherche de l'utilisateur par email
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(401).body("Utilisateur non trouvé");
        }

        // 2. Vérification du mot de passe haché
        User user = optionalUser.get();
        if (!passwordEncoder.matches(loginRequest.getMotDePasse(), user.getMotDePasse())) {
            return ResponseEntity.status(401).body("Mot de passe incorrect");
        }

        // 3. Login réussi
        // Pour une application réelle, vous pourriez générer et renvoyer un JWT ici.
        return ResponseEntity.ok("Login réussi");
    }
}
