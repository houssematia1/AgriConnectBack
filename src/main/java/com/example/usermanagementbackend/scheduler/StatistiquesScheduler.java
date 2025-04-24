package com.example.usermanagementbackend.scheduler;

import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatistiquesScheduler {

    private final UserService userService;

    public StatistiquesScheduler(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 23 ? * SUN")
    public void calculerStatsHebdo() {
        List<User> users = userService.getAllUsers();

        long nouveauxUtilisateurs = users.stream()
                .filter(u -> u.getDerniereConnexion() != null &&
                        u.getDerniereConnexion().isAfter(LocalDateTime.now().minusDays(7)))
                .count();

        long comptesBloques = users.stream().filter(User::isBlocked).count();

        double actionsMoyennes = users.stream()
                .mapToInt(User::getActionsEffectuees)
                .average()
                .orElse(0);

        System.out.println("📊 [Statistiques Hebdo]");
        System.out.println("Nouveaux utilisateurs : " + nouveauxUtilisateurs);
        System.out.println("Comptes bloqués : " + comptesBloques);
        System.out.println("Actions moyennes : " + actionsMoyennes);
    }
}
