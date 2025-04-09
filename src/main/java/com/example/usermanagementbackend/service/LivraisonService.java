package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.dto.LivraisonDTO;
import com.example.usermanagementbackend.entity.Livraison;
import com.example.usermanagementbackend.mapper.LivraisonMapper;
import com.example.usermanagementbackend.repository.LivraisonRepository;
import com.example.usermanagementbackend.repository.LivreurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LivraisonService implements ILivraisonService {

    @Autowired
    private LivraisonRepository livraisonRepository;

    @Autowired
    private LivreurRepository livreurRepository;

    @Override
    public LivraisonDTO addLivraison(LivraisonDTO livraisonDTO) {
        Livraison livraison = LivraisonMapper.toEntity(livraisonDTO);
        // Ensure Livreur exists in DB
        livreurRepository.findById(livraison.getLivreur().getId())
                .orElseThrow(() -> new RuntimeException("Livreur not found"));
        Livraison savedLivraison = livraisonRepository.save(livraison);
        return LivraisonMapper.toDTO(savedLivraison);
    }

    @Override
    public LivraisonDTO getLivraisonById(Long id) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison not found"));
        return LivraisonMapper.toDTO(livraison);
    }

    @Override
    public List<LivraisonDTO> getAllLivraisons() {
        return livraisonRepository.findAll().stream()
                .map(LivraisonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LivraisonDTO updateLivraison(Long id, LivraisonDTO livraisonDTO) {
        Livraison existingLivraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison not found"));
        Livraison updatedLivraison = LivraisonMapper.toEntity(livraisonDTO);
        updatedLivraison.setId(id); // Preserve the ID
        // Ensure Livreur exists
        livreurRepository.findById(updatedLivraison.getLivreur().getId())
                .orElseThrow(() -> new RuntimeException("Livreur not found"));
        Livraison savedLivraison = livraisonRepository.save(updatedLivraison);
        return LivraisonMapper.toDTO(savedLivraison);
    }

    @Override
    public void deleteLivraison(Long id) {
        if (!livraisonRepository.existsById(id)) {
            throw new RuntimeException("Livraison not found");
        }
        livraisonRepository.deleteById(id);
    }
}