package com.giunei.my_museum.auth.repository;

import com.giunei.my_museum.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.revokedAt IS NULL AND rt.expiresAt > CURRENT_TIMESTAMP ORDER BY rt.createdAt DESC")
    List<RefreshToken> findActiveTokens();
}


