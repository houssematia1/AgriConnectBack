package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByFideliteId(Integer fideliteId);
    void deleteByFideliteId(Integer fideliteId);
}