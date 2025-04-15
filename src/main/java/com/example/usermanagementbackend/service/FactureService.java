package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Commande;
import com.example.usermanagementbackend.entity.Facture;
import com.example.usermanagementbackend.entity.LigneFacture;
import com.example.usermanagementbackend.entity.TransactionPaiement;
import com.example.usermanagementbackend.repository.CommandeRepository;
import com.example.usermanagementbackend.repository.FactureRepository;
import com.example.usermanagementbackend.repository.TransactionPaiementRepository;
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
    private final TransactionPaiementRepository transactionPaiementRepository;

    public FactureService(FactureRepository factureRepository, CommandeRepository commandeRepository,
                          LigneFactureService ligneFactureService, TransactionPaiementRepository transactionPaiementRepository) {
        this.factureRepository = factureRepository;
        this.commandeRepository = commandeRepository;
        this.ligneFactureService = ligneFactureService;
        this.transactionPaiementRepository = transactionPaiementRepository;
    }

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    public Optional<Facture> getFactureById(Long id) {
        return factureRepository.findById(id);
    }

    public Facture saveFacture(Facture facture) {
        if (facture.getCommande() == null) {
            throw new IllegalArgumentException("Commande doit être spécifiée");
        }
        if (commandeRepository.findById(facture.getCommande().getId()).isEmpty()) {
            throw new IllegalArgumentException("Commande non trouvée avec l'ID: " + facture.getCommande().getId());
        }
        if (facture.getMontantTotal() == null || facture.getMontantTotal() <= 0) {
            throw new IllegalArgumentException("Le montant total de la facture doit être supérieur à 0");
        }

        if (facture.getClient() == null && facture.getCommande().getClient() != null) {
            facture.setClient(facture.getCommande().getClient());
        }

        if (facture.getDateFacture() == null) {
            facture.setDateFacture(LocalDate.now());
        }
        if (facture.getNumeroFacture() == null) {
            facture.setNumeroFacture("FACT-" + System.currentTimeMillis());
        }

        Facture savedFacture = factureRepository.save(facture);

        if (facture.getLignesFacture() != null) {
            for (LigneFacture ligne : facture.getLignesFacture()) {
                ligne.setFacture(savedFacture);
                ligneFactureService.saveLigneFacture(ligne);
            }
        }

        generateInvoicePDF(savedFacture);
        return savedFacture;
    }

    public Facture updateFacture(Long id, Facture updatedFacture) {
        Facture existing = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID: " + id));

        existing.setMontantTotal(updatedFacture.getMontantTotal());
        existing.setDateFacture(updatedFacture.getDateFacture());
        existing.setNumeroFacture(updatedFacture.getNumeroFacture());
        existing.setClient(updatedFacture.getClient());

        Facture savedFacture = factureRepository.save(existing);
        generateInvoicePDF(savedFacture);
        return savedFacture;
    }

    public void deleteFacture(Long id) {
        if (!factureRepository.existsById(id)) {
            throw new RuntimeException("Facture non trouvée avec l'ID: " + id);
        }
        factureRepository.deleteById(id);
    }

    private void generateInvoicePDF(Facture facture) {
        try {
            if (facture == null || facture.getCommande() == null) {
                throw new IllegalArgumentException("Facture ou commande invalide");
            }

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter("invoices/invoice_" + facture.getId() + ".pdf"));
            Document doc = new Document(pdfDoc);

            // Client details
            String clientName = (facture.getClient() != null && facture.getClient().getName() != null)
                    ? facture.getClient().getName() : "N/A";
            doc.add(new Paragraph("Client: " + clientName));
            String clientEmail = (facture.getClient() != null && facture.getClient().getEmail() != null)
                    ? facture.getClient().getEmail() : "N/A";
            doc.add(new Paragraph("Email: " + clientEmail));
            String clientPhone = (facture.getClient() != null && facture.getClient().getPhone() != null)
                    ? facture.getClient().getPhone() : "N/A";
            doc.add(new Paragraph("Téléphone: " + clientPhone));
            doc.add(new Paragraph(""));

            // Delivery details from Commande
            Commande commande = facture.getCommande();
            doc.add(new Paragraph("Livraison:"));
            doc.add(new Paragraph("Téléphone: " + (commande.getTelephone() != null ? commande.getTelephone() : "N/A")));
            doc.add(new Paragraph("Gouvernorat: " + (commande.getGouvernement() != null ? commande.getGouvernement() : "N/A")));
            doc.add(new Paragraph("Adresse: " + (commande.getAdresse() != null ? commande.getAdresse() : "N/A")));
            doc.add(new Paragraph(""));

            // Invoice details
            doc.add(new Paragraph("Facture #" + (facture.getNumeroFacture() != null ? facture.getNumeroFacture() : "N/A")));
            doc.add(new Paragraph("Date: " + (facture.getDateFacture() != null ? facture.getDateFacture().toString() : "N/A")));
            doc.add(new Paragraph("Commande ID: " + commande.getId()));
            doc.add(new Paragraph("Statut: " + (commande.getStatus() != null ? commande.getStatus().toString() : "N/A")));
            doc.add(new Paragraph(""));

            // Payment status
            List<TransactionPaiement> transactions = transactionPaiementRepository.findByCommandeId(facture.getCommande().getId());
            String paymentStatus = transactions.isEmpty() ? "Non payé" : transactions.get(0).getPaymentStatus();
            doc.add(new Paragraph("Statut du paiement: " + paymentStatus));
            if (!transactions.isEmpty() && transactions.get(0).getPaymentGatewayReference() != null) {
                doc.add(new Paragraph("Référence de paiement: " + transactions.get(0).getPaymentGatewayReference()));
            }
            doc.add(new Paragraph(""));

            // Invoice lines
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

            doc.add(new Paragraph("Montant Total: TND " + (facture.getMontantTotal() != null ? facture.getMontantTotal() : "0.0")));
            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
    }
}