package com.example.usermanagementbackend.entity;

public enum TypeLivraison {
    // Original delivery types
    POINT_RELAIS,
    A_DOMICILE,
    
    // Vehicle types for carbon footprint calculation
    VELO,       // Bicycle - zero emission
    MOTO,       // Motorcycle
    VOITURE,    // Car
    CAMION      // Truck/van
}
