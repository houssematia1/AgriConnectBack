package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.dto.LivreurDTO;
import com.example.usermanagementbackend.service.LivreurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileName = livreurService.uploadPhoto(id, file);
            return ResponseEntity.ok("Photo uploaded successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
    @GetMapping("/{id}/photo")
    public ResponseEntity<?> getPhoto(@PathVariable Long id) {
        return livreurService.getPhoto(id);
    }


}