package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Fidelite;
import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.repository.FideliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.usermanagementbackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FideliteService implements IFideliteService {

    @Autowired
    private FideliteRepository fideliteRepository;

    @Autowired
    private UserRepository userRepository;  // Injection de UserRepository

    // --- CRUD ---
    public List<Fidelite> getAllFidelites() {
        return fideliteRepository.findAll();
    }

    public Fidelite getFideliteById(Integer id) {
        return fideliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fidélité non trouvée pour l'ID: " + id)); // Message d'erreur plus explicite
    }

    public Fidelite saveFidelite(Fidelite fidelite) {
        return fideliteRepository.save(fidelite);
    }

    public void deleteFidelite(Integer id) {
        fideliteRepository.deleteById(id);
    }

    // Ajouter des points à un utilisateur
    @Override
    public void ajouterPoints(Integer userId, int points) {
        Fidelite fidelite = fideliteRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Fidélité non trouvée pour l'utilisateur avec ID: " + userId));

        fidelite.setPoints(fidelite.getPoints() + points);
        mettreAJourNiveau(fidelite);
        fideliteRepository.save(fidelite);

    }

    private void mettreAJourNiveau(Fidelite fidelite) {
        if (fidelite.getPoints() >= 1000) fidelite.setNiveau("Or");
        else if (fidelite.getPoints() >= 500) fidelite.setNiveau("Argent");
        else fidelite.setNiveau("Bronze");
    }

    // Ajouter des points de fidélité lorsqu'un utilisateur effectue un achat
    public void ajouterPointsFidelite(Integer utilisateurId, int points) {
        Optional<Fidelite> fideliteOpt = fideliteRepository.findByUser_Id(utilisateurId);

        if (fideliteOpt.isPresent()) {
            Fidelite fidelite = fideliteOpt.get();
            fidelite.setPoints(fidelite.getPoints() + 100); // Ajouter les points
            fideliteRepository.save(fidelite); // Sauvegarder la mise à jour
        } else {
            // Si la fidélité n'existe pas, en créer une nouvelle
            User user = userRepository.findById(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + utilisateurId)); // Message d'erreur explicite
            Fidelite fidelite = new Fidelite();
            fidelite.setUser(user);
            fidelite.setPoints(points); // Initialiser avec les points
            fidelite.setNiveau("Bronze"); // Initialiser avec niveau Bronze
            fideliteRepository.save(fidelite); // Sauvegarder la fidélité
        }
    }












    // Méthode pour appliquer une promotion spéciale au carte fidélité
    public void appliquerPromotionFidelite(Integer utilisateurId, double montantAchat) {
        Optional<Fidelite> fideliteOpt = fideliteRepository.findByUser_Id(utilisateurId);

        if (fideliteOpt.isPresent()) {
            Fidelite fidelite = fideliteOpt.get();
            if ("Or".equals(fidelite.getNiveau())) {
                montantAchat *= 0.80;  // Applique 20% de réduction pour les membres Or
            }
            // Sauvegarder le montant avec promotion appliquée ou toute autre logique
        }
    }






    // récompenser des points
    public void recompensePointsFidelite(Integer utilisateurId, double montantAchat) {
        Optional<Fidelite> fideliteOpt = fideliteRepository.findByUser_Id(utilisateurId);

        if (fideliteOpt.isPresent()) {
            Fidelite fidelite = fideliteOpt.get();

            // 1 Dinar = 1 Point
            int pointsGagnes = (int) montantAchat;
            fidelite.setPoints(fidelite.getPoints() + pointsGagnes);

            // Mettre à jour le niveau si le seuil est atteint
            fidelite.mettreAJourNiveau();

            // Sauvegarde en base de données
            fideliteRepository.save(fidelite);
        }
    }
}