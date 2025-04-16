package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.payload.LoginRequest;
import com.example.usermanagementbackend.repository.UserRepository;
import com.example.usermanagementbackend.service.UserService;
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

    @Autowired
    private UserService userService;

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

        // ✅ Mettre à jour dernière connexion + nombre de connexions
        userService.mettreAJourConnexion(user);

        user.setMotDePasse(null); // Ne jamais retourner le mot de passe
        return ResponseEntity.ok(user);
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        try {
            userService.sendPasswordResetCode(email);  // ✅ envoie le code
            return ResponseEntity.ok("Code de réinitialisation envoyé à " + email);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String newPassword) {

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(404).body("Utilisateur introuvable.");
        }

        User user = userOpt.get();
        String expectedCode = user.getResetCode();

        if (expectedCode == null || !expectedCode.equals(code.trim())) {
            return ResponseEntity.status(400).body("Code invalide.");
        }

        user.setMotDePasse(passwordEncoder.encode(newPassword));
        user.setResetCode(null); // on vide le code
        userRepository.save(user);

        return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
    }
}
