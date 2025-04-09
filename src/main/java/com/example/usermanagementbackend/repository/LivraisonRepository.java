package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
}
