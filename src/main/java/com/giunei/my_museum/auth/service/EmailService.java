package com.giunei.my_museum.auth.service;

import com.giunei.my_museum.common.exception.EmailDeliveryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final String frontendUrl;
    private final String mailFrom;
    private final String resendApiKey;
    private final String resendFrom;
    private final RestClient resendClient;

    public EmailService(
            @Autowired(required = false) JavaMailSender mailSender,
            @Value("${app.frontend.url:http://localhost:4200}") String frontendUrl,
            @Value("${spring.mail.username:}") String mailFrom,
            @Value("${resend.api-key:}") String resendApiKey,
            @Value("${resend.from:My Museum <onboarding@resend.dev>}") String resendFrom
    ) {
        this.mailSender = mailSender;
        this.frontendUrl = frontendUrl;
        this.mailFrom = mailFrom;
        this.resendApiKey = resendApiKey;
        this.resendFrom = resendFrom;
        this.resendClient = createResendClient();
    }

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;
        send(
                toEmail,
                "Verificação de Email - My Museum",
                "Olá!\n\n" +
                        "Clique no link abaixo para verificar seu email:\n" +
                        verificationUrl + "\n\n" +
                        "Este link expira em 24 horas.\n\n" +
                        "Equipe My Museum"
        );
    }

    public void sendPasswordResetCodeEmail(String toEmail, String code) {
        send(
                toEmail,
                "Código de redefinição de senha - My Museum",
                "Olá!\n\n" +
                        "Use o código abaixo para redefinir sua senha:\n\n" +
                        code + "\n\n" +
                        "Este código expira em 15 minutos.\n\n" +
                        "Se você não solicitou esta redefinição, ignore este email.\n\n" +
                        "Equipe My Museum"
        );
    }

    private void send(String toEmail, String subject, String text) {
        if (StringUtils.hasText(resendApiKey)) {
            sendViaResend(toEmail, subject, text);
            return;
        }
        sendViaSmtp(toEmail, subject, text);
    }

    private void sendViaResend(String toEmail, String subject, String text) {
        try {
            resendClient.post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + resendApiKey)
                    .body(Map.of(
                            "from", resendFrom,
                            "to", List.of(toEmail),
                            "subject", subject,
                            "text", text
                    ))
                    .retrieve()
                    .toBodilessEntity();

            log.info("Email enviado via Resend para {}", toEmail);
        } catch (Exception e) {
            log.error("Falha Resend ao enviar email para {}: {}", toEmail, rootCause(e).getMessage(), e);
            throw new EmailDeliveryException("Falha no envio do email", e);
        }
    }

    private void sendViaSmtp(String toEmail, String subject, String text) {
        if (mailSender == null) {
            throw new EmailDeliveryException(
                    "Falha no envio do email: configure RESEND_API_KEY (Railway) ou MAIL_USERNAME/MAIL_PASSWORD (local)",
                    null
            );
        }
        if (!StringUtils.hasText(mailFrom)) {
            throw new EmailDeliveryException(
                    "Falha no envio do email: MAIL_USERNAME não configurado",
                    null
            );
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            log.info("Email enviado via SMTP para {}", toEmail);
        } catch (Exception e) {
            log.error("Falha SMTP ao enviar email para {}: {}", toEmail, rootCause(e).getMessage(), e);
            throw new EmailDeliveryException("Falha no envio do email", e);
        }
    }

    private static RestClient createResendClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .baseUrl("https://api.resend.com")
                .build();
    }

    private static Throwable rootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }
}
