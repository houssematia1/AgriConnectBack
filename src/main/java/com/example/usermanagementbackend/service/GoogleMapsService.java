package com.example.usermanagementbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleMapsService {

    // Clé API injectée depuis application.properties
    @Value("${google.maps.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    // Méthode pour obtenir la distance entre deux endroits
    public String getDistance(String origin, String destination) {
        // Construction de l'URL avec les paramètres
        String url = String.format("%s?origins=%s&destinations=%s&key=%s", BASE_URL, origin, destination, apiKey);

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Effectuer la requête HTTP GET
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            // Gérer les erreurs, par exemple, une clé API invalide
            return "Erreur lors de l'appel à l'API Google Maps : " + e.getMessage();
        }
    }
}
