package com.giunei.my_museum.support;

import com.giunei.my_museum.profile.entity.Profile;
import com.giunei.my_museum.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.recommendation.model.CachedCatalogItem;
import com.giunei.my_museum.user.entity.Gender;
import com.giunei.my_museum.user.entity.Person;
import com.giunei.my_museum.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Set;
public final class TestFixtures {

    private TestFixtures() {
    }

    public static User user(Long id, String username) {
        User user = User.builder()
                .username(username)
                .password("encoded-password")
                .email(username + "@test.com")
                .emailVerified(true)
                .build();
        setId(user, id);
        return user;
    }

    public static User userWithProfile(Long id, String username, boolean privateProfile) {
        User user = user(id, username);
        Profile profile = Profile.builder()
                .user(user)
                .privateProfile(privateProfile)
                .build();
        setId(profile, id);
        user.setProfile(profile);
        return user;
    }

    public static Person person() {
        return Person.builder()
                .name("Juliano")
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .build();
    }

    public static User userWithProfileAndPerson(Long id, String username, boolean privateProfile) {
        User user = userWithProfile(id, username, privateProfile);
        Person person = person();
        person.setUser(user);
        user.setPerson(person);
        return user;
    }

    public static CachedCatalogItem catalogItem(String title) {
        return new CachedCatalogItem(
                1L,
                title,
                "Creator",
                EditorialCategory.CLASSIC,
                Set.of("Drama")
        );
    }

    private static void setId(Object entity, Long id) {
        ReflectionTestUtils.setField(entity, "id", id);
    }
}
