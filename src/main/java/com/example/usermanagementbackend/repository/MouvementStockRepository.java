package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.MouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Integer> {
}
