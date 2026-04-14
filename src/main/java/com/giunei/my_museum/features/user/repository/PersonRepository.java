package com.giunei.my_museum.features.user.repository;

import com.giunei.my_museum.features.user.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
