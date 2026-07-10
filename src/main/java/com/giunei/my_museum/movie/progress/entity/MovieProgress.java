package com.giunei.my_museum.movie.progress.entity;

import com.giunei.my_museum.common.persistence.EntityAbstract;
import com.giunei.my_museum.movie.catalog.entity.MovieCatalog;
import com.giunei.my_museum.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "movie_progress")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"user", "movie"})
public class MovieProgress extends EntityAbstract {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieCatalog movie;

    @Column(nullable = false)
    @Builder.Default
    private Boolean watched = false;
}
