package com.ll.TeamSteam.domain.user.repository;

import com.ll.TeamSteam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySteamId(String steamId);
    List<User> findTop7ByOrderByTemperatureDesc();
}
