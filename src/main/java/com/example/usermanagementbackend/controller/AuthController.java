package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.dto.LivreurDTO;
import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.mapper.UserMapper;
import com.example.usermanagementbackend.payload.LoginRequest;
import com.example.usermanagementbackend.repository.UserRepository;
import com.example.usermanagementbackend.service.LivreurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private LivreurService livreurService;

    public AuthController(LivreurService livreurService) {
        this.livreurService = livreurService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("error", "Utilisateur non trouvé"));
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(loginRequest.getMotDePasse(), user.getMotDePasse())) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("error", "Mot de passe incorrect"));
        }

        return ResponseEntity.ok(UserMapper.toDTO(user)); // Return DTO with prenom
    }

    @GetMapping("/me")
    public ResponseEntity<LivreurDTO> getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        LivreurDTO livreur = LivreurService.findByUserId(Long.parseLong(userId));
        return ResponseEntity.ok(livreur);
    }
}