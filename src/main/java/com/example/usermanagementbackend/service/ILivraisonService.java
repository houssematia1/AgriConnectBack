package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.dto.LivraisonDTO;
import java.util.List;

public interface ILivraisonService {
    LivraisonDTO addLivraison(LivraisonDTO livraisonDTO);
    LivraisonDTO getLivraisonById(Long id);
    List<LivraisonDTO> getAllLivraisons();
    LivraisonDTO updateLivraison(Long id, LivraisonDTO livraisonDTO);
    void deleteLivraison(Long id);

}