package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LivraisonRepository extends JpaRepository<Livraison, Long> {


}