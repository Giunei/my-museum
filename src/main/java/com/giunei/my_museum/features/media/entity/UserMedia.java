package com.giunei.my_museum.features.media.entity;

import com.giunei.my_museum.features.media.enums.MediaType;
import com.giunei.my_museum.features.media.enums.MediaStatus;
import com.giunei.my_museum.features.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private MediaType type;

    private String externalId;

    private String title;

    private String thumbnail;

    private boolean completed;

    private Integer rating;

    private LocalDate finishedAt;

    private boolean highlighted;

    private Integer displayOrder;

    private Integer pageCount;

    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    private Integer currentSeason;

    private Integer currentEpisode;

    private String author;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "media_collection_user_media",
            joinColumns = @JoinColumn(name = "user_media_id"),
            inverseJoinColumns = @JoinColumn(name = "media_collection_id")
    )
    @Builder.Default
    private Set<MediaCollection> collections = new HashSet<>();

}
