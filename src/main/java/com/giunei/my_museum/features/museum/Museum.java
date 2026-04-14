package com.giunei.my_museum.features.museum;

import com.giunei.my_museum.core.EntityAbstract;
import com.giunei.my_museum.features.user.entity.User;
import com.giunei.my_museum.features.highlight.Highlight;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Museum extends EntityAbstract {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "museum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Highlight> highlights = new ArrayList<>();

    public void addHighlight(Highlight highlight) {
        highlights.add(highlight);
        highlight.setMuseum(this);
    }
}
