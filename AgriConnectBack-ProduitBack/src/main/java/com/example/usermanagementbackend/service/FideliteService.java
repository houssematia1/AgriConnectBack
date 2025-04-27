package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Fidelite;
import com.example.usermanagementbackend.entity.PointHistory;
import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.repository.FideliteRepository;
import com.example.usermanagementbackend.repository.PointHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FideliteService implements IFideliteService {

    @Autowired
    private FideliteRepository fideliteRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private UserService userService;

    // --- CRUD Operations ---
    @Override
    public List<Fidelite> getAllFidelites() {
        return fideliteRepository.findAllWithUser();
    }

    @Override
    public Fidelite getFideliteById(Integer id) {
        return fideliteRepository.findByIdWithUser(id)
                .orElseThrow(() -> new RuntimeException("Programme de fidélité non trouvé"));
    }

    @Override
    @Transactional
    public Fidelite saveFidelite(Fidelite fidelite) {
        if (fidelite.getUser() == null) {
            throw new RuntimeException("Utilisateur requis pour le programme de fidélité");
        }
        Optional<Fidelite> existingFidelite = fideliteRepository.findByUserId(fidelite.getUser().getId());
        if (existingFidelite.isPresent()) {
            throw new RuntimeException("Un programme de fidélité existe déjà pour cet utilisateur");
        }
        fidelite.mettreAJourNiveau();
        return fideliteRepository.save(fidelite);
    }

    @Override
    @Transactional
    public void deleteFidelite(Integer id) {
        Fidelite fidelite = getFideliteById(id);
        pointHistoryRepository.deleteByFideliteId(id);
        fideliteRepository.delete(fidelite);
    }

    // --- Point Management ---
    @Override
    @Transactional
    public void ajouterPoints(Long userId, int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Les points doivent être positifs");
        }
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Fidelite fidelite = fideliteRepository.findByUserId(userId)
                .orElse(new Fidelite(null, 0, "Bronze", user));
        fidelite.setPoints(fidelite.getPoints() + points);
        fidelite.mettreAJourNiveau();
        PointHistory history = new PointHistory(null, fidelite, points, "Ajout manuel", LocalDate.now());
        pointHistoryRepository.save(history);
        fideliteRepository.save(fidelite);
    }

    @Override
    @Transactional
    public Fidelite addPointsForPurchase(Long userId, double purchaseAmount) {
        if (purchaseAmount <= 0) {
            throw new IllegalArgumentException("Le montant d'achat doit être positif");
        }
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Fidelite fidelite = fideliteRepository.findByUserId(userId)
                .orElse(new Fidelite(null, 0, "Bronze", user));
        int points = (int) purchaseAmount; // 1 point per dinar
        fidelite.setPoints(fidelite.getPoints() + points);
        fidelite.mettreAJourNiveau();
        PointHistory history = new PointHistory(null, fidelite, points, "Achat de " + purchaseAmount + " TND", LocalDate.now());
        pointHistoryRepository.save(history);
        return fideliteRepository.save(fidelite);
    }

    @Override
    @Transactional
    public Fidelite addBirthdayPoints(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        LocalDate today = LocalDate.now();
        if (user.getDateOfBirth() == null ||
                user.getDateOfBirth().getMonthValue() != today.getMonthValue() ||
                user.getDateOfBirth().getDayOfMonth() != today.getDayOfMonth()) {
            throw new RuntimeException("Ce n'est pas l'anniversaire de l'utilisateur");
        }
        Fidelite fidelite = fideliteRepository.findByUserId(userId)
                .orElse(new Fidelite(null, 0, "Bronze", user));
        int birthdayPoints = 50;
        fidelite.setPoints(fidelite.getPoints() + birthdayPoints);
        fidelite.mettreAJourNiveau();
        PointHistory history = new PointHistory(null, fidelite, birthdayPoints, "Bonus d'anniversaire", LocalDate.now());
        pointHistoryRepository.save(history);
        return fideliteRepository.save(fidelite);
    }

    // --- Other Methods ---
    @Override
    public List<PointHistory> getPointHistory(Long userId) {
        Fidelite fidelite = fideliteRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Programme de fidélité non trouvé"));
        return pointHistoryRepository.findByFideliteId(fidelite.getId());
    }

    @Override
    public Optional<Fidelite> getFideliteByUserId(Long userId) {
        return fideliteRepository.findByUserId(userId);
    }

    // --- Scheduled Task for Birthday Points ---
    @Scheduled(cron = "0 0 9 * * *") // Exécute tous les jours à 9h00
    @Transactional
    public void checkAndAddBirthdayPoints() {
        List<User> allUsers = userService.getAllUsers();
        LocalDate today = LocalDate.now();
        for (User user : allUsers) {
            if (user.getDateOfBirth() != null &&
                    user.getDateOfBirth().getMonthValue() == today.getMonthValue() &&
                    user.getDateOfBirth().getDayOfMonth() == today.getDayOfMonth()) {
                try {
                    addBirthdayPoints(user.getId());
                } catch (RuntimeException e) {
                    // Log l'erreur mais continue pour les autres utilisateurs
                    System.err.println("Erreur lors de l'ajout des points d'anniversaire pour l'utilisateur " + user.getId() + ": " + e.getMessage());
                }
            }
        }
    }
}