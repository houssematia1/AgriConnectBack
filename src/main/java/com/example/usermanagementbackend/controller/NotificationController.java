
package com.example.usermanagementbackend.controller;


import com.example.usermanagementbackend.entity.Notification;
import com.example.usermanagementbackend.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // Injection de la dépendance
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Récupérer une notification par ID
    @GetMapping("/{id}")
    public Notification getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id);
    }

    // Envoyer une notification
    @PostMapping("/envoyer")
    public String envoyerNotification(@RequestBody Notification notification) {
        // Here we extract the required data from the Notification object
        notificationService.sendNotification(
                notification.getDestinataire(),
                notification.getMessage(),
                notification.getType()
        );
        return "Notification envoyée avec succès!";
    }
}
