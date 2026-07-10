package com.giunei.my_museum.user.service;

import com.giunei.my_museum.common.exception.NotFoundException;
import com.giunei.my_museum.user.entity.User;
import com.giunei.my_museum.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserLookupService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User requireByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
}
