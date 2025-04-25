package com.example.usermanagementbackend.mapper;

import com.example.usermanagementbackend.dto.LivreurDTO;
import com.example.usermanagementbackend.entity.Livreur;

public class LivreurMapper {

    public static LivreurDTO toDTO(Livreur livreur) {
        LivreurDTO dto = new LivreurDTO();
        dto.setId(livreur.getId());
        dto.setNom(livreur.getNom());
        dto.setEmail(livreur.getEmail());
        dto.setTelephone(livreur.getTelephone());
        dto.setUserId(livreur.getUserId());
        dto.setPhoto(livreur.getPhoto());
        return dto;
    }

    public static Livreur toEntity(LivreurDTO dto) {
        Livreur livreur = new Livreur();
        livreur.setId(dto.getId());
        livreur.setNom(dto.getNom());
        livreur.setEmail(dto.getEmail());
        livreur.setTelephone(dto.getTelephone());
        livreur.setUserId(dto.getUserId());
        livreur.setPhoto(dto.getPhoto());
        return livreur;
    }
}