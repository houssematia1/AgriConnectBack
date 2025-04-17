package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.*;
import com.example.usermanagementbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PromotionService implements IPromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private FideliteRepository fideliteRepository;

    @Autowired
    private PromotionUsageRepository promotionUsageRepository;

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Optional<Promotion> getPromotionById(Integer id) {
        return promotionRepository.findById(id);
    }

    @Override
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion updatePromotion(Integer id, Promotion promotion) {
        return promotionRepository.findById(id).map(existingPromotion -> {
            existingPromotion.setNom(promotion.getNom());
            existingPromotion.setPourcentageReduction(promotion.getPourcentageReduction());
            existingPromotion.setDateDebut(promotion.getDateDebut());
            existingPromotion.setDateFin(promotion.getDateFin());
            existingPromotion.setConditionPromotion(promotion.getConditionPromotion());
            existingPromotion.setProduits(promotion.getProduits());
            existingPromotion.setActive(promotion.isActive());
            return promotionRepository.save(existingPromotion);
        }).orElseThrow(() -> new RuntimeException("Promotion not found"));
    }

    @Override
    public void deletePromotion(Integer id) {
        promotionRepository.deleteById(id);
    }

    public double appliquerPromotion(double montantTotal, Promotion promotion) {
        if (promotion == null || promotion.getConditionPromotion() == null || !promotion.isActive()) {
            return montantTotal;
        }

        String condition = promotion.getConditionPromotion();
        double reduction = promotion.getPourcentageReduction() / 100;
        double montantApresReduction = montantTotal;

        if ("ACHAT_GROUPE".equals(condition) && montantTotal >= 3) {
            montantApresReduction = montantTotal * (1 - reduction);
        } else if ("MONTANT_MIN".equals(condition) && montantTotal > 100) {
            montantApresReduction = montantTotal * (1 - reduction);
        } else if ("EXPIRATION_PRODUIT".equals(condition)) {
            montantApresReduction = montantTotal * (1 - reduction);
        }

        // Track usage
        PromotionUsage usage = new PromotionUsage();
        usage.setPromotion(promotion);
        usage.setMontantInitial(montantTotal);
        usage.setMontantApresReduction(montantApresReduction);
        usage.setDateApplication(new Date());
        promotionUsageRepository.save(usage);

        return montantApresReduction;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void verifierPromotionsActives() {
        List<Promotion> promotions = promotionRepository.findAll();
        Date today = new Date();

        for (Promotion promo : promotions) {
            if (promo.getDateFin() != null && promo.getDateFin().before(today)) {
                promo.setActive(false);
                promotionRepository.save(promo);
            }
        }
    }

    public List<Promotion> getPromotionsActives() {
        return promotionRepository.findByActiveTrue();
    }

    @Override
    public void appliquerPromotionExpirationProduit() {
        List<Produit> produits = produitRepository.findAll();
        Date today = new Date();

        for (Produit produit : produits) {
            if (produit.getDateExpiration() != null) {
                LocalDate expirationDate = produit.getDateExpiration()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate todayLocal = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(todayLocal, expirationDate);

                if (daysRemaining <= 5 && daysRemaining >= 0) {
                    Optional<Promotion> existingPromo = promotionRepository.findByConditionPromotionAndActiveTrue("EXPIRATION_PRODUIT");
                    Promotion promo;
                    if (existingPromo.isPresent()) {
                        promo = existingPromo.get();
                    } else {
                        promo = new Promotion();
                        promo.setNom("Promotion Expiration Produit");
                        promo.setPourcentageReduction(40);
                        promo.setConditionPromotion("EXPIRATION_PRODUIT");
                        promo.setDateDebut(today);
                        LocalDate dateFinLocal = todayLocal.plusDays(5);
                        promo.setDateFin(Date.from(dateFinLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        promo.setActive(true);
                        promo = promotionRepository.save(promo);
                    }
                    appliquerPromotionSurProduit(produit, promo);
                }
            }
        }
    }

    public void appliquerPromotionSurProduit(Produit produit, Promotion promo) {
        double newPrice = produit.getPrix() * (1 - promo.getPourcentageReduction() / 100);
        produit.setPrix(newPrice);
        produit.getPromotions().add(promo);
        produitRepository.save(produit);
    }

    @Scheduled(cron = "0 0 0 25 11 ?")
    public void appliquerPromoBlackFriday() {
        Optional<Promotion> blackFridayPromoOpt = promotionRepository.findByNom("Black Friday");
        if (blackFridayPromoOpt.isPresent()) {
            Promotion blackFridayPromo = blackFridayPromoOpt.get();
            if (blackFridayPromo.isActive()) {
                List<Produit> produits = produitRepository.findAll();
                for (Produit produit : produits) {
                    if (produit.getPromotions().stream().noneMatch(p -> p.isActive() && !p.equals(blackFridayPromo))) {
                        double prixAvecReduction = produit.getPrix() * (1 - blackFridayPromo.getPourcentageReduction() / 100);
                        produit.setPrix(prixAvecReduction);
                        produit.getPromotions().add(blackFridayPromo);
                        produitRepository.save(produit);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 0 28 11 ?")
    public void desactiverPromoBlackFriday() {
        Optional<Promotion> blackFridayPromoOpt = promotionRepository.findByNom("Black Friday");
        blackFridayPromoOpt.ifPresent(promo -> {
            promo.setActive(false);
            promotionRepository.save(promo);
        });
    }

    @Override
    public void bulkActivate(List<Integer> ids) {
        List<Promotion> promotions = promotionRepository.findAllById(ids);
        for (Promotion promo : promotions) {
            promo.setActive(true);
            promotionRepository.save(promo);
        }
    }

    @Override
    public void bulkDeactivate(List<Integer> ids) {
        List<Promotion> promotions = promotionRepository.findAllById(ids);
        for (Promotion promo : promotions) {
            promo.setActive(false);
            promotionRepository.save(promo);
        }
    }

    @Override
    public void bulkDelete(List<Integer> ids) {
        promotionRepository.deleteAllById(ids);
    }

    public Map<String, Object> getPromotionAnalytics() {
        List<PromotionUsage> usageList = promotionUsageRepository.findAll();
        Map<Integer, List<PromotionUsage>> usageByPromotion = usageList.stream()
                .collect(Collectors.groupingBy(usage -> usage.getPromotion().getId()));

        Map<String, Object> analytics = new HashMap<>();
        List<Map<String, Object>> promotionStats = new ArrayList<>();

        for (Map.Entry<Integer, List<PromotionUsage>> entry : usageByPromotion.entrySet()) {
            Integer promoId = entry.getKey();
            List<PromotionUsage> usages = entry.getValue();
            Promotion promo = promotionRepository.findById(promoId).orElse(null);
            if (promo == null) continue;

            double totalRevenueImpact = usages.stream()
                    .mapToDouble(usage -> usage.getMontantInitial() - usage.getMontantApresReduction())
                    .sum();
            long usageCount = usages.size();

            Map<String, Object> stat = new HashMap<>();
            stat.put("promotionId", promoId);
            stat.put("promotionName", promo.getNom());
            stat.put("usageCount", usageCount);
            stat.put("totalRevenueImpact", totalRevenueImpact);
            promotionStats.add(stat);
        }

        analytics.put("promotionStats", promotionStats);
        analytics.put("totalPromotionsApplied", usageList.size());
        return analytics;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void suggestPromotions() {
        List<Produit> produits = produitRepository.findAll();
        Date today = new Date();

        for (Produit produit : produits) {
            Integer salesCount = produit.getSalesCount() != null ? produit.getSalesCount() : 0;
            boolean lowSales = salesCount < 10;
            boolean nearingExpiration = false;

            if (produit.getDateExpiration() != null) {
                LocalDate expirationDate = produit.getDateExpiration()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate todayLocal = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(todayLocal, expirationDate);
                nearingExpiration = daysRemaining <= 10 && daysRemaining >= 0;
            }

            if (lowSales || nearingExpiration) {
                Optional<Promotion> existingPromo = promotionRepository.findByNom("AI Suggested Promotion for " + produit.getNom());
                if (!existingPromo.isPresent()) {
                    Promotion promo = new Promotion();
                    promo.setNom("AI Suggested Promotion for " + produit.getNom());
                    promo.setPourcentageReduction(nearingExpiration ? 50 : 30);
                    promo.setConditionPromotion(nearingExpiration ? "EXPIRATION_PRODUIT" : "MONTANT_MIN");
                    promo.setDateDebut(today);
                    LocalDate dateFinLocal = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(7);
                    promo.setDateFin(Date.from(dateFinLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    promo.setActive(true);
                    List<Produit> produitsList = new ArrayList<>();
                    produitsList.add(produit);
                    promo.setProduits(produitsList);
                    promotionRepository.save(promo);
                }
            }
        }
    }
}