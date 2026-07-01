package com.giunei.my_museum.features.user.profile.comment.entity;

import com.giunei.my_museum.core.EntityAbstract;
import com.giunei.my_museum.features.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProfileComment extends EntityAbstract {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_owner_id")
    private User profileOwner;

    @Column(nullable = false, length = 1000)
    private String content;
}
