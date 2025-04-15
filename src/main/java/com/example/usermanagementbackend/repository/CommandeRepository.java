package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByStatus(Commande.OrderStatus status);
    List<Commande> findByDateCreationBetween(LocalDate startDate, LocalDate endDate);
    List<Commande> findByClientId(Long clientId);
}