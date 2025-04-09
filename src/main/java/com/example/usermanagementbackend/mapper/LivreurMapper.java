package com.example.usermanagementbackend.mapper;

import com.example.usermanagementbackend.dto.LivreurDTO;
import com.example.usermanagementbackend.entity.Livreur;

public class LivreurMapper {

    public static LivreurDTO toDTO(Livreur livreur) {
        if (livreur == null) return null;
        return new LivreurDTO(
                livreur.getId(),
                livreur.getNom(),
                livreur.getEmail(),
                livreur.getTelephone(),
                livreur.getVehicule(),
                livreur.getUser() != null ? livreur.getUser().getId() : null
        );
    }

    public static Livreur toEntity(LivreurDTO dto) {
        if (dto == null) return null;
        Livreur livreur = new Livreur();
        livreur.setId(dto.getId());
        livreur.setNom(dto.getNom());
        livreur.setEmail(dto.getEmail());
        livreur.setTelephone(dto.getTelephone());
        livreur.setVehicule(dto.getVehicule());
        return livreur;
    }
}