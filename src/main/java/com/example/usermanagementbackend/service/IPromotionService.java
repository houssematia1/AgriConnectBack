package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Promotion;

import java.util.List;
import java.util.Optional;

public interface IPromotionService {
    List<Promotion> getAllPromotions();
    Optional<Promotion> getPromotionById(Integer id);
    Promotion createPromotion(Promotion promotion);
    Promotion updatePromotion(Integer id, Promotion promotion);
    void deletePromotion(Integer id);
    void appliquerPromotionExpirationProduit();
    void bulkActivate(List<Integer> ids);
    void bulkDeactivate(List<Integer> ids);
    void bulkDelete(List<Integer> ids);
}