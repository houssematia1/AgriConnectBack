package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Notification;
import com.example.usermanagementbackend.enums.TypeNotification;
import com.example.usermanagementbackend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public void sendNotification(Long destinataire, String message, TypeNotification type) {
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setMessage(message);
        notification.setType(type);
        notification.setDateEnvoi(new Date());
        notification.setLue(false);

        notificationRepository.save(notification);

        if (destinataire != null) {
            messagingTemplate.convertAndSend("/topic/notifications/" + destinataire, notification);
        } else {
            messagingTemplate.convertAndSend("/topic/notifications", notification);
        }
    }

    public void sendNotificationToAll(String message, TypeNotification type) {
        sendNotification(null, message, type);
    }
}