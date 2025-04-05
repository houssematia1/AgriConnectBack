package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.OtpCode;
import com.example.usermanagementbackend.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class TwoFactorAuthService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    // Génère un OTP à 6 chiffres, le sauvegarde en base et le retourne
    public String generateAndSaveOTP(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);
        // Supprimer tout code existant pour cet email
        otpRepository.deleteByEmail(email);
        OtpCode otpCode = new OtpCode(email, otp, expiration);
        otpRepository.save(otpCode);
        return otp;
    }

    // Envoie l'OTP par email
    public void sendOTPEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Votre code de vérification 2FA");
        message.setText("Bonjour,\n\nVotre code de vérification est : " + otp + "\nCe code expire dans 5 minutes.\n\nCordialement,\nL'équipe");
        mailSender.send(message);
    }

    // Vérifie que le code OTP est valide pour l'email donné
    public boolean verifyOTP(String email, String otp) {
        Optional<OtpCode> optionalOtp = otpRepository.findByEmail(email);
        if(optionalOtp.isPresent()) {
            OtpCode otpCode = optionalOtp.get();
            if(otpCode.getOtp().equals(otp) && LocalDateTime.now().isBefore(otpCode.getExpiration())) {
                otpRepository.delete(otpCode);
                return true;
            }
        }
        return false;
    }
}
