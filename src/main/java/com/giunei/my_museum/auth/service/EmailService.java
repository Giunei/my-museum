package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.common.exception.EmailDeliveryException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verificação de Email - My Museum");
        message.setText("Olá!\n\n" +
                "Clique no link abaixo para verificar seu email:\n" +
                verificationUrl + "\n\n" +
                "Este link expira em 24 horas.\n\n" +
                "Equipe My Museum");

        send(message);
    }

    public void sendPasswordResetCodeEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Código de redefinição de senha - My Museum");
        message.setText("Olá!\n\n" +
                "Use o código abaixo para redefinir sua senha:\n\n" +
                code + "\n\n" +
                "Este código expira em 15 minutos.\n\n" +
                "Se você não solicitou esta redefinição, ignore este email.\n\n" +
                "Equipe My Museum");

        send(message);
    }

    private void send(SimpleMailMessage message) {
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailDeliveryException("Falha no envio do email", e);
        }
    }
}
