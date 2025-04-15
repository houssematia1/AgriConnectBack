package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.entity.Facture;
import com.example.usermanagementbackend.service.FactureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/factures")
public class FactureController {

    private final FactureService factureService;

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @GetMapping
    public ResponseEntity<List<Facture>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        Optional<Facture> facture = factureService.getFactureById(id);
        return facture.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Facture> createFacture(@RequestBody Facture facture) {
        try {
            Facture savedFacture = factureService.saveFacture(facture);
            return new ResponseEntity<>(savedFacture, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Facture> updateFacture(@PathVariable Long id, @RequestBody Facture facture) {
        try {
            Facture updatedFacture = factureService.updateFacture(id, facture);
            return ResponseEntity.ok(updatedFacture);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        try {
            factureService.deleteFacture(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadFacturePDF(@PathVariable Long id) {
        Optional<Facture> facture = factureService.getFactureById(id);
        if (facture.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path pdfPath = Paths.get("invoice_" + id + ".pdf");
            if (!Files.exists(pdfPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("PDF not found for facture ID: ".getBytes());
            }
            byte[] pdfBytes = Files.readAllBytes(pdfPath);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + id + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error reading PDF: " + e.getMessage()).getBytes());
        }
    }
}