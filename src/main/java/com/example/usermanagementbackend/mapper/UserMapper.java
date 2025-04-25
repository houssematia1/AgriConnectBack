package com.example.usermanagementbackend.mapper;

import com.example.usermanagementbackend.dto.UserDTO;
import com.example.usermanagementbackend.entity.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getNom(),
                user.getPrenom(), // Map prenom
                user.getEmail(),
                null, // Don't send password
                user.getNumeroDeTelephone(),
                user.getRole(),
                user.getAdresseLivraison()
        );
    }

    public static User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setEmail(dto.getEmail());
        user.setMotDePasse(dto.getMotDePasse());
        user.setNumeroDeTelephone(dto.getNumeroDeTelephone());
        user.setRole(dto.getRole());
        user.setAdresseLivraison(dto.getAdresseLivraison());
        return user;
    }
}