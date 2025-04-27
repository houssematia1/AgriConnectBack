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

    @Query("SELECT f FROM Fidelite f JOIN FETCH f.user u")
    List<Fidelite> findAllWithUser();

    @Query("SELECT f FROM Fidelite f JOIN FETCH f.user u WHERE f.id = :id")
    Optional<Fidelite> findByIdWithUser(@Param("id") Integer id);

    Optional<Fidelite> findByUserId(Long userId);

    @Query("SELECT f FROM Fidelite f JOIN FETCH f.user u " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "LOWER(u.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Fidelite> findByUserSearch(@Param("search") String search);
}