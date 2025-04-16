package com.example.usermanagementbackend.repository;

import com.example.usermanagementbackend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
