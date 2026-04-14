package com.giunei.my_museum.features.auth.entity;

import com.giunei.my_museum.features.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @ManyToOne
    private User user;
}
