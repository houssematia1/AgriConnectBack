package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> existingUser = userService.getUserById(id);
        if (!existingUser.isPresent()) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé");
        }

        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/getByEmail")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            // ✅ Action admin simulée
            userService.incrementerActions(1L);
            return ResponseEntity.ok("Utilisateur supprimé avec succès.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(404).body("Utilisateur introuvable.");
        }

        User user = userOpt.get();
        user.setIsBlocked(true);
        userService.saveUserDirect(user);
        userService.incrementerActions(1L); // ✅ simule admin ID 1

        return ResponseEntity.ok("Utilisateur bloqué avec succès.");
    }

    @PutMapping("/unblock/{id}")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(404).body("Utilisateur introuvable.");
        }

        User user = userOpt.get();
        user.setIsBlocked(false);
        userService.saveUserDirect(user);
        userService.incrementerActions(1L); // ✅ idem ici

        return ResponseEntity.ok("Utilisateur débloqué avec succès.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("query") String query) {
        List<User> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam String code) {
        Optional<User> userOpt;

        try {
            userOpt = userService.getUserByEmail(email);
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body("Erreur interne : plusieurs comptes utilisent cet email.");
        }

        if (!userOpt.isPresent()) {
            return ResponseEntity.status(404).body("Utilisateur introuvable.");
        }

        User user = userOpt.get();

        if (user.isVerified()) {
            return ResponseEntity.ok("Compte déjà vérifié.");
        }

        String codeRecu = code.trim().replace("\"", "");
        String codeAttendu = user.getVerificationCode() != null
                ? user.getVerificationCode().trim()
                : "";

        if (codeAttendu.equalsIgnoreCase(codeRecu)) {
            user.setVerified(true);
            user.setVerificationCode(null);
            userService.saveUserDirect(user);
            return ResponseEntity.ok("Vérification réussie !");
        } else {
            return ResponseEntity.status(400).body("Code de vérification invalide.");
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getUserStatistics() {
        return ResponseEntity.ok(userService.getUserStats());
    }
    @GetMapping("/predict/{id}")
    public ResponseEntity<?> predictRisk(@PathVariable Long id) {
        try {
            double score = userService.predictChurnRisk(id);
            return ResponseEntity.ok(Collections.singletonMap("risk_score", score));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur IA : " + e.getMessage());
        }
    }
}
