package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.dto.LivraisonDTO;
import com.example.usermanagementbackend.entity.TypeLivraison;
import com.example.usermanagementbackend.service.ILivraisonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/livraisons")
@CrossOrigin(origins = "http://localhost:4200")
public class LivraisonController {

    private final ILivraisonService livraisonService;

    public LivraisonController(ILivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    @PostMapping("/create")
    public ResponseEntity<LivraisonDTO> createLivraison(@RequestBody LivraisonDTO livraisonDTO) {
        System.out.println("=== Creating Livraison ===");
        System.out.println("Livraison DTO: " + livraisonDTO);

        if (livraisonDTO == null) {
            System.out.println("Request rejected: livraisonDTO is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (livraisonDTO.getLivreur() == null) {
            System.out.println("Request rejected: livreur is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (livraisonDTO.getLivreur().getEmail() == null) {
            System.out.println("Request rejected: livreur email is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (livraisonDTO.getCommandeId() == null) {
            System.out.println("Request rejected: commandeId is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        System.out.println("Request validated, creating livraison");
        LivraisonDTO createdLivraison = livraisonService.addLivraison(livraisonDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLivraison);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivraisonDTO> getLivraison(@PathVariable Long id) {
        LivraisonDTO livraisonDTO = livraisonService.getLivraisonById(id);
        return ResponseEntity.ok(livraisonDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<LivraisonDTO>> getAllLivraisons() {
        List<LivraisonDTO> livraisons = livraisonService.getAllLivraisons();
        return ResponseEntity.ok(livraisons);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivraisonDTO> updateLivraison(@PathVariable Long id, @RequestBody LivraisonDTO livraisonDTO) {
        LivraisonDTO updatedLivraison = livraisonService.updateLivraison(id, livraisonDTO);
        return ResponseEntity.ok(updatedLivraison);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteLivraison(@PathVariable Long id) {
        livraisonService.deleteLivraison(id);
        return ResponseEntity.ok(Map.of("message", "Livraison supprimée avec succès", "id", id.toString()));
    }
    
    /**
     * Calculate Haversine distance between two points in kilometers
     * 
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in kilometers
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        // Radius of the Earth in kilometers
        final double R = 6371.0;
        
        // Convert latitude and longitude from degrees to radians
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        // Haversine formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Calculate distance and round to 2 decimal places
        return Math.round(R * c * 100) / 100.0;
    }
    
    @PostMapping("/carbon-footprint")
    public ResponseEntity<Map<String, Object>> calculateCarbonFootprint(@RequestBody Map<String, Object> request) {
        try {
            String vehicleType = (String) request.get("vehicleType");
            String address = (String) request.get("address");
            Double distance = request.get("distance") != null ? Double.parseDouble(request.get("distance").toString()) : null;
            
            // Get GPS coordinates
            Double currentLat = request.get("currentLat") != null ? Double.parseDouble(request.get("currentLat").toString()) : null;
            Double currentLng = request.get("currentLng") != null ? Double.parseDouble(request.get("currentLng").toString()) : null;
            Double destinationLat = request.get("destinationLat") != null ? Double.parseDouble(request.get("destinationLat").toString()) : null;
            Double destinationLng = request.get("destinationLng") != null ? Double.parseDouble(request.get("destinationLng").toString()) : null;
            
            // Default values if not provided
            if (vehicleType == null) vehicleType = "VOITURE";
            if (address == null) address = "";
            
            // Calculate distance based on GPS coordinates if available
            if (currentLat != null && currentLng != null && destinationLat != null && destinationLng != null) {
                distance = calculateHaversineDistance(currentLat, currentLng, destinationLat, destinationLng);
                System.out.println("Calculated GPS distance: " + distance + " km");
            } else if (distance == null) {
                distance = 10.0; // Default 10km if no GPS coordinates or manual distance
            }
            
            // Convert string vehicle type to enum
            TypeLivraison typeLivraison = null;
            try {
                typeLivraison = TypeLivraison.valueOf(vehicleType);
            } catch (IllegalArgumentException e) {
                typeLivraison = TypeLivraison.VOITURE; // Default to car if invalid type
            }
            
            // Calculate carbon footprint using only car emission factor
            // Constant for carbon footprint calculation (kg CO2 per km)
            final double CAR_EMISSION_FACTOR = 0.2; // Only using car emission factor
            
            // Apply distance modifiers based on address keywords if custom distance not provided
            if (request.get("distance") == null && address != null) {
                String lowerCaseAddress = address.toLowerCase();
                if (lowerCaseAddress.contains("centre ville") || lowerCaseAddress.contains("downtown")) {
                    distance = 5.0; // Shorter distance for downtown deliveries
                } else if (lowerCaseAddress.contains("rural") || lowerCaseAddress.contains("campagne")) {
                    distance = 20.0; // Longer distance for rural deliveries
                }
            }
            
            // Only use car emission factor regardless of vehicle type
            double emissionFactor = CAR_EMISSION_FACTOR;
            
            // Calculate and round to 2 decimal places
            double carbonFootprint = Math.round(distance * emissionFactor * 100) / 100.0;
            
            return ResponseEntity.ok(Map.of(
                "carbonFootprint", carbonFootprint,
                "distance", distance,
                "emissionFactor", emissionFactor,
                "usedGPS", (currentLat != null && currentLng != null && destinationLat != null && destinationLng != null)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", 0.0));
        }
    }
}