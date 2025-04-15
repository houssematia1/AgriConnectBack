package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Commande;
import com.example.usermanagementbackend.entity.TransactionPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionPaiementRepository extends JpaRepository<TransactionPaiement, Long> {
    List<TransactionPaiement> findByCommande(Commande commande);

    List<TransactionPaiement> findByCommandeId(Long id);
}
