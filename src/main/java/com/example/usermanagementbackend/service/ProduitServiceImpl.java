package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.TypeNotification;
import com.example.usermanagementbackend.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final NotificationService notificationService;

    @Override
    public Produit creer(Produit produit) {
        Produit savedProduit = produitRepository.save(produit);
        notificationService.sendNotification(null,
                " Nouveau produit disponible : " + produit.getNom(),
                TypeNotification.NOUVEAU_PRODUIT);
        return savedProduit;
    }

    @Override
    public List<Produit> lire() {
        return produitRepository.findAll();
    }

    @Override
    public Produit modifier(Integer id, Produit produit) {
        return produitRepository.findById(id)
                .map(p -> {
                    p.setPrix(produit.getPrix());
                    p.setNom(produit.getNom());
                    p.setDevise(produit.getDevise());
                    p.setFournisseur(produit.getFournisseur());
                    p.setTaxe(produit.getTaxe());
                    p.setDateExpiration(produit.getDateExpiration());
                    p.setImage(produit.getImage());
                    p.setStock(produit.getStock());
                    p.setSeuilMin(produit.getSeuilMin());
                    p.setAutoReapprovisionnement(produit.isAutoReapprovisionnement());
                    p.setQuantiteReapprovisionnement(produit.getQuantiteReapprovisionnement());
                    verifierStock(p);
                    return produitRepository.save(p);
                }).orElseThrow(() -> new RuntimeException("❌ Produit non trouvé"));
    }

    @Override
    public String supprimer(Integer id) {
        produitRepository.deleteById(id);
        return "✅ Produit supprimé";
    }

    @Override
    public Produit trouverParId(Integer id) {
        return produitRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Produit> lireProduitsPagine(int numeroPage, int taillePage, String triPar) {
        Sort.Direction directionTri = Sort.Direction.ASC;
        String proprieteTri = "id";
        if (triPar != null && !triPar.isEmpty()) {
            if (triPar.startsWith("-")) {
                directionTri = Sort.Direction.DESC;
                proprieteTri = triPar.substring(1);
            } else {
                proprieteTri = triPar;
            }
        }
        return produitRepository.findAll(PageRequest.of(numeroPage, taillePage, directionTri, proprieteTri));
    }

    @Override
    public Page<Produit> recherche(String recherche, String critere) {
        PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE);
        switch (critere.toLowerCase()) {
            case "nom":
                return produitRepository.findByNomContaining(recherche, pageable);
            case "fournisseur":
                return produitRepository.findByFournisseurContaining(recherche, pageable);
            case "prix":
                String[] prixRange = recherche.split("-");
                if (prixRange.length != 2) {
                    throw new IllegalArgumentException("⚠️ Format de prix invalide. Utilisez 'min-max'");
                }
                double min = Double.parseDouble(prixRange[0]);
                double max = Double.parseDouble(prixRange[1]);
                return produitRepository.findByPrixBetween(min, max, pageable);
            default:
                throw new IllegalArgumentException("⚠️ Critère non supporté: " + critere);
        }
    }

    private void verifierStock(Produit produit) {
        if (produit.getStock() <= produit.getSeuilMin()) {
            if (produit.getFournisseurId() != null) {
                notificationService.sendNotification(produit.getFournisseurId(),
                        "⚠️ Le stock du produit " + produit.getNom() + " est bas ! Pensez à réapprovisionner.",
                        TypeNotification.STOCK_BAS);
            }
            notificationService.sendNotification(null,
                    "📢 Promo spéciale : Le produit " + produit.getNom() + " est bientôt en rupture de stock. Profitez-en !",
                    TypeNotification.PRODUIT_RARE);
        }
        if (produit.isAutoReapprovisionnement() && produit.getStock() <= produit.getSeuilMin()) {
            produit.setStock(produit.getStock() + produit.getQuantiteReapprovisionnement());
            produitRepository.save(produit);
            notificationService.sendNotification(produit.getFournisseurId(),
                    "🔄 Stock auto-réapprovisionné pour " + produit.getNom() + " (+"
                            + produit.getQuantiteReapprovisionnement() + " unités).",
                    TypeNotification.REAPPRO_AUTO);
        }
    }
}