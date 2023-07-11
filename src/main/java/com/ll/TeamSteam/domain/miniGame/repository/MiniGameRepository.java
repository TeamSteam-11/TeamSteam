package com.ll.TeamSteam.domain.miniGame.repository;

import com.ll.TeamSteam.domain.miniGame.entity.MiniGame;
import com.ll.TeamSteam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MiniGameRepository extends JpaRepository<MiniGame, Long> {
    MiniGame findByUser(User user);
    List<MiniGame> findTop5ByOrderByScoreDesc();
}
