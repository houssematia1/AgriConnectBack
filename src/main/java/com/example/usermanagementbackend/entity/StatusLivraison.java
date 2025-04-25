package com.example.usermanagementbackend.entity;

public enum StatusLivraison {
    TAKE_IT,    // Initial status when a livraison is created
    EN_COURS,   // Delivery is in progress (taken by a livreur)
    LIVRE,      // Delivery is successfully completed
    NON_LIVRE   // Delivery failed
}