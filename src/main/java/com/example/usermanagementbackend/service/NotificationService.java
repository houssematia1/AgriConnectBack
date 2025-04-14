package com.example.usermanagementbackend.service;


import com.example.usermanagementbackend.entity.Notification;
import com.example.usermanagementbackend.enums.TypeNotification;
import com.example.usermanagementbackend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Récupérer une notification par ID
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    // Envoyer une notification
    public void sendNotification(Long destinataire, String message, TypeNotification type) {
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setMessage(message);
        notification.setType(type);
        notification.setDateEnvoi(new Date());
        notification.setLue(false);

        notificationRepository.save(notification);
    }

}

