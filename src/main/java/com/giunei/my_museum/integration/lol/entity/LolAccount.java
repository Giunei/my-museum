package com.giunei.my_museum.integration.lol.entity;

import com.giunei.my_museum.common.persistence.EntityAbstract;
import com.giunei.my_museum.integration.lol.enums.LolPlatform;
import com.giunei.my_museum.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lol_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class LolAccount extends EntityAbstract {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String puuid;

    @Column(name = "game_name", nullable = false, length = 100)
    private String gameName;

    @Column(name = "tag_line", nullable = false, length = 20)
    private String tagLine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LolPlatform platform;

    @Column(name = "solo_tier", length = 20)
    private String soloTier;

    @Column(name = "solo_rank", length = 5)
    private String soloRank;

    private Integer soloLeaguePoints;

    private Integer soloWins;

    private Integer soloLosses;

    @Column(name = "flex_tier", length = 20)
    private String flexTier;

    @Column(name = "flex_rank", length = 5)
    private String flexRank;

    private Integer flexLeaguePoints;

    private Integer flexWins;

    private Integer flexLosses;

    private LocalDateTime lastRankRefreshAt;
}
