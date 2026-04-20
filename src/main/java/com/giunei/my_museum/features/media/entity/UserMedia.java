package com.giunei.my_museum.features.media.entity;

import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId;

    @Enumerated(EnumType.STRING)
    private MediaType type; // BOOK, MOVIE, GAME, SERIES

    private String title;
    private String thumbnail;

    private boolean completed;

    private Integer rating; // 1–5

    private LocalDate finishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
