package com.giunei.my_museum.features.user.entity;

import com.giunei.my_museum.core.EntityAbstract;
import com.giunei.my_museum.features.user.preference.entity.Preference;
import com.giunei.my_museum.features.user.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends EntityAbstract {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private boolean onboardingCompleted = false;


    @OneToOne(mappedBy = "user")
    private Person person;

    @OneToOne(mappedBy = "user", optional = false)
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Preference> preferences;

}