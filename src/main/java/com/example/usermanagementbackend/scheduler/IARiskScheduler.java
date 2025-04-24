package com.example.usermanagementbackend.scheduler;

import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IARiskScheduler {

    private final UserService userService;

    public IARiskScheduler(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void evaluerRisqueDesactivation() {
        List<User> users = userService.getAllUsers();

        for (User user : users) {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("last_login", user.getDerniereConnexion().toString());
                data.put("login_count", user.getNombreConnexions());
                data.put("actions_count", user.getActionsEffectuees());
                data.put("is_blocked", user.isBlocked());
                data.put("is_verified", user.isVerified());

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Map> response = restTemplate.postForEntity("http://localhost:8000/predict", data, Map.class);

                Object score = response.getBody().get("risk_score");
                System.out.println("🔮 IA → " + user.getEmail() + " = " + score + "%");
            } catch (Exception e) {
                System.err.println("❌ Erreur IA pour " + user.getEmail() + " : " + e.getMessage());
            }
        }
    }
}
