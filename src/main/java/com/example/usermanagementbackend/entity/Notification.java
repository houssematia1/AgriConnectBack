package com.example.usermanagementbackend.entity;


import com.example.usermanagementbackend.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "NOTIFICATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEnvoi;

    private boolean lue; // Pour savoir si la notification a été consultée

    @Enumerated(EnumType.STRING)
    private TypeNotification type;
    private Long destinataire; // ID de l'agriculteur ou du client concerné
}