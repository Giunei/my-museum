package com.giunei.my_museum.media.service;

import com.giunei.my_museum.common.security.SecurityUtils;
import com.giunei.my_museum.media.dto.RecentActivityResponse;
import com.giunei.my_museum.media.entity.UserMedia;
import com.giunei.my_museum.media.enums.MediaType;
import com.giunei.my_museum.media.repository.UserMediaRepository;
import com.giunei.my_museum.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentActivityService {

    private final UserMediaRepository userMediaRepository;

    public List<RecentActivityResponse> getRecentActivities(int limit, MediaType type) {
        return getRecentActivities(SecurityUtils.getAuthenticatedUser(), limit, type);
    }

    public List<RecentActivityResponse> getRecentActivities(User user, int limit, MediaType type) {
        List<UserMedia> activities;

        if (type != null) {
            activities = userMediaRepository.findByUserAndTypeAndFinishedAtIsNotNullOrderByFinishedAtDesc(user, type);
        } else {
            activities = userMediaRepository.findByUserAndFinishedAtIsNotNullOrderByFinishedAtDesc(user);
        }

        return activities.stream()
                .limit(limit)
                .map(this::toResponse)
                .toList();
    }

    private RecentActivityResponse toResponse(UserMedia media) {
        return new RecentActivityResponse(
                media.getId(),
                media.getTitle(),
                media.getThumbnail(),
                media.getType(),
                media.getFinishedAt(),
                media.getRating(),
                formatTimeAgo(media.getFinishedAt())
        );
    }

    private String formatTimeAgo(LocalDate date) {
        if (date == null) {
            return null;
        }
        
        LocalDate today = LocalDate.now();
        Period period = Period.between(date, today);
        
        if (period.getYears() > 0) {
            return period.getYears() == 1 ? "1 ano atrás" : period.getYears() + " anos atrás";
        } else if (period.getMonths() > 0) {
            return period.getMonths() == 1 ? "1 mês atrás" : period.getMonths() + " meses atrás";
        } else if (period.getDays() > 0) {
            return period.getDays() == 1 ? "1 dia atrás" : period.getDays() + " dias atrás";
        } else {
            return "Hoje";
        }
    }
}
