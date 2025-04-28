package sample.evenement_crud.controller;

import sample.evenement_crud.entity.Evenement;
import sample.evenement_crud.service.EvenementService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/evenements")
public class EvenementController {
    private final EvenementService evenementService;

    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    // 🔹 CRUD standard
    @GetMapping
    public List<Evenement> getAllEvenements() {
        return evenementService.getAllEvenements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEvenementById(@PathVariable Long id) {
        return evenementService.getEvenementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Evenement createEvenement(@RequestBody Evenement evenement) {
        return evenementService.createEvenement(evenement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evenement> updateEvenement(@PathVariable Long id, @RequestBody Evenement evenement) {
        return ResponseEntity.ok(evenementService.updateEvenement(id, evenement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Upload d'image
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Fichier vide");
            }

            System.out.println("Upload reçu : " + file.getOriginalFilename());

            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("/api/evenements/image/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de l'upload : " + e.getMessage());
        }
    }


    // 🔹 Accès à une image
    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path file = Paths.get("uploads").resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Tu peux aussi détecter dynamiquement
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/{id}/resume")
    public ResponseEntity<Map<String, String>> resumeEvenement(@PathVariable Long id) {
        return evenementService.getEvenementById(id)
                .map(evenement -> {
                    try {
                        RestTemplate restTemplate = new RestTemplate();
                        String resumeApiUrl = "http://localhost:8000/api/resume";

                        Map<String, String> request = new HashMap<>();
                        request.put("text", evenement.getDescription());

                        Map<String, String> response = restTemplate.postForObject(resumeApiUrl, request, Map.class);

                        Map<String, String> result = new HashMap<>();
                        result.put("summary", response.get("summary")); // toujours renvoyer un JSON

                        return ResponseEntity.ok(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(500).body(Map.of("error", "Erreur lors de la génération du résumé"));
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
