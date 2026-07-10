package com.giunei.my_museum.game.repository;

import com.giunei.my_museum.game.entity.SteamAccount;
import com.giunei.my_museum.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SteamAccountRepository extends JpaRepository<SteamAccount, Long> {

    Optional<SteamAccount> findByUser(User user);

    Optional<SteamAccount> findBySteamId64(String steamId64);
}
