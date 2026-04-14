package com.giunei.my_museum;

import com.giunei.my_museum.features.highlight.Category;
import com.giunei.my_museum.features.highlight.Highlight;
import com.giunei.my_museum.features.highlight.Review;
import com.giunei.my_museum.features.museum.Museum;
import com.giunei.my_museum.features.user.entity.Gender;
import com.giunei.my_museum.features.user.entity.Person;
import com.giunei.my_museum.features.user.entity.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MockFactory {

    public static Person createPessoa() {
        return Person.builder()
                .name("Juliano")
                .birthDate(LocalDate.of(2000, 1, 1))
                .description("Gosto muito de ler")
                .gender(Gender.MALE)
                .build();
    }

    public static User createUser() {
        return User.builder()
                .username("testuser")
                .password("password")
                .build();
    }

    public static List<Highlight> createHighlights() {
        Highlight h1 = Highlight.builder()
                .name("Narnia VOL1")
                .review(Review.ONE)
                .timeSpent(LocalTime.of(4, 0))
                .category(Category.builder().name("Leitura").photo("fotoDeLivro").build())
                .finished(true)
                .build();

        Highlight h2 = Highlight.builder()
                .name("Narnia VOL2")
                .review(Review.ONE)
                .timeSpent(LocalTime.of(4, 0))
                .category(Category.builder().name("Leitura").photo("fotoDeLivro2").build())
                .finished(true)
                .build();

        return List.of(h1, h2);
    }

    public static Museum createMuseum(User user, List<Highlight> highlights) {
        return Museum.builder()
                .user(user)
                .highlights(highlights)
                .build();
    }
}
