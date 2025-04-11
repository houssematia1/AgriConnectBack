package com.example.usermanagementbackend.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Active CORS
                .cors(withDefaults())
                // Désactive CSRF (attention en production)
                .csrf(csrf -> csrf.disable())
                // Configure les règles d'autorisation
                .authorizeHttpRequests(auth -> auth
                        // Autorise toutes les requêtes OPTIONS (pour le prévol CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Autorise l'inscription et l'authentification
                        .requestMatchers("/api/users/register", "/api/auth/**").permitAll()
                        // Autorise toutes les requêtes GET sur /api/users et ses sous-chemins
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                        // Autorise toutes les requêtes DELETE sur /api/users et ses sous-chemins
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()
                        // Toute autre requête doit être authentifiée
                        .anyRequest().authenticated()
                )
                // Active l'authentification basique HTTP (pour tester avec Postman)
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("adminpass"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("userpass"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
