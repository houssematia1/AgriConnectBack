package com.example.usermanagementbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeDTO {
    private Long id;
    private String clientNom;
    private String statut;
    private String address;
    private String telephone;
    private String statusColor;
    private LivreurDTO livreur;
    private Long livreurId; // New field

    public CommandeDTO(Long id, String clientNom, String statut, String address, String telephone) {
        this.id = id;
        this.clientNom = clientNom;
        this.statut = statut;
        this.address = address;
        this.telephone = telephone;
    }
}