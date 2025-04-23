package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Notification;
import com.example.usermanagementbackend.enums.TypeNotification;
import com.example.usermanagementbackend.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*") // à adapter si besoin
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Récupérer une notification par son ID
    @GetMapping("/{id}")
    public Notification getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id);
    }

    // Envoyer une notification à un destinataire spécifique
    @PostMapping("/envoyer")
    public String envoyerNotification(@RequestBody Notification notification) {
        notificationService.sendNotification(
                notification.getDestinataire(),
                notification.getMessage(),
                notification.getType()
        );
        return "Notification envoyée avec succès !";
    }

    // Envoyer une notification à tous les clients (type général, exemple : NOUVEAU_PRODUIT)
    @PostMapping("/broadcast")
    public String broadcastNotification(@RequestParam String message,
                                        @RequestParam TypeNotification type) {
        notificationService.sendNotificationToAllClients(message, type);
        return "Notification envoyée à tous les clients !";
    }

    // Envoyer une notification à tous (clients + autres rôles éventuels)
    @PostMapping("/broadcast-all")
    public String broadcastAll(@RequestParam String message,
                               @RequestParam TypeNotification type) {
        notificationService.sendNotificationToAll(message, type);
        return "Notification globale envoyée à tous !";
    }
}
