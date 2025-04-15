package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.dto.LivraisonDTO;
import com.example.usermanagementbackend.entity.Livraison;
import com.example.usermanagementbackend.entity.StatusLivraison;
import com.example.usermanagementbackend.mapper.LivraisonMapper;
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

    @Override
    @Transactional
    public LivraisonDTO addLivraison(LivraisonDTO dto) {
        if (dto == null || dto.getLivreur() == null || dto.getTypeLivraison() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Livraison data");
        }
        
        Livraison livraison = LivraisonMapper.toEntity(dto);
        livreurRepository.findById(livraison.getLivreur().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livreur not found"));
                
        return LivraisonMapper.toDTO(livraisonRepository.save(livraison));
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
                
        Livraison updated = LivraisonMapper.toEntity(dto);
        updated.setId(id);
        
        if (existing.getStatusLivraison() == StatusLivraison.LIVRE && updated.getStatusLivraison() != StatusLivraison.LIVRE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change status from LIVRE");
        }
        
        livreurRepository.findById(updated.getLivreur().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livreur not found"));
                
        return LivraisonMapper.toDTO(livraisonRepository.save(updated));
    }

    @Override
    @Transactional
    public void deleteLivraison(Long id) {
        if (!livraisonRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison not found");
        }
        livraisonRepository.deleteById(id);
    }
}