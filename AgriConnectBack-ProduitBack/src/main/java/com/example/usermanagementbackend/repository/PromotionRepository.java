package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    List<Promotion> findByActiveTrue();
    Optional<Promotion> findByNom(String nom);
    Optional<Promotion> findByConditionPromotionAndActiveTrue(String condition);
    List<Promotion> findByConditionPromotionInAndActiveTrue(List<String> conditionPromotions);
}