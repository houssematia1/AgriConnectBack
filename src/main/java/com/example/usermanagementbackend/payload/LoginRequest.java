package com.example.usermanagementbackend.payload;

public class LoginRequest {
    private String email;
    private String motDePasse;

    // Getters et setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMotDePasse() {
        return motDePasse;
    }
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}
