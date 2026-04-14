package com.giunei.my_museum.features.user.profile.entity;

import com.giunei.my_museum.core.EntityAbstract;
import com.giunei.my_museum.features.user.profile.ProfileTheme;
import com.giunei.my_museum.features.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Profile extends EntityAbstract {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private ProfileTheme theme;

    private String bio;
}
