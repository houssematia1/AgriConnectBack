package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public User saveUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Un compte avec cet email existe déjà.");
        }

        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(user.getMotDePasse());
            user.setMotDePasse(hashedPassword);
        }

        // ✅ Génération d’un code à 6 chiffres uniquement
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        user.setVerificationCode(code);
        user.setVerified(false);

        // ✅ Envoi de l’email pro
        sendVerificationEmail(user.getEmail(), code);

        return userRepository.save(user);
    }

    public void sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(toEmail);
            helper.setSubject("Vérification de votre compte - AgriConnect");

            String htmlContent = "<div style='font-family: Arial, sans-serif; font-size: 16px;'>"
                    + "<p>Bonjour,</p>"
                    + "<p>Merci pour votre inscription sur <strong>AgriConnect</strong>.</p>"
                    + "<p>Voici votre code de vérification :</p>"
                    + "<h2 style='color: #2e7d32; font-size: 28px;'>" + code + "</h2>"
                    + "<p>Ce code est valable pour une durée limitée.</p>"
                    + "<br><p>Cordialement,<br>L’équipe AgriConnect</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            helper.setFrom("noreply@agriconnect.com"); // ✅ nom visible (à adapter selon ton fournisseur SMTP)

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email de vérification.");
        }
    }

    public User saveUserDirect(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User user) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        User existingUser = existingUserOpt.get();
        existingUser.setNom(user.getNom());
        existingUser.setPrenom(user.getPrenom());
        existingUser.setEmail(user.getEmail());

        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(user.getMotDePasse());
            existingUser.setMotDePasse(hashedPassword);
        }

        existingUser.setNumeroDeTelephone(user.getNumeroDeTelephone());
        existingUser.setRole(user.getRole());
        existingUser.setAdresseLivraison(user.getAdresseLivraison());
        return userRepository.save(existingUser);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query);
    }
}
