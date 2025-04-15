package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.dto.UserDTO;
import com.example.usermanagementbackend.entity.Livreur;
import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.mapper.UserMapper;
import com.example.usermanagementbackend.repository.LivreurRepository;
import com.example.usermanagementbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LivreurRepository livreurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO saveUser(UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Un compte avec cet email existe déjà.");
        }

        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        }

        User savedUser = userRepository.save(user);

        if ("LIVREUR".equalsIgnoreCase(savedUser.getRole())) {
            Livreur livreur = new Livreur();
            livreur.setNom(savedUser.getNom() + " " + savedUser.getPrenom());
            livreur.setEmail(savedUser.getEmail());
            livreur.setTelephone(savedUser.getNumeroDeTelephone());
            livreur.setUser(savedUser);
            livreurRepository.save(livreur);
        }

        return UserMapper.toDTO(savedUser);
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

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        User existingUser = existingUserOpt.get();
        existingUser.setNom(userDTO.getNom());
        existingUser.setPrenom(userDTO.getPrenom());
        existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getMotDePasse() != null && !userDTO.getMotDePasse().isEmpty()) {
            existingUser.setMotDePasse(passwordEncoder.encode(userDTO.getMotDePasse()));
        }
        existingUser.setNumeroDeTelephone(userDTO.getNumeroDeTelephone());
        existingUser.setRole(userDTO.getRole());
        existingUser.setAdresseLivraison(userDTO.getAdresseLivraison());

        User updatedUser = userRepository.save(existingUser);

        if ("LIVREUR".equalsIgnoreCase(updatedUser.getRole())) {
            Optional<Livreur> existingLivreur = livreurRepository.findById(id);
            Livreur livreur = existingLivreur.orElse(new Livreur());
            livreur.setNom(updatedUser.getNom() + " " + updatedUser.getPrenom());
            livreur.setEmail(updatedUser.getEmail());
            livreur.setTelephone(updatedUser.getNumeroDeTelephone());
            livreur.setUser(updatedUser);
            livreurRepository.save(livreur);
        }

        return UserMapper.toDTO(updatedUser);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query);
    }
}
