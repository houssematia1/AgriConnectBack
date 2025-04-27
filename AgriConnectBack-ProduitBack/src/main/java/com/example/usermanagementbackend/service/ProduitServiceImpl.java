package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.entity.Purchase;
import com.example.usermanagementbackend.enums.Category;
import com.example.usermanagementbackend.repository.MouvementStockRepository;
import com.example.usermanagementbackend.repository.ProduitRepository;
import com.example.usermanagementbackend.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.usermanagementbackend.enums.TypeNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduitServiceImpl implements ProduitService {
    private final PurchaseRepository purchaseRepository;

    private final ProduitRepository produitRepository;
    private final StockService stockService;
    private final MouvementStockRepository mouvementStockRepository;
    private final NotificationService notificationService;
    @Override
    @Transactional
    public Produit creer(Produit produit) {
        try {
            System.out.println("Création du produit : " + produit);
            produit.setFournisseurId(getCurrentUserId());

            // Sauvegarder le produit
            Produit savedProduit = produitRepository.save(produit);
            produitRepository.flush();

            System.out.println("Produit créé avec succès : " + savedProduit);

            // Vérifier stock
            stockService.verifierEtReapprovisionner(savedProduit);

            // 🔔 Envoyer notification après création
            String message = "🆕 Nouveau produit créé : " + savedProduit.getNom();
            System.out.println("Envoi de la notification pour le nouveau produit : " + message);
            notificationService.sendNotification(null, message, TypeNotification.NOUVEAU_PRODUIT);

            return savedProduit;
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du produit : " + e.getMessage());
            throw new RuntimeException("Échec de la création du produit: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produit> lire() {
        try {
            List<Produit> produits = produitRepository.findAll();
            System.out.println("Produits récupérés : " + produits);
            return produits;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des produits : " + e.getMessage());
            throw new RuntimeException("Échec de la récupération des produits: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Produit modifier(Long id, Produit produit) {
        try {
            System.out.println("Modification du produit ID=" + id + " avec : " + produit);
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
                        p.setAutoReapprovisionnement(produit.isAutoReapprovisionnement());
                        p.setQuantiteReapprovisionnement(produit.getQuantiteReapprovisionnement());
                        p.setCategory(produit.getCategory());
                        Produit updatedProduit = produitRepository.save(p);
                        System.out.println("Produit modifié avec succès : " + updatedProduit);
                        produitRepository.flush();
                        stockService.verifierEtReapprovisionner(updatedProduit);
                        return updatedProduit;
                    }).orElseThrow(() -> new RuntimeException("❌ Produit non trouvé avec ID=" + id));
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification du produit ID=" + id + " : " + e.getMessage());
            throw new RuntimeException("Échec de la modification du produit: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public String supprimer(Long id) {
        try {
            Produit produit = produitRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("❌ Produit avec ID " + id + " non trouvé"));
            System.out.println("Suppression des mouvements de stock associés au produit ID=" + id);
            mouvementStockRepository.deleteByProduit(produit);
            System.out.println("Mouvements de stock supprimés avec succès");
            produitRepository.deleteById(id);
            System.out.println("Produit ID=" + id + " supprimé avec succès");
            return "✅ Produit supprimé";
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du produit ID=" + id + " : " + e.getMessage());
            throw new RuntimeException("Échec de la suppression du produit: " + e.getMessage(), e);
        }
    }

    @Override
    public Produit lireParId(Long id) {
        try {
            return produitRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("❌ Produit avec ID " + id + " non trouvé"));
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du produit ID=" + id + " : " + e.getMessage());
            throw new RuntimeException("Échec de la récupération du produit: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<Produit> lireProduitsPagine(int numeroPage, int taillePage, String triPar) {
        try {
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
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des produits paginés : " + e.getMessage());
            throw new RuntimeException("Échec de la récupération des produits paginés: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<Produit> recherche(String recherche, String critere) {
        try {
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
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche des produits : " + e.getMessage());
            throw new RuntimeException("Échec de la recherche des produits: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<Produit> findByCategory(Category category, int page, int pageSize, String sortBy) {
        try {
            Sort.Direction directionTri = Sort.Direction.ASC;
            String proprieteTri = "id";
            if (sortBy != null && !sortBy.isEmpty()) {
                if (sortBy.startsWith("-")) {
                    directionTri = Sort.Direction.DESC;
                    proprieteTri = sortBy.substring(1);
                } else {
                    proprieteTri = sortBy;
                }
            }
            return produitRepository.findByCategory(category, PageRequest.of(page, pageSize, directionTri, proprieteTri));
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche des produits par catégorie : " + e.getMessage());
            throw new RuntimeException("Échec de la recherche des produits par catégorie: " + e.getMessage(), e);
        }
    }

    private Long getCurrentUserId() {
        return 1L;
    }

    @Override
    public List<Produit> recommendProductsBasedOnHistory(Long userId, int limit) {
        try {
            // 1. Récupérer l'historique d'achat de l'utilisateur
            List<Purchase> purchases = purchaseRepository.findByUserId(userId);
            if (purchases.isEmpty()) {
                // Fallback : retourner les produits les plus vendus
                return produitRepository.findAll(Sort.by(Sort.Direction.DESC, "salesCount"))
                        .stream()
                        .filter(p -> p.getStock() > 0)
                        .limit(limit)
                        .collect(Collectors.toList());
            }

            // 2. Identifier les catégories préférées
            Map<Category, Long> categoryFrequency = new HashMap<>();
            for (Purchase purchase : purchases) {
                for (Produit produit : purchase.getProduits()) {
                    categoryFrequency.merge(produit.getCategory(), 1L, Long::sum);
                }
            }

            // Trier les catégories par fréquence
            List<Category> preferredCategories = categoryFrequency.entrySet().stream()
                    .sorted(Map.Entry.<Category, Long>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // 3. Recommander des produits des catégories préférées, triés par salesCount
            List<Produit> recommendations = new ArrayList<>();
            for (Category category : preferredCategories) {
                List<Produit> categoryProducts = produitRepository.findByCategory(
                                category,
                                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "salesCount"))
                        ).getContent()
                        .stream()
                        .filter(p -> p.getStock() > 0)
                        .collect(Collectors.toList());
                recommendations.addAll(categoryProducts);
                if (recommendations.size() >= limit) break;
            }

            // 4. Compléter avec les produits les plus vendus si nécessaire
            if (recommendations.size() < limit) {
                List<Produit> topSellingProducts = produitRepository.findAll(Sort.by(Sort.Direction.DESC, "salesCount"))
                        .stream()
                        .filter(p -> p.getStock() > 0 && !recommendations.contains(p))
                        .limit(limit - recommendations.size())
                        .collect(Collectors.toList());
                recommendations.addAll(topSellingProducts);
            }

            return recommendations.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erreur lors de la recommandation basée sur l'historique : " + e.getMessage());
            throw new RuntimeException("Échec de la recommandation: " + e.getMessage(), e);
        }
    }
    @Override
    public List<Produit> getTopSellingProducts(int limit) {
        return produitRepository.findAll(Sort.by(Sort.Direction.DESC, "salesCount"))
                .stream()
                .limit(limit)
                .toList();
    }
    @Override
    public Page<Produit> searchProducts(
            String nom,
            Category category,
            Double minPrice,
            Double maxPrice,
            String fournisseur,
            int page,
            int size,
            String sort) {

        try {
            // Gestion du tri
            Sort.Direction direction = Sort.Direction.ASC;
            String property = "id";

            if (sort != null && !sort.isEmpty()) {
                if (sort.startsWith("-")) {
                    direction = Sort.Direction.DESC;
                    property = sort.substring(1);
                } else {
                    property = sort;
                }
            }

            Pageable pageable = PageRequest.of(page, size, direction, property);

            return produitRepository.searchProducts(
                    nom,
                    category,
                    minPrice,
                    maxPrice,
                    fournisseur,
                    pageable);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des produits", e);
        }
    }

}

