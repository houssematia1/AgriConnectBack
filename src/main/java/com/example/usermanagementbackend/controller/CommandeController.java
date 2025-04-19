package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.dto.CommandeDTO;
import com.example.usermanagementbackend.dto.LivreurDTO;
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
                    "SELECT c.id, c.client_nom, c.statut, c.address, c.telephone, c.livreur_id, " +
                            "lvr.nom as livreur_nom, lvr.email as livreur_email, lvr.telephone as livreur_telephone, lvr.user_id as livreur_user_id " +
                            "FROM commandes c " +
                            "LEFT JOIN livreurs lvr ON c.livreur_id = lvr.id",
                    (rs, rowNum) -> {
                        CommandeDTO cmd = new CommandeDTO(
                                rs.getLong("id"),
                                rs.getString("client_nom"),
                                rs.getString("statut"),
                                rs.getString("address"),
                                rs.getString("telephone")
                        );
                        cmd.setLivreurId(rs.getLong("livreur_id"));
                        if (!rs.wasNull()) {
                            LivreurDTO livreur = new LivreurDTO();
                            livreur.setId(rs.getLong("livreur_id"));
                            livreur.setNom(rs.getString("livreur_nom"));
                            livreur.setEmail(rs.getString("livreur_email"));
                            livreur.setTelephone(rs.getString("livreur_telephone"));
                            livreur.setUserId(rs.getLong("livreur_user_id"));
                            cmd.setLivreur(livreur);
                        }
                        return cmd;
                    }
            );
            return ResponseEntity.ok(commandes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching commandes", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> getCommandeById(@PathVariable Long id) {
        try {
            CommandeDTO cmd = jdbcTemplate.queryForObject(
                    "SELECT c.id, c.client_nom, c.statut, c.address, c.telephone, c.livreur_id, " +
                            "lvr.nom as livreur_nom, lvr.email as livreur_email, lvr.telephone as livreur_telephone, lvr.user_id as livreur_user_id " +
                            "FROM commandes c " +
                            "LEFT JOIN livreurs lvr ON c.livreur_id = lvr.id " +
                            "WHERE c.id = ?",
                    (rs, rowNum) -> {
                        CommandeDTO commandeDTO = new CommandeDTO(
                                rs.getLong("id"),
                                rs.getString("client_nom"),
                                rs.getString("statut"),
                                rs.getString("address"),
                                rs.getString("telephone")
                        );
                        commandeDTO.setLivreurId(rs.getLong("livreur_id"));
                        if (!rs.wasNull()) {
                            LivreurDTO livreur = new LivreurDTO();
                            livreur.setId(rs.getLong("livreur_id"));
                            livreur.setNom(rs.getString("livreur_nom"));
                            livreur.setEmail(rs.getString("livreur_email"));
                            livreur.setTelephone(rs.getString("livreur_telephone"));
                            livreur.setUserId(rs.getLong("livreur_user_id"));
                            commandeDTO.setLivreur(livreur);
                        }
                        return commandeDTO;
                    },
                    id
            );
            return ResponseEntity.ok(cmd);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande not found with id: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<CommandeDTO> createCommande(@RequestBody CommandeDTO commandeDTO) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO commandes (client_nom, statut, address, telephone, livreur_id) VALUES (?, ?, ?, ?, ?)",
                    commandeDTO.getClientNom(),
                    commandeDTO.getStatut() != null ? commandeDTO.getStatut() : "PENDING",
                    commandeDTO.getAddress(),
                    commandeDTO.getTelephone(),
                    commandeDTO.getLivreurId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(commandeDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating commande", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandeDTO> updateCommande(@PathVariable Long id, @RequestBody CommandeDTO commandeDTO) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE commandes SET client_nom = ?, statut = ?, address = ?, telephone = ?, livreur_id = ? WHERE id = ?",
                    commandeDTO.getClientNom(),
                    commandeDTO.getStatut(),
                    commandeDTO.getAddress(),
                    commandeDTO.getTelephone(),
                    commandeDTO.getLivreurId(),
                    id
            );
            if (rowsAffected == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande not found with id: " + id);
            }
            return ResponseEntity.ok(commandeDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating commande", e);
        }
    }
}