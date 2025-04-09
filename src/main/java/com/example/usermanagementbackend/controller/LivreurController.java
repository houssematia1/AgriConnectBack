package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.dto.LivreurDTO;
import com.example.usermanagementbackend.service.LivreurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livreurs")
@CrossOrigin(origins = "http://localhost:4200")
public class LivreurController {

    @Autowired
    private LivreurService livreurService;

    @GetMapping("/all")
    public ResponseEntity<List<LivreurDTO>> getAllLivreurs() {
        List<LivreurDTO> livreurs = livreurService.getAllLivreurs();
        return ResponseEntity.ok(livreurs);
    }

}