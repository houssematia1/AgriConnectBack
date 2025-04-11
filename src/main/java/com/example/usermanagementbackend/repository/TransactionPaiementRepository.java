package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.TransactionPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionPaiementRepository extends JpaRepository<TransactionPaiement, Long> {

}
