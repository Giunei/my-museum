package com.giunei.my_museum.home.service;

import com.giunei.my_museum.achievement.repository.UserAchievementRepository;
import com.giunei.my_museum.home.dto.HomeStatisticsResponse;
import com.giunei.my_museum.home.dto.PopularProfileResponse;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.user.repository.UserRepository;
import com.giunei.my_museum.user.entity.Person;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.social.repository.FollowRepository;
import com.giunei.my_museum.profile.entity.Profile;
import com.giunei.my_museum.profile.repository.ProfileRepository;
import com.giunei.my_museum.user.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final UserAchievementRepository userAchievementRepository;
    private final UserMediaRepository userMediaRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final PersonRepository personRepository;

    public HomeStatisticsResponse getStatistics() {
        long totalAchievementsUnlocked = userAchievementRepository.count();
        long totalRatedItems = userMediaRepository.countByRatingIsNotNull();
        long totalUsers = userRepository.count();

        return new HomeStatisticsResponse(totalAchievementsUnlocked, totalRatedItems, totalUsers);
    }

    public List<PopularProfileResponse> getPopularProfiles() {
        List<Object[]> mostFollowed = followRepository.findMostFollowedUsers();

        return IntStream.range(0, Math.min(5, mostFollowed.size()))
                .mapToObj(index -> {
                    Object[] row = mostFollowed.get(index);
                    User user = (User) row[0];
                    long followersCount = (long) row[1];

                    String profileImageUrl = profileRepository.findByUserId(user.getId())
                            .map(Profile::getProfileImageUrl)
                            .orElse(null);

                    String name = personRepository.findByUserId(user.getId())
                            .map(Person::getName)
                            .orElse(null);

                    String bio = profileRepository.findByUserId(user.getId())
                            .map(Profile::getBio)
                            .orElse(null);

                    long achievementsCount = userAchievementRepository.countByUser(user);
                    long ratingsCount = userMediaRepository.countByUserAndRatingIsNotNull(user);

                    return new PopularProfileResponse(
                            user.getId(),
                            user.getUsername(),
                            profileImageUrl,
                            name,
                            bio,
                            followersCount,
                            achievementsCount,
                            ratingsCount,
                            index + 1
                    );
                })
                .toList();
    }
}
