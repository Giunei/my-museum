package com.giunei.my_museum.features.achievement.controller;

import com.giunei.my_museum.features.achievement.dto.UserGoalRequest;
import com.giunei.my_museum.features.achievement.dto.UserGoalResponse;
import com.giunei.my_museum.features.achievement.dto.UserGoalUpdateRequest;
import com.giunei.my_museum.features.achievement.service.UserGoalService;
import com.giunei.my_museum.features.media.enums.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class UserGoalController {

    private final UserGoalService service;

    @PostMapping
    public UserGoalResponse create(@RequestBody UserGoalRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public UserGoalResponse update(
            @PathVariable Long id,
            @RequestBody UserGoalUpdateRequest request
    ) {
        return service.update(id, request);
    }

    @GetMapping
    public List<UserGoalResponse> findMyGoals(
            @RequestParam(required = false) MediaType type
    ) {
        return service.findMyGoals(type);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
