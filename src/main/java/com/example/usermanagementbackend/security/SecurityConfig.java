package com.example.usermanagementbackend.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and() // Enable CORS if required
                .csrf().disable()  // Disable CSRF (you can enable it based on your app's needs)
                .httpBasic().disable()  // Disable HTTP Basic Authentication
                .formLogin().disable()  // Disable form-based login
                .authorizeRequests()
                // Allow access to registration and authentication routes for everyone
                .requestMatchers("/api/users/register", "/api/auth/**").permitAll()  // Allow registration and login for all users
                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll() // Allow GET requests for users
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll() // Allow DELETE requests for users
                .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll() // Allow authenticated users to update
                .requestMatchers("/api/factures/**").permitAll()
                .requestMatchers("/api/commandes/**").permitAll()
                // Allow access to /notifications/** for everyone
                // All other requests must be authenticated
                .anyRequest().authenticated();

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
