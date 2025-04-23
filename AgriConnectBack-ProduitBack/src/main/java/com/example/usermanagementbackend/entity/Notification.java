package com.example.usermanagementbackend.entity;

import com.example.usermanagementbackend.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long destinataire;

    private String message;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    private Date dateEnvoi;

    private Boolean lue;
}