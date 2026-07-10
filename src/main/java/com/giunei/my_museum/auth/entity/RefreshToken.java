package com.giunei.my_museum.auth.entity;

import com.giunei.my_museum.common.persistence.EntityAbstract;
import com.giunei.my_museum.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RefreshToken extends EntityAbstract {

    @Column(unique = true, length = 1024)
    private String tokenHash;

    @NotNull
    @Column
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime revokedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    private User user;

    @Transient
    private String token;

    public boolean isExpired(LocalDateTime now) {
        if (expiresAt == null) {
            return false;
        }
        return expiresAt.isBefore(Objects.requireNonNull(now, "Timestamp for expiration check cannot be null"));
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }
}

