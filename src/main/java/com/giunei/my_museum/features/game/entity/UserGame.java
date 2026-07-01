package com.giunei.my_museum.features.game.entity;

import com.giunei.my_museum.core.converter.StringListConverter;
import com.giunei.my_museum.core.EntityAbstract;
import com.giunei.my_museum.features.game.converter.StoreInfoListConverter;
import com.giunei.my_museum.features.game.dto.StoreInfo;
import com.giunei.my_museum.features.media.enums.MediaStatus;
import com.giunei.my_museum.features.media.entity.UserMedia;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class UserGame extends EntityAbstract {

    @OneToOne
    @JoinColumn(name = "user_media_id")
    private UserMedia media;

    private String steamAppId;

    private Integer playtimeMinutes;

    private Integer achievementsUnlocked;

    private Integer totalAchievements;

    private boolean platinumed;

    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    @Column
    private Long rawgId;

    @Column
    private String name;

    @Convert(converter = StringListConverter.class)
    @Column(length = 1000)
    private List<String> genres;

    @Convert(converter = StringListConverter.class)
    @Column(length = 1000)
    private List<String> platforms;

    @Convert(converter = StoreInfoListConverter.class)
    @Column(length = 2000)
    private List<StoreInfo> stores;
}
