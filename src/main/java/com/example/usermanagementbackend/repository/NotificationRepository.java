package com.example.usermanagementbackend.repository;


import com.example.usermanagementbackend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataire(Long destinataire);
}
