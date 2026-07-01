package com.giunei.my_museum.features.series.progress.entity;

import com.giunei.my_museum.core.EntityAbstract;
import com.giunei.my_museum.features.series.catalog.entity.SeriesCatalog;
import com.giunei.my_museum.features.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "series_progress")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"user", "series"})
public class SeriesProgress extends EntityAbstract {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    private SeriesCatalog series;

    @Column
    private Integer lastSeason;

    @Column
    private Integer lastEpisode;
}
