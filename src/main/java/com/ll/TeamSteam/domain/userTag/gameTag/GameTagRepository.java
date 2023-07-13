package com.ll.TeamSteam.domain.userTag.gameTag;

import com.ll.TeamSteam.domain.user.entity.User;
import com.ll.TeamSteam.domain.userTag.UserTag;
import com.ll.TeamSteam.domain.userTag.gameTag.GameTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameTagRepository extends JpaRepository<GameTag, Long> {
    Optional<GameTag> findByUserTagId(Long id);
    void deleteByUserTag(UserTag userTag);
    Optional<GameTag> findByAppidAndUserTagId(Integer appid, Long userTagId);
}
