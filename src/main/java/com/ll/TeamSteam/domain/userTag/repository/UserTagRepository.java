package com.ll.TeamSteam.domain.userTag.repository;

import com.ll.TeamSteam.domain.userTag.entity.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTagRepository extends JpaRepository<UserTag, Long> {
    Optional<UserTag> findByUserId(Long id);
}
