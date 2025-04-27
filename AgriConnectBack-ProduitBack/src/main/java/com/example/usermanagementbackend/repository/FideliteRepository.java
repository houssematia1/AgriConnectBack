package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.Fidelite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FideliteRepository extends JpaRepository<Fidelite, Integer> {
    Optional<Fidelite> findByUserId(Long userId);

    @Query("SELECT f FROM Fidelite f JOIN FETCH f.user WHERE f.id = :id")
    Optional<Fidelite> findByIdWithUser(@Param("id") Integer id);
    @Query("SELECT f FROM Fidelite f JOIN FETCH f.user")
    List<Fidelite> findAllWithUser();

}