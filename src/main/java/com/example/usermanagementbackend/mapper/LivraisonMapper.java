package com.example.usermanagementbackend.mapper;

import com.example.usermanagementbackend.dto.LivraisonDTO;
import com.example.usermanagementbackend.dto.LivreurDTO;
import com.example.usermanagementbackend.entity.Livraison;
import com.example.usermanagementbackend.entity.Livreur;
import com.example.usermanagementbackend.entity.User;

public class LivraisonMapper {

    public static Livraison toEntity(LivraisonDTO dto) {
        Livraison livraison = new Livraison();
        livraison.setId(dto.getId());
        livraison.setDateLivraison(dto.getDateLivraison());
        livraison.setStatusLivraison(dto.getStatusLivraison());
        livraison.setTypeLivraison(dto.getTypeLivraison());
        livraison.setPhoto(dto.getPhoto());
        livraison.setReason(dto.getReason());
        livraison.setCommandeId(dto.getCommandeId());
        livraison.setAddress(dto.getAddress()); // Map address
        livraison.setCarbonFootprint(dto.getCarbonFootprint());


        if (dto.getLivreur() != null) {
            Livreur livreur = new Livreur();
            livreur.setId(dto.getLivreur().getId());
            livreur.setNom(dto.getLivreur().getNom());
            livreur.setEmail(dto.getLivreur().getEmail());
            livreur.setTelephone(dto.getLivreur().getTelephone());
            if (dto.getLivreur().getUserId() != null) {
                User user = new User();
                user.setId(dto.getLivreur().getUserId());
                livreur.setUser(user);
            }
            livraison.setLivreur(livreur);
        }

        return livraison;
    }

    public static LivraisonDTO toDTO(Livraison livraison) {
        LivraisonDTO dto = new LivraisonDTO();
        dto.setId(livraison.getId());
        dto.setDateLivraison(livraison.getDateLivraison());
        dto.setStatusLivraison(livraison.getStatusLivraison());
        dto.setTypeLivraison(livraison.getTypeLivraison());
        dto.setPhoto(livraison.getPhoto());
        dto.setReason(livraison.getReason());
        dto.setCommandeId(livraison.getCommandeId());
        dto.setCreatedAt(livraison.getCreatedAt());
        dto.setUpdatedAt(livraison.getUpdatedAt());
        dto.setAddress(livraison.getAddress()); // Map address
        dto.setCarbonFootprint(livraison.getCarbonFootprint());



        if (livraison.getLivreur() != null) {
            LivreurDTO livreurDTO = new LivreurDTO();
            livreurDTO.setId(livraison.getLivreur().getId());
            livreurDTO.setNom(livraison.getLivreur().getNom());
            livreurDTO.setEmail(livraison.getLivreur().getEmail());
            livreurDTO.setTelephone(livraison.getLivreur().getTelephone());
            if (livraison.getLivreur().getUser() != null) {
                livreurDTO.setUserId(livraison.getLivreur().getUser().getId());
            }
            dto.setLivreur(livreurDTO);
        }

        return dto;
    }
}