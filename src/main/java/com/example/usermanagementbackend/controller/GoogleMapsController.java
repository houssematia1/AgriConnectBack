package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.service.GoogleMapsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maps")
public class GoogleMapsController {

        @Autowired
        private GoogleMapsService googleMapsService;

        // Endpoint pour obtenir la distance
        @GetMapping("/distance")
        public String getDistance(@RequestParam String origin, @RequestParam String destination) {
            return googleMapsService.getDistance(origin, destination);
        }
}
