package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Fidelite;
import com.example.usermanagementbackend.entity.PointHistory;

import java.util.List;
import java.util.Optional;

public interface IFideliteService {
    List<Fidelite> getAllFidelites(String search);
    Fidelite getFideliteById(Integer id);
    Fidelite saveFidelite(Fidelite fidelite);
    void deleteFidelite(Integer id);
    void ajouterPoints(Long userId, int points);
    Fidelite addPointsForPurchase(Long userId, double purchaseAmount);
    Fidelite addBirthdayPoints(Long userId);
    List<PointHistory> getPointHistory(Long userId);
    Optional<Fidelite> getFideliteByUserId(Long userId);
}