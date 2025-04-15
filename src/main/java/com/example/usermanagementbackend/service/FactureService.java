package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Facture;
import com.example.usermanagementbackend.entity.LigneFacture;
import com.example.usermanagementbackend.repository.CommandeRepository;
import com.example.usermanagementbackend.repository.FactureRepository;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FactureService {
    private final FactureRepository factureRepository;
    private final CommandeRepository commandeRepository;
    private final LigneFactureService ligneFactureService;

    public FactureService(FactureRepository factureRepository, CommandeRepository commandeRepository, LigneFactureService ligneFactureService) {
        this.factureRepository = factureRepository;
        this.commandeRepository = commandeRepository;
        this.ligneFactureService = ligneFactureService;
    }

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    public Optional<Facture> getFactureById(Long id) {
        return factureRepository.findById(id);
    }

    public Facture saveFacture(Facture facture) {
        // Validation
        if (facture.getCommande() == null) {
            throw new IllegalArgumentException("Commande must not be null");
        }
        if (commandeRepository.findById(facture.getCommande().getId()).isEmpty()) {
            throw new IllegalArgumentException("Commande not found with ID: " + facture.getCommande().getId());
        }
        if (facture.getMontantTotal() == null || facture.getMontantTotal() <= 0) {
            throw new IllegalArgumentException("Le montant total de la facture doit être supérieur à 0");
        }

        // Set invoice date and numeroFacture if not provided
        if (facture.getDateFacture() == null) {
            facture.setDateFacture(LocalDate.now());
        }
        if (facture.getNumeroFacture() == null) {
            facture.setNumeroFacture("FACT-" + System.currentTimeMillis());
        }

        // Save the facture first
        Facture savedFacture = factureRepository.save(facture);

        // Save associated lignesFacture
        if (facture.getLignesFacture() != null) {
            for (LigneFacture ligne : facture.getLignesFacture()) {
                ligne.setFacture(savedFacture);
                ligneFactureService.saveLigneFacture(ligne);
            }
        }

        // Generate PDF for the invoice
        generateInvoicePDF(savedFacture);

        return savedFacture;
    }

    public Facture updateFacture(Long id, Facture updatedFacture) {
        Facture existing = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID: " + id));

        existing.setMontantTotal(updatedFacture.getMontantTotal());
        existing.setDateFacture(updatedFacture.getDateFacture());
        existing.setNumeroFacture(updatedFacture.getNumeroFacture());
        return factureRepository.save(existing);
    }

    public void deleteFacture(Long id) {
        if (!factureRepository.existsById(id)) {
            throw new RuntimeException("Facture non trouvée avec l'ID: " + id);
        }
        factureRepository.deleteById(id);
    }

    private void generateInvoicePDF(Facture facture) {
        try {
            // Validate facture
            if (facture == null) {
                throw new IllegalArgumentException("Facture cannot be null");
            }
            if (facture.getCommande() == null) {
                throw new IllegalArgumentException("Commande associated with Facture cannot be null");
            }

            // Create PDF document
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter("invoice_" + facture.getId() + ".pdf"));
            Document doc = new Document(pdfDoc);

            // Add invoice header details
            doc.add(new Paragraph("Invoice #" + (facture.getNumeroFacture() != null ? facture.getNumeroFacture() : "N/A")));
            doc.add(new Paragraph("Date: " + (facture.getDateFacture() != null ? facture.getDateFacture().toString() : "N/A")));
            doc.add(new Paragraph("Order ID: " + facture.getCommande().getId()));
            doc.add(new Paragraph("Client: " + (facture.getCommande().getClientNom() != null ? facture.getCommande().getClientNom() : "N/A")));
            doc.add(new Paragraph(""));

            // Add invoice lines in a table
            List<LigneFacture> lignesFacture = ligneFactureService.getLignesFactureByFactureId(facture.getId());
            if (!lignesFacture.isEmpty()) {
                Table table = new Table(new float[]{2, 2, 1, 1, 1, 1});
                table.addHeaderCell(new Cell().add(new Paragraph("Produit")));
                table.addHeaderCell(new Cell().add(new Paragraph("Description")));
                table.addHeaderCell(new Cell().add(new Paragraph("Quantité")));
                table.addHeaderCell(new Cell().add(new Paragraph("Prix Unitaire")));
                table.addHeaderCell(new Cell().add(new Paragraph("Total HT")));
                table.addHeaderCell(new Cell().add(new Paragraph("TTC")));

                for (LigneFacture ligne : lignesFacture) {
                    table.addCell(new Cell().add(new Paragraph(ligne.getProduit() != null && ligne.getProduit().getNom() != null ? ligne.getProduit().getNom() : "N/A")));
                    table.addCell(new Cell().add(new Paragraph(ligne.getProduit() != null && ligne.getProduit().getDescription() != null ? ligne.getProduit().getDescription() : "N/A")));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(ligne.getQte()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(ligne.getPrixUnitaire()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(ligne.getTotal() != null ? ligne.getTotal() : 0.0))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(ligne.getTtc() != null ? ligne.getTtc() : 0.0))));
                }

                doc.add(table);
                doc.add(new Paragraph(""));
            }

            // Add total amount
            doc.add(new Paragraph("Total Amount: $" + (facture.getMontantTotal() != null ? facture.getMontantTotal() : "0.0")));

            // Close the document
            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF pour la facture #" +
                    (facture != null && facture.getId() != null ? facture.getId() : "unknown") + ": " + e.getMessage(), e);
        }
    }
}