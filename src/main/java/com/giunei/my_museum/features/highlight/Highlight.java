package com.giunei.my_museum.features.highlight;

import com.giunei.my_museum.core.EntityAbstract;
import com.giunei.my_museum.features.museum.Museum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Highlight extends EntityAbstract {

    @ManyToOne
    private Category category;

    private String name;

    private LocalTime timeSpent;

    private Boolean finished;

    private Boolean platinumed;

    @Enumerated(EnumType.STRING)
    private Review review;

    @ManyToOne
    @JoinColumn(name = "museum_id")
    private Museum museum;
}
