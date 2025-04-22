package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.enums.Category;
import com.example.usermanagementbackend.repository.MouvementStockRepository;
import com.example.usermanagementbackend.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final StockService stockService;
    private final MouvementStockRepository mouvementStockRepository;

    @Override
    @Transactional
    public Produit creer(Produit produit) {
        try {
            System.out.println("Création du produit : " + produit);
            produit.setFournisseurId(getCurrentUserId());

            // Sauvegarder d'abord le produit
            Produit savedProduit = produitRepository.save(produit);
            produitRepository.flush(); // Force la synchronisation avec la base de données

            System.out.println("Produit créé avec succès : " + savedProduit);

            // Maintenant que le produit a un ID, on peut gérer le stock
            stockService.verifierEtReapprovisionner(savedProduit);

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
}