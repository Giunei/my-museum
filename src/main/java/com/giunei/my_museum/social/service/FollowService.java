package com.giunei.my_museum.social.service;

import com.giunei.my_museum.common.exception.BusinessException;
import com.giunei.my_museum.common.exception.UserNotFoundException;
import com.giunei.my_museum.user.repository.UserRepository;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.social.entity.Follow;
import com.giunei.my_museum.social.entity.FollowStatus;
import com.giunei.my_museum.social.dto.FollowRequestResponse;
import com.giunei.my_museum.social.repository.FollowRepository;
import com.giunei.my_museum.profile.FollowRelationStatus;
import com.giunei.my_museum.profile.entity.Profile;
import com.giunei.my_museum.profile.service.ProfileAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ProfileAccessService profileAccessService;

    public int getFollowersCount(User user) {
        return (int) followRepository.countByFollowingAndStatus(user, FollowStatus.ACCEPTED);
    }

    public int getFollowingCount(User user) {
        return (int) followRepository.countByFollowerAndStatus(user, FollowStatus.ACCEPTED);
    }

    @Transactional
    public FollowRelationStatus follow(User follower, String followingUsername) {
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (follower.getId().equals(following.getId())) {
            throw new BusinessException("Você não pode seguir a si mesmo");
        }

        var existingFollow = followRepository.findByFollowerAndFollowing(follower, following);
        if (existingFollow.isPresent()) {
            FollowStatus currentStatus = existingFollow.get().getStatus();
            if (currentStatus == FollowStatus.ACCEPTED) {
                throw new BusinessException("Você já segue este usuário");
            }
            throw new BusinessException("Solicitação de follow já enviada");
        }

        FollowStatus status = profileAccessService.isPrivateProfile(following)
                ? FollowStatus.PENDING
                : FollowStatus.ACCEPTED;

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setStatus(status);
        followRepository.save(follow);

        return status == FollowStatus.ACCEPTED
                ? FollowRelationStatus.ACCEPTED
                : FollowRelationStatus.PENDING;
    }

    @Transactional
    public void unfollow(User follower, String followingUsername) {
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new BusinessException("Você não segue este usuário"));

        followRepository.delete(follow);
    }

    @Transactional
    public void acceptFollowRequest(User profileOwner, String followerUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, profileOwner)
                .orElseThrow(() -> new BusinessException("Solicitação de follow não encontrada"));

        if (follow.getStatus() != FollowStatus.PENDING) {
            throw new BusinessException("Esta solicitação já foi processada");
        }

        follow.setStatus(FollowStatus.ACCEPTED);
        followRepository.save(follow);
    }

    @Transactional
    public void rejectFollowRequest(User profileOwner, String followerUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, profileOwner)
                .orElseThrow(() -> new BusinessException("Solicitação de follow não encontrada"));

        if (follow.getStatus() != FollowStatus.PENDING) {
            throw new BusinessException("Esta solicitação já foi processada");
        }

        followRepository.delete(follow);
    }

    @Transactional(readOnly = true)
    public List<FollowRequestResponse> listPendingRequests(User profileOwner) {
        if (profileOwner.getId() == null) {
            return List.of();
        }

        return followRepository.findPendingIncomingRequests(profileOwner.getId(), FollowStatus.PENDING)
                .stream()
                .map(follow -> toFollowRequestResponse(follow.getFollower()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<FollowRequestResponse> listFollowers(String username, int page, int size) {
        User target = profileAccessService.requireUserWithFollowListsAccess(username);
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        return followRepository
                .findByFollowing_IdAndStatusOrderByIdDesc(
                        target.getId(),
                        FollowStatus.ACCEPTED,
                        PageRequest.of(safePage, safeSize)
                )
                .map(follow -> toFollowRequestResponse(follow.getFollower()));
    }

    @Transactional(readOnly = true)
    public Page<FollowRequestResponse> listFollowing(String username, int page, int size) {
        User target = profileAccessService.requireUserWithFollowListsAccess(username);
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        return followRepository
                .findByFollower_IdAndStatusOrderByIdDesc(
                        target.getId(),
                        FollowStatus.ACCEPTED,
                        PageRequest.of(safePage, safeSize)
                )
                .map(follow -> toFollowRequestResponse(follow.getFollowing()));
    }

    public FollowRelationStatus getFollowRelationStatus(User follower, String followingUsername) {
        User following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        return profileAccessService.resolveFollowRelationStatus(following, follower);
    }

    private FollowRequestResponse toFollowRequestResponse(User user) {
        Profile profile = user.getProfile();
        return new FollowRequestResponse(
                user.getId(),
                user.getUsername(),
                profile != null ? profile.getProfileImageUrl() : null
        );
    }
}
