package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.payload.LoginRequest;
import com.example.usermanagementbackend.repository.UserRepository;
import com.example.usermanagementbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

        userService.mettreAJourConnexion(user);
        user.setMotDePasse(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        try {
            userService.sendPasswordResetCode(email);
            return ResponseEntity.ok("Code de réinitialisation envoyé à " + email);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
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
        user.setResetCode(null);
        userRepository.save(user);

        return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
    }

    @PostMapping("/login-face")
    public ResponseEntity<?> loginWithFace(@RequestBody Map<String, String> payload) {
        try {
            String faceDescriptorStr = payload.get("faceDescriptor");

            if (faceDescriptorStr == null || faceDescriptorStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Descripteur facial manquant");
            }

            float[] inputDescriptor = parseFaceDescriptor(faceDescriptorStr);
            if (inputDescriptor.length != 128) {
                return ResponseEntity.status(400).body("Descripteur facial invalide");
            }

            for (User user : userRepository.findAll()) {
                if (user.getFaceDescriptor() == null) continue;

                try {
                    float[] storedDescriptor = parseFaceDescriptor(user.getFaceDescriptor());
                    float distance = calculateEuclideanDistance(inputDescriptor, storedDescriptor);

                    System.out.println("🔍 Comparaison avec: " + user.getEmail() + " | distance = " + distance);

                    if (distance < 0.6f) {
                        if (user.isBlocked()) {
                            return ResponseEntity.status(403).body("Compte bloqué.");
                        }

                        userService.mettreAJourConnexion(user);
                        user.setMotDePasse(null);
                        return ResponseEntity.ok(user);
                    }
                } catch (Exception e) {
                    System.err.println("❌ Erreur de parsing pour l'utilisateur " + user.getEmail() + " : " + e.getMessage());
                }
            }

            return ResponseEntity.status(401).body("Aucun visage correspondant trouvé.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur interne : " + e.getMessage());
        }
    }

    private float[] parseFaceDescriptor(String descriptorStr) {
        try {
            String[] parts = descriptorStr.replace("[", "").replace("]", "").split(",");
            float[] descriptor = new float[parts.length];

            for (int i = 0; i < parts.length; i++) {
                descriptor[i] = Float.parseFloat(parts[i].trim());
            }

            return descriptor;
        } catch (Exception e) {
            throw new IllegalArgumentException("Format de descripteur facial invalide");
        }
    }

    private float calculateEuclideanDistance(float[] d1, float[] d2) {
        if (d1.length != d2.length) return Float.MAX_VALUE;

        float sum = 0f;
        for (int i = 0; i < d1.length; i++) {
            float diff = d1[i] - d2[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }
}
