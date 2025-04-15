package com.example.usermanagementbackend.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CommandeController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<List<CommandeDTO>> getAllCommandes() {
        try {
            List<CommandeDTO> commandes = jdbcTemplate.query(
                "SELECT id, client_nom, statut, address, telephone FROM commandes",
                (rs, rowNum) -> new CommandeDTO(
                    rs.getLong("id"),
                    rs.getString("client_nom"),
                    rs.getString("statut"),
                    rs.getString("address"),
                    rs.getString("telephone")
                )
            );
            return ResponseEntity.ok(commandes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching commandes", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> getCommandeById(@PathVariable Long id) {
        try {
            CommandeDTO commande = jdbcTemplate.queryForObject(
                "SELECT id, client_nom, statut, address, telephone FROM commandes WHERE id = ?",
                (rs, rowNum) -> new CommandeDTO(
                    rs.getLong("id"),
                    rs.getString("client_nom"),
                    rs.getString("statut"),
                    rs.getString("address"),
                    rs.getString("telephone")
                ),
                id
            );
            return ResponseEntity.ok(commande);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande not found with id: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<CommandeDTO> createCommande(@RequestBody CommandeDTO commandeDTO) {
        try {
            jdbcTemplate.update(
                "INSERT INTO commandes (client_nom, statut, address, telephone) VALUES (?, ?, ?, ?)",
                commandeDTO.getClientNom(),
                commandeDTO.getStatut(),
                commandeDTO.getAddress(),
                commandeDTO.getTelephone()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(commandeDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating commande", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandeDTO> updateCommande(@PathVariable Long id, @RequestBody CommandeDTO commandeDTO) {
        try {
            jdbcTemplate.update(
                "UPDATE commandes SET client_nom = ?, statut = ?, address = ?, telephone = ? WHERE id = ?",
                commandeDTO.getClientNom(),
                commandeDTO.getStatut(),
                commandeDTO.getAddress(),
                commandeDTO.getTelephone(),
                id
            );
            return ResponseEntity.ok(commandeDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating commande", e);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommandeDTO {
        private Long id;
        private String clientNom;
        private String statut;
        private String address;
        private String telephone;
    }
}