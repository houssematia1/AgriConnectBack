package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Fidelite;
import com.example.usermanagementbackend.entity.Produit;
import com.example.usermanagementbackend.entity.Promotion;
import com.example.usermanagementbackend.repository.FideliteRepository;
import com.example.usermanagementbackend.repository.ProduitRepository;
import com.example.usermanagementbackend.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
@Service
public class PromotionService  implements IPromotionService {
    private PromotionRepository promotionRepository;  // Référencement du repository pour interagir avec la base de données
    // Ajout de l'injection de dépendance pour ProduitRepository
@Autowired
    private ProduitRepository produitRepository;
@Autowired
    private FideliteRepository fideliteRepository;

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
        // Récupère toutes les promotions stockées dans la base de données
    }

    @Override
    public Optional<Promotion> getPromotionById(Integer id) {
        return promotionRepository.findById(id);
        // Recherche une promotion par son ID, retourne un Optional pour éviter les erreurs NullPointerException
    }

    @Override
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
        // Enregistre une nouvelle promotion dans la base de données et la retourne
    }

    @Override
    public Promotion updatePromotion(Integer id, Promotion promotion) {
        return promotionRepository.findById(id).map(existingPromotion -> {
            // Si l'ID de la promotion existe, on met à jour les champs

            existingPromotion.setNom(promotion.getNom());
            // Met à jour le nom de la promotion

            existingPromotion.setPourcentageReduction(promotion.getPourcentageReduction());
            // Met à jour le pourcentage de réduction

            existingPromotion.setDateDebut(promotion.getDateDebut());
            // Met à jour la date de début

            existingPromotion.setDateFin(promotion.getDateFin());
            // Met à jour la date de fin

            existingPromotion.setProduits(promotion.getProduits());
            // Met à jour la liste des produits liés à la promotion

            return promotionRepository.save(existingPromotion);
            // Sauvegarde et retourne la promotion mise à jour
        }).orElseThrow(() -> new RuntimeException("Promotion not found"));
        // Si l'ID de la promotion n'existe pas, une exception est levée
    }

    @Override
    public void deletePromotion(Integer id) {
        promotionRepository.deleteById(id);
        // Supprime une promotion de la base de données en fonction de son ID
    }


    //Appliquer une promotion avec condition
    public double appliquerPromotion(double montantTotal, Promotion promotion) {
        if (promotion == null || promotion.getConditionPromotion() == null) {
            return montantTotal; // Aucune promotion à appliquer
        }

        String condition = promotion.getConditionPromotion();
        double reduction = promotion.getPourcentageReduction() / 100;

        if ("ACHAT_GROUPE".equals(condition) && montantTotal >= 3) {
            return montantTotal * (1 - reduction);
        } else if ("MONTANT_MIN".equals(condition) && montantTotal > 100) {
            return montantTotal * (1 - reduction);
        } else if ("EXPIRATION_PRODUIT".equals(condition)) {
            return montantTotal * (1 - reduction); // Applique la réduction pour un produit proche de l'expiration
        }

        return montantTotal; // Pas de réduction applicable
    }


    //Désactiver une promotion lorsque sa date de fin est dépassée
    @Scheduled(cron = "0 0 0 * * ?") // Exécution tous les jours à minuit
    public void verifierPromotionsActives() {
        List<Promotion> promotions = promotionRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Promotion promo : promotions) {
            if (promo.getDateFin().isBefore(today)) {
                promo.setActive(false);
                promotionRepository.save(promo);
            }
        }
    }

    public List<Promotion> getPromotionsActives() {
        return promotionRepository.findByActiveTrue();
    }




    //appliquerPromotionExpirationProduit
    public void appliquerPromotionExpirationProduit() {
        List<Produit> produits = produitRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Produit produit : produits) {
            if (produit.getDateExpiration() != null) {
                LocalDate expirationDate = produit.getDateExpiration()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                long daysRemaining = ChronoUnit.DAYS.between(today, expirationDate);

                if (daysRemaining <= 5) {
                    Promotion promo = new Promotion();
                    promo.setNom("Promotion Expiration Produit");
                    promo.setPourcentageReduction(40);
                    promo.setConditionPromotion("EXPIRATION_PRODUIT");
                    promo.setDateDebut(today);
                    promo.setDateFin(today.plusDays(5));

                    appliquerPromotionSurProduit(produit, promo);
                }
            }
        }
    }





    private void appliquerPromotionSurProduit(Produit produit, Promotion promo) {
        double newPrice = produit.getPrix() * (1 - promo.getPourcentageReduction() / 100);
        produit.setPrix(newPrice);
        produitRepository.save(produit);
    }









    //Promotion black friday sur tous les produits
    @Scheduled(cron = "0 0 0 25 11 ?") // Exécution chaque année à la date du Black Friday
    public void appliquerPromoBlackFriday() {
        List<Produit> produits = produitRepository.findAll();
        Promotion blackFridayPromo = promotionRepository.findByNom("Black Friday").stream().findFirst().orElse(null);

        if (blackFridayPromo != null) {
            for (Produit produit : produits) {
                double prixAvecReduction = produit.getPrix() * (1 - blackFridayPromo.getPourcentageReduction() / 100);
                produit.setPrix(prixAvecReduction);
                produitRepository.save(produit);
            }
        }
    }
    //Desactiver la promotion de blackfriday
    @Scheduled(cron = "0 0 0 28 11 ?") // Exécution le 28 novembre pour désactiver la promotion
    public void desactiverPromoBlackFriday() {
        Promotion blackFridayPromo = promotionRepository.findByNom("Black Friday").stream().findFirst().orElse(null);

        if (blackFridayPromo != null) {
            blackFridayPromo.setActive(false);
            promotionRepository.save(blackFridayPromo);
        }
    }

//Promotion pour les utilisateurs ayant des cartes des fidélités

    public double appliquerPromoFidelite(Integer utilisateurId, double montantTotal) {
        // Recherche de la fidélité de l'utilisateur
        Optional<Fidelite> fideliteOpt = fideliteRepository.findByUser_Id(utilisateurId);  // Rechercher par l'ID de l'utilisateur

        // Vérifier si l'objet Fidelite est présent dans l'Optional
        if (fideliteOpt.isPresent()) {
            Fidelite fidelite = fideliteOpt.get();  // Récupérer la valeur contenue dans l'Optional

            // Vérification du niveau de fidélité et application de la promotion
            double reduction = 0.0;

            // Appliquer la réduction en fonction du niveau de fidélité
            if ("Or".equals(fidelite.getNiveau())) {
                reduction = 0.30;  // 30% pour le niveau Or
            } else if ("Argent".equals(fidelite.getNiveau())) {
                reduction = 0.20;  // 20% pour le niveau Argent
            } else if ("Bronze".equals(fidelite.getNiveau())) {
                reduction = 0.10;  // 10% pour le niveau Bronze
            }

            // Calcul du montant après réduction
            double montantAvecReduction = montantTotal * (1 - reduction);
            return montantAvecReduction;
        } else {
            // Retourner le montant sans réduction si la fidélité n'existe pas
            return montantTotal;
        }
    }

}
