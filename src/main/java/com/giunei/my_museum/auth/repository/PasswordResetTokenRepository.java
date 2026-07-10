package com.giunei.my_museum.auth.repository;

import com.giunei.my_museum.auth.entity.PasswordResetToken;
import com.giunei.my_museum.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
