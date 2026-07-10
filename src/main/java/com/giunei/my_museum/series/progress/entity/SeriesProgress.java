package com.giunei.my_museum.series.progress.entity;

import com.giunei.my_museum.common.persistence.EntityAbstract;
import com.giunei.my_museum.series.catalog.entity.SeriesCatalog;
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
@Table(name = "series_progress")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"user", "series"})
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
