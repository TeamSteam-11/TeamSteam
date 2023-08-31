package com.ll.TeamSteam.domain.gameTag.repository;

import com.ll.TeamSteam.domain.gameTag.entity.GameTag;
import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameTagRepository extends JpaRepository<GameTag, Long> {
    Optional<GameTag> findByUserTagId(Long id);
    void deleteByUserTag(UserTag userTag);
    Optional<GameTag> findByAppidAndUserTagId(Integer appid, Long userTagId);
}
