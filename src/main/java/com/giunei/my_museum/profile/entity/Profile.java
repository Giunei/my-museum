package com.giunei.my_museum.profile.entity;

import com.giunei.my_museum.common.persistence.EntityAbstract;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.profile.ProfileTheme;
import jakarta.persistence.*;
import lombok.Builder;
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

    @Builder.Default
    @Column(name = "private_profile", nullable = false)
    private boolean privateProfile = false;
}
