package com.giunei.my_museum.features.user.friendship;

import com.giunei.my_museum.features.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User userRequest;

    @ManyToOne
    private User addressee;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;
}
