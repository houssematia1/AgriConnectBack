package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.dto.LivreurDTO;
import com.example.usermanagementbackend.entity.Livreur;
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
import java.util.stream.Collectors;

@Service
public class LivreurService {

    @Autowired
    private static LivreurRepository livreurRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${upload.directory}")
    private String uploadDir;

    public List<LivreurDTO> getAllLivreurs() {
        return livreurRepository.findAll().stream()
                .map(LivreurMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static LivreurDTO findByUserId(Long userId) {
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

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        livreur.setPhoto(fileName);
        livreurRepository.save(livreur);

        return fileName;
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
                        .contentType(getMediaType(filename))
                        .body(content);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error reading file: " + e.getMessage());
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
}