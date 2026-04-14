package com.giunei.my_museum.features.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verificação de Email - My Museum");
        message.setText("Olá!\n\n" +
                "Por favor, clique no link abaixo para verificar seu email:\n" +
                verificationUrl + "\n\n" +
                "Este link expirará em 24 horas.\n\n" +
                "Obrigado!\n" +
                "Equipe My Museum");
        
        try {
            mailSender.send(message);
            log.info("Email de verificação enviado para: {}", toEmail);
        } catch (Exception e) {
            log.error("Erro ao enviar email de verificação para: {}", toEmail, e);
            throw new RuntimeException("Não foi possível enviar o email de verificação");
        }
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Redefinição de Senha - My Museum");
        message.setText("Olá!\n\n" +
                "Clique no link abaixo para redefinir sua senha:\n" +
                resetUrl + "\n\n" +
                "Este link expirará em 1 hora.\n\n" +
                "Se você não solicitou esta redefinição, ignore este email.\n\n" +
                "Equipe My Museum");
        
        try {
            mailSender.send(message);
            log.info("Email de redefinição de senha enviado para: {}", toEmail);
        } catch (Exception e) {
            log.error("Erro ao enviar email de redefinição para: {}", toEmail, e);
            throw new RuntimeException("Não foi possível enviar o email de redefinição");
        }
    }
}
