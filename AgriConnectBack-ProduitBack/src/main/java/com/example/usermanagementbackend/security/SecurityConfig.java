package com.example.usermanagementbackend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.addAllowedOrigin("http://localhost:4200");
                    corsConfiguration.addAllowedMethod("*");
                    corsConfiguration.addAllowedHeader("*");
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Autoriser les requêtes OPTIONS pour CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // WebSocket
                        .requestMatchers("/ws/**", "/topic/**", "/app/**").permitAll()

                        // Fichiers statiques
                        .requestMatchers("/index.html", "/static/**", "/assets/**", "/").permitAll()

                        // Auth routes
                        .requestMatchers("/api/users/register", "/api/auth/**").permitAll()

                        // API user GET/PUT/DELETE
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()

                        // Notifications
                        .requestMatchers("/notifications/**").permitAll()

                        // Produits
                        .requestMatchers("/api/produits/**").permitAll()

                        // Images (pour servir les fichiers uploadés)
                        .requestMatchers("/uploads/**").permitAll()

                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest().authenticated()
                )
                // Ajouter un log pour diagnostiquer les requêtes bloquées
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            System.out.println("Access Denied for request: " + request.getRequestURI() + " - " + accessDeniedException.getMessage());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                        })
                );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/assets/**", "/static/**", "/css/**", "/js/**", "/images/**");
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