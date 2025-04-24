/*package com.example.usermanagementbackend.controller;

import com.example.usermanagementbackend.scheduler.StatistiquesScheduler;
import com.example.usermanagementbackend.scheduler.IARiskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:4200")
public class StatistiquesController {

    @Autowired
    private StatistiquesScheduler statistiquesScheduler;

    @Autowired
    private IARiskScheduler iaRiskScheduler;

    @GetMapping("/stats-hebdo")
    public String runStatsHebdomadaire() {
        statistiquesScheduler.calculerStatsHebdo();
        return "✅ Statistiques hebdomadaires calculées ";
    }

    @GetMapping("/ia-risk")
    public String runIARiskPrediction() {
        iaRiskScheduler.evaluerRisqueDesactivation();
        return "✅ Évaluation du risque IA effectuée ";
    }
}
*/