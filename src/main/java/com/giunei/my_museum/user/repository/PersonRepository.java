package com.giunei.my_museum.user.repository;

import com.giunei.my_museum.user.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByUserId(Long userId);
}
