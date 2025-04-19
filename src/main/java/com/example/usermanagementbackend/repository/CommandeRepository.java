package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
}