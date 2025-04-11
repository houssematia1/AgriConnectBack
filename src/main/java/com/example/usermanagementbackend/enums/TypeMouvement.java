package com.example.usermanagementbackend.enums;

public enum TypeMouvement {
    ENTREE,  // Ajout de stock
    SORTIE,  // Vente ou retrait
    PERTE,   // Stock perdu (ex : périmé ou endommagé)
    DON      // Don aux associations
}
