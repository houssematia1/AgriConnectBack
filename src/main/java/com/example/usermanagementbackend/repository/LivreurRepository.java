package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur, Long> {
    Optional<Livreur> findByUserId(Long userId);
}
