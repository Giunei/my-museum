package com.giunei.my_museum.game.entity;

import com.giunei.my_museum.common.persistence.EntityAbstract;
import com.giunei.my_museum.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
public class SteamAccount extends EntityAbstract {

    @OneToOne
    private User user;

    private String steamId64;

    private String profileUrl;

    private String avatarUrl;

    private String personaName;

    private LocalDateTime lastSyncAt;
}
