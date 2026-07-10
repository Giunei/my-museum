package com.giunei.my_museum.integration.lol.repository;

import com.giunei.my_museum.integration.lol.entity.LolAccount;
import com.giunei.my_museum.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LolAccountRepository extends JpaRepository<LolAccount, Long> {

    Optional<LolAccount> findByUser(User user);

    Optional<LolAccount> findByUserId(Long userId);
}
