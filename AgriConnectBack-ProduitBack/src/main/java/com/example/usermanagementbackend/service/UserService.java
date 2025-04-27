package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Fidelite;
import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FideliteService fideliteService;

    @Transactional
    public User saveUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Un compte avec cet email existe déjà.");
        }
        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(user.getMotDePasse());
            user.setMotDePasse(hashedPassword);
        }
        // Save the user first
        User savedUser = userRepository.save(user);
        // Create and save Fidelite using FideliteService
        Fidelite fidelite = new Fidelite(null, 0, "Bronze", savedUser);
        fideliteService.saveFidelite(fidelite);
        return savedUser;
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User user) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + id);
        }
        User existingUser = existingUserOpt.get();
        existingUser.setNom(user.getNom());
        existingUser.setPrenom(user.getPrenom());
        existingUser.setEmail(user.getEmail());
        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(user.getMotDePasse());
            existingUser.setMotDePasse(hashedPassword);
        }
        existingUser.setNumeroDeTelephone(user.getNumeroDeTelephone());
        existingUser.setRole(user.getRole());
        existingUser.setAdresseLivraison(user.getAdresseLivraison());
        existingUser.setDateOfBirth(user.getDateOfBirth());
        return userRepository.save(existingUser);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query);
    }
}