package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Notification;
import com.example.usermanagementbackend.enums.TypeNotification;
import com.example.usermanagementbackend.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        try {
            String jsonPayload = objectMapper.writeValueAsString(notification);
            System.out.println("Sending notification to /topic/notifications/clients: " + jsonPayload);
        } catch (Exception e) {
            System.out.println("Error serializing notification: " + e.getMessage());
        }

        if (type == TypeNotification.NOUVEAU_PRODUIT) {
            messagingTemplate.convertAndSend("/topic/notifications/clients", notification);
        } else if (destinataire != null) {
            messagingTemplate.convertAndSend("/topic/notifications/" + destinataire, notification);
        } else {
            messagingTemplate.convertAndSend("/topic/notifications", notification);
        }
    }

    public void sendNotificationToAllClients(String message, TypeNotification type) {
        sendNotification(null, message, type);
    }

    public void sendNotificationToAll(String message, TypeNotification type) {
        sendNotification(null, message, type);
    }
}