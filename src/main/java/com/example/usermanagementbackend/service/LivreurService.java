package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.dto.LivreurDTO;
import com.example.usermanagementbackend.entity.Livreur;
import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.mapper.LivreurMapper;
import com.example.usermanagementbackend.repository.LivreurRepository;
import com.example.usermanagementbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LivreurService {

    @Autowired
    private LivreurRepository livreurRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${upload.directory}")
    private String uploadDir;

    public List<LivreurDTO> getAllLivreurs() {
        return livreurRepository.findAll().stream()
                .map(LivreurMapper::toDTO)
                .collect(Collectors.toList());
    }

    public LivreurDTO findByUserId(Long userId) {
        Livreur livreur = livreurRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Livreur not found for userId: " + userId
                ));
        return LivreurMapper.toDTO(livreur);
    }

    public String uploadPhoto(Long livreurId, MultipartFile file) throws IOException {
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Livreur not found with id: " + livreurId
                ));

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Delete old photo if exists
        if (livreur.getPhoto() != null) {
            Path oldFilePath = uploadPath.resolve(livreur.getPhoto());
            Files.deleteIfExists(oldFilePath);
        }

        livreur.setPhoto(fileName);
        livreurRepository.save(livreur);

        return "/uploads/" + fileName;
    }

    public ResponseEntity<?> getPhoto(Long id) {
        return livreurRepository.findById(id).map(livreur -> {
            String filename = livreur.getPhoto();
            if (filename == null) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(uploadDir, filename);
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            try {
                byte[] content = Files.readAllBytes(path);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(content);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    private MediaType getMediaType(String filename) {
        if (filename.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public LivreurDTO createLivreurForUser(Long userId) {
        try {
            // Find the user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found with id: " + userId
                    ));

            // Check if a livreur already exists for this user
            Optional<Livreur> existingLivreur = livreurRepository.findByUserId(userId);
            if (existingLivreur.isPresent()) {
                return LivreurMapper.toDTO(existingLivreur.get());
            }

            // Create a new livreur
            Livreur livreur = new Livreur();
            livreur.setNom(user.getNom() + " " + user.getPrenom());
            livreur.setEmail(user.getEmail());
            livreur.setTelephone(user.getNumeroDeTelephone() != null ? user.getNumeroDeTelephone() : "");
            livreur.setUserId(userId); // Set the userId field

            // Log the creation
            System.out.println("Creating new Livreur: " + livreur.getNom() + ", email: " + livreur.getEmail() + ", userId: " + livreur.getUserId());

            // Save the livreur
            Livreur savedLivreur = livreurRepository.save(livreur);
            System.out.println("Saved Livreur with ID: " + savedLivreur.getId() + ", userId: " + savedLivreur.getUserId());

            return LivreurMapper.toDTO(savedLivreur);
        } catch (Exception e) {
            // If there's any error with the user, create a default livreur
            System.err.println("Error finding user, creating default livreur: " + e.getMessage());
            return createDefaultLivreurForUserId(userId);
        }
    }

    /**
     * Creates a default Livreur for a user ID without requiring the User entity
     * This is a fallback method in case there are issues with the User-Livreur relationship
     */
    public LivreurDTO createDefaultLivreurForUserId(Long userId) {
        System.out.println("=== CREATING DEFAULT LIVREUR ====");
        System.out.println("Input userId: " + userId);

        // Check if a livreur already exists for this user
        Optional<Livreur> existingLivreur = livreurRepository.findByUserId(userId);
        if (existingLivreur.isPresent()) {
            Livreur existing = existingLivreur.get();
            System.out.println("Found existing livreur with ID: " + existing.getId() + ", userId: " + existing.getUserId());
            return LivreurMapper.toDTO(existing);
        }

        // Create a new default livreur
        Livreur livreur = new Livreur();
        livreur.setNom("Delivery Person " + userId);
        livreur.setEmail("delivery" + userId + "@example.com");
        livreur.setTelephone("0000000000");
        livreur.setUserId(userId); // Set the userId field

        // Log the creation
        System.out.println("Creating new Livreur with userId: " + livreur.getUserId());
        System.out.println("Livreur before save: " + livreur);

        // Save the livreur
        Livreur savedLivreur = livreurRepository.save(livreur);
        System.out.println("Livreur after save: " + savedLivreur);
        System.out.println("Saved Livreur with ID: " + savedLivreur.getId() + ", userId: " + savedLivreur.getUserId());

        // Create DTO and verify userId is set
        LivreurDTO dto = LivreurMapper.toDTO(savedLivreur);
        System.out.println("Created DTO: " + dto);
        System.out.println("DTO userId: " + dto.getUserId());

        return dto;
    }
}

