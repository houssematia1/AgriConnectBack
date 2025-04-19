package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.dto.LivraisonDTO;
import com.example.usermanagementbackend.entity.Commande;
import com.example.usermanagementbackend.entity.Livraison;
import com.example.usermanagementbackend.entity.StatusLivraison;
import com.example.usermanagementbackend.mapper.LivraisonMapper;
import com.example.usermanagementbackend.repository.CommandeRepository;
import com.example.usermanagementbackend.repository.LivraisonRepository;
import com.example.usermanagementbackend.repository.LivreurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivraisonService implements ILivraisonService {
    private final LivraisonRepository livraisonRepository;
    private final LivreurRepository livreurRepository;
    private final CommandeRepository commandeRepository;

    @Override
    @Transactional
    public LivraisonDTO addLivraison(LivraisonDTO dto) {
        if (dto == null || dto.getLivreur() == null || dto.getTypeLivraison() == null || dto.getCommandeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Livraison data");
        }

        // Validate commandeId exists and fetch the Commande
        Commande commande = commandeRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande not found with id: " + dto.getCommandeId()));

        Livraison livraison = LivraisonMapper.toEntity(dto);
        if (livraison.getStatusLivraison() == null) {
            livraison.setStatusLivraison(StatusLivraison.TAKE_IT);
        }

        // Validate livreur exists
        livreurRepository.findById(livraison.getLivreur().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livreur not found"));

        // Update Commande status and livreur_id
        if (livraison.getStatusLivraison() == StatusLivraison.EN_COURS) {
            commande.setStatut("EN_COURS");
            commande.setLivreurId(livraison.getLivreur().getId()); // Set livreur_id
            commandeRepository.save(commande);
        }

        Livraison savedLivraison = livraisonRepository.save(livraison);
        return LivraisonMapper.toDTO(savedLivraison);
    }

    @Override
    public LivraisonDTO getLivraisonById(Long id) {
        return livraisonRepository.findById(id)
                .map(LivraisonMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison not found"));
    }

    @Override
    public List<LivraisonDTO> getAllLivraisons() {
        return livraisonRepository.findAll().stream()
                .map(LivraisonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LivraisonDTO updateLivraison(Long id, LivraisonDTO dto) {
        Livraison existing = livraisonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison not found"));

        // Validate commandeId exists
        Commande commande = commandeRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande not found with id: " + dto.getCommandeId()));

        Livraison updated = LivraisonMapper.toEntity(dto);
        updated.setId(id);

        // Prevent changing status from LIVRE or NON_LIVRE
        if (existing.getStatusLivraison() == StatusLivraison.LIVRE && updated.getStatusLivraison() != StatusLivraison.LIVRE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change status from LIVRE");
        }
        if (existing.getStatusLivraison() == StatusLivraison.NON_LIVRE && updated.getStatusLivraison() != StatusLivraison.NON_LIVRE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change status from NON_LIVRE");
        }

        // Validate livreur exists
        livreurRepository.findById(updated.getLivreur().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livreur not found"));

        // Update Commande status and livreur_id
        if (updated.getStatusLivraison() == StatusLivraison.LIVRE) {
            commande.setStatut("LIVRE");
            commande.setLivreurId(null); // Clear livreur_id when delivered
        } else if (updated.getStatusLivraison() == StatusLivraison.NON_LIVRE) {
            commande.setStatut("NON_LIVRE");
            commande.setLivreurId(null); // Clear livreur_id when not delivered
        } else if (updated.getStatusLivraison() == StatusLivraison.EN_COURS) {
            commande.setStatut("EN_COURS");
            commande.setLivreurId(updated.getLivreur().getId()); // Set livreur_id
        }
        commandeRepository.save(commande);

        Livraison savedLivraison = livraisonRepository.save(updated);
        return LivraisonMapper.toDTO(savedLivraison);
    }

    @Override
    @Transactional
    public void deleteLivraison(Long id) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison not found"));
        Commande commande = commandeRepository.findById(livraison.getCommandeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande not found"));
        commande.setStatut("PENDING");
        commande.setLivreurId(null); // Clear livreur_id when deleting livraison
        commandeRepository.save(commande);
        livraisonRepository.deleteById(id);
    }
}