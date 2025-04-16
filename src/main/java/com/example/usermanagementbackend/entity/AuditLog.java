package com.example.usermanagementbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;         // Exemple : "bloqué", "supprimé"
    private String performedBy;    // L'admin qui a fait l'action
    private String targetUser;     // Utilisateur concerné

    private LocalDateTime dateAction;

    public AuditLog() {}

    public AuditLog(String action, String performedBy, String targetUser, LocalDateTime dateAction) {
        this.action = action;
        this.performedBy = performedBy;
        this.targetUser = targetUser;
        this.dateAction = dateAction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }
}
