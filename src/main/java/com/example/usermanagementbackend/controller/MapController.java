package com.example.usermanagementbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class MapController {
    @GetMapping("/showMap")
    public String index() {
        return "index";
    }
}
