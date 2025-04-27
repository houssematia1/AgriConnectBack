package com.example.usermanagementbackend.repository;


import com.example.usermanagementbackend.entity.PromotionUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Integer> {
}